package com.wxh.shareiot.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wxh.basiclib.base.BaseActivity;
import com.wxh.basiclib.utils.LogUtil;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.CreateRechargeOrderRequest;
import com.wxh.shareiot.api.CreateRechargeOrderResponse;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.api.UserBalanceResponse;
import com.wxh.shareiot.api.WechatResponse;
import com.wxh.shareiot.api.ZfbPayRequest;
import com.wxh.shareiot.api.ZfbResponse;
import com.wxh.shareiot.databinding.ActivityRechargeBalanceBinding;
import com.wxh.shareiot.socket.JWebSocketClientService;
import com.wxh.shareiot.utils.AliPayResult;
import com.wxh.shareiot.utils.Constant;
import com.wxh.shareiot.viewmodel.BalanceViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

public class BalanceActivity extends BaseActivity<BalanceViewModel, ActivityRechargeBalanceBinding> {
    private static final int SDK_PAY_FLAG = 1;
    private int payType;//0-支付宝 1-微信 2-贝宝
    private ChatMessageReceiver chatMessageReceiver;
    private String orderId;

    @Override
    protected int getLayout() {
        return R.layout.activity_recharge_balance;
    }

    @Override
    protected void initView(Bundle bundle) {
        dataBinding.tvHistory.setOnClickListener(view -> {
            ActivityUtils.startActivity(BalanceListActivity.class);
        });

        dataBinding.topAppBar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected void initData() {
        viewModel.getUserBalance();
        viewModel.getBalanceResult().observe(this, new Observer<UserBalanceResponse>() {
            @Override
            public void onChanged(UserBalanceResponse userBalanceResponseMyResult) {
                if (userBalanceResponseMyResult.getUserBalance() == null) {
                    dataBinding.tvBalance.setText("0.00");
                    dataBinding.et.setText("");
                } else {
                    int balance = userBalanceResponseMyResult.getUserBalance().getBalance();
                    dataBinding.tvBalance.setText(String.format("%.2f", balance / 100f));
                    dataBinding.et.setText("");
                }
            }
        });
        viewModel.getZfbResult().observe(this, new Observer<ZfbResponse>() {
            @Override
            public void onChanged(ZfbResponse zfbResponseMyResult) {
                String orderInfo = zfbResponseMyResult.getZfbResponse();
                alipayV2(orderInfo);
            }
        });

        viewModel.getWechatResult().observe(this, new Observer<WechatResponse>() {
            @Override
            public void onChanged(WechatResponse wechatResponseMyResult) {
                try {
                    wechatPayV3(wechatResponseMyResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        viewModel.getRechargeOrderResult().observe(this, new Observer<CreateRechargeOrderResponse>() {
            @Override
            public void onChanged(CreateRechargeOrderResponse createRechargeOrderResponseMyResult) {
                orderId = createRechargeOrderResponseMyResult.getOrderId();
                if (payType == 0) {
                    //支付宝支付
                    zfbPay();
                } else if (payType == 1) {
                    //微信支付
                    wechatPay();
                } else if (payType == 2) {
                    //贝宝支付
                    Intent intent = new Intent(getApplicationContext(), PayPalActivity.class);
                    intent.putExtra("orderId", orderId);
                    startActivity(intent);
                }
            }
        });

        dataBinding.llRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money = dataBinding.et.getText().toString();
                int amount = (int) (Double.parseDouble(money) * 100);
                //  创建充值订单
                int platform = 0;
                if (payType == 0) {
                    //支付宝支付
                    platform = 1;
                } else if (payType == 1) {
                    //微信支付
                    platform = 0;
                } else if (payType == 2) {
                    //贝宝支付
                    platform = 2;
                }
                CreateRechargeOrderRequest request = new CreateRechargeOrderRequest();
                request.setAmount(amount);
                request.setPlatform(platform);
                viewModel.createRechargeOrder(request);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            viewModel.getUserBalance();
        }
    }

    @Override
    protected BalanceViewModel initViewModel() {
        return ViewModelProviders.of(this).get(BalanceViewModel.class);
    }

    @Override
    protected void showError(Object o) {
        if (o instanceof Error) {
            Error error = (Error) o;
            switch (error.getCode()) {
                case 401:
                    ToastUtils.showShort(error.getMsg());
                    //令牌失效
                    stopService(new Intent(this, JWebSocketClientService.class));
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    ActivityUtils.finishAllActivities();
                    break;
                case 403:
                    ToastUtils.showShort("无权访问");
                    break;
                case 504:
                    ToastUtils.showShort("timeout");
                    break;
                default:
                    ToastUtils.showShort(((Error) o).getMsg());
                    break;
            }
        }
    }

    /**
     * websocket消息接收及处理
     */
    private class ChatMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            LogUtils.e(message);
            //deal message，支付成功的消息
            try {
                JSONObject object = new JSONObject(message);
                if (object.getInt("msgType") == 0) {//文本内容
                    //收到推送过来的骑行编号
                    if (object.getString("bizCode").equals("paySuccess")) {
                        String content = object.getString("content");
                        if (orderId.equals(content)) {
                            //支付成功
                            ToastUtils.showShort(R.string.pay_success);
                            viewModel.getUserBalance();
//                            Intent intent1;
//                            if (paymentType != null && (paymentType.equals("order") || paymentType.equals("recharge"))) {
//                                intent1 = new Intent();
//                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                BalanceActivity.this.setResult(RESULT_OK, intent1);
//                            } else {
//                                intent1 = new Intent(PaymentActivity.this, MapsActivity.class);
//                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent1);
//                            }
//                            finish();
                        } else {
                            // ToastUtils.showShort("骑行编号不一致");
                        }
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 向服务器发送支付宝支付请求
     */
    private void zfbPay() {
        ZfbPayRequest request = new ZfbPayRequest();
        request.setOrderId(orderId);
        request.setSubject("溧电");
        request.setBody("充值");
        viewModel.zfbPay(request);
    }

    /**
     * 向服务器发送微信支付请求
     */
    private void wechatPay() {
        ZfbPayRequest request = new ZfbPayRequest();
        request.setOrderId(orderId);
        request.setSubject("溧电");
        request.setBody("充值");
        viewModel.wechatPay(request);
    }

    /**
     * 支付宝支付业务示例
     */
    public void alipayV2(String orderInfo) {
        //orderInfo来自服务器
        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(BalanceActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 调用微信支付
     */
    public void wechatPayV3(WechatResponse response) throws NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constant.WECHAT_APP_ID, true);

        PayReq request = new PayReq();
        //应用ID
        request.appId = response.getAppid();
        //商户号
        request.partnerId = response.getPartnerid();
        //预支付交易会话ID
        request.prepayId = response.getPrepay_id();
        //
        request.packageValue = "Sign=WXPay";
        //随机字符串
        request.nonceStr = response.getNoncestr();
        //时间戳，10位数字,到s
        request.timeStamp = response.getTimestamp();
        //签名，使用字段appId、timeStamp、nonceStr、prepayid
        //request.sign = WechatUtil.sign(this, request.appId, request.timeStamp, request.nonceStr, prepayId, "apiclient_key.pem");
        request.sign = response.getServerSign();
        api.sendReq(request);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case SDK_PAY_FLAG: {

                    AliPayResult payResult = new AliPayResult((Map<String, String>) message.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        LogUtil.d("支付成功");
                        viewModel.getUserBalance();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        LogUtil.d("支付失败" + payResult.getResult());
                    }
                    break;
                }
                default:
                    break;
            }
            return false;
        }
    });
}
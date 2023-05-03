package com.wxh.shareiot.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wxh.basiclib.base.BaseActivity;
import com.wxh.basiclib.utils.LogUtil;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.BalancePayRequest;
import com.wxh.shareiot.api.CreatePayOrderBean;
import com.wxh.shareiot.api.CreateRechargeOrderRequest;
import com.wxh.shareiot.api.CreateRechargeOrderResponse;
import com.wxh.shareiot.api.EmptyBean;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.api.UserBalanceResponse;
import com.wxh.shareiot.api.WechatResponse;
import com.wxh.shareiot.api.ZfbPayRequest;
import com.wxh.shareiot.api.ZfbResponse;
import com.wxh.shareiot.databinding.ActivityPaymentBinding;
import com.wxh.shareiot.socket.JWebSocketClientService;
import com.wxh.shareiot.utils.AliPayResult;
import com.wxh.shareiot.utils.Constant;
import com.wxh.shareiot.utils.MyRadioGroup;
import com.wxh.shareiot.utils.NumberUtil;
import com.wxh.shareiot.utils.UserUtil;
import com.wxh.shareiot.viewmodel.PayViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

public class PaymentActivity extends BaseActivity<PayViewModel, ActivityPaymentBinding> {

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private CreatePayOrderBean.PayOrder order;
    private int payType;//0-支付宝 1-微信 2-贝宝
    private ChatMessageReceiver chatMessageReceiver;
    private String paymentType;//标记从哪个页面进的
    private String orderId;
    private int amount;

    @Override
    protected int getLayout() {
        return R.layout.activity_payment;
    }

    @Override
    protected void initView(Bundle bundle) {
        dataBinding.topAppBar.setNavigationOnClickListener(view -> {
            if (getIntent().getStringExtra("type").equals("order")) {
                finish();
            } else {
//                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        dataBinding.llPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentType != null && paymentType.equals("recharge")) {
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
                } else {
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
                    } else if (payType == 3) {
                        //余额支付
                        BalancePayRequest request = new BalancePayRequest();
                        request.setOrderId(orderId);
                        viewModel.balancePay(request);
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        paymentType = getIntent().getStringExtra("type");
        if (paymentType != null && paymentType.equals("recharge")) {
            //余额充值过来的
            amount = getIntent().getIntExtra("amount", 0);
            dataBinding.tvMoney.setText(String.format("%s", NumberUtil.div(amount, 100, 2)));
            dataBinding.rlBalance.setVisibility(View.GONE);
        } else {
            viewModel.getUserBalance();
            if (paymentType == null || !paymentType.equals("order")) {
                UserUtil.cleanBikeData();
            }
            order = new Gson().fromJson(getIntent().getStringExtra("order"), CreatePayOrderBean.PayOrder.class);
            orderId = order.getOrderId();
            dataBinding.tvMoney.setText(String.format("%s", NumberUtil.div(order.getAmount(), 100, 2)));
        }
        dataBinding.rgPay.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_alipay) {
                    payType = 0;
                } else if (checkedId == R.id.rb_wechatpay) {
                    payType = 1;
                } else if (checkedId == R.id.rb_paypal) {
                    payType = 2;
                } else if (checkedId == R.id.rb_balance) {
                    payType = 3;
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
        viewModel.getBalancePayResult().observe(this, new Observer<EmptyBean>() {
            @Override
            public void onChanged(EmptyBean emptyBeanMyResult) {
                ToastUtils.showShort(R.string.pay_success);
                Intent intent1;
                if (paymentType != null && paymentType.equals("order")) {
                    intent1 = new Intent();
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PaymentActivity.this.setResult(RESULT_OK, intent1);
                } else {
                    intent1 = new Intent(PaymentActivity.this, MapsActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }
                finish();
            }
        });

        viewModel.getBalanceResult().observe(this, new Observer<UserBalanceResponse>() {
            @Override
            public void onChanged(UserBalanceResponse userBalanceResponseMyResult) {
                if (userBalanceResponseMyResult.getUserBalance() == null) {
                    dataBinding.tvBalance.setText("0.00");
                } else {
                    int balance = userBalanceResponseMyResult.getUserBalance().getBalance();
                    dataBinding.tvBalance.setText(String.format("%.2f", balance / 100f));
                    if (balance > 0) {
                        dataBinding.rbBalance.setEnabled(true);
                    }
                }
            }
        });

        doRegisterReceiver();
    }

    /**
     * 向服务器发送支付宝支付请求
     */
    private void zfbPay() {
        ZfbPayRequest request = new ZfbPayRequest();
        request.setOrderId(orderId);
        request.setSubject("溧电");
        request.setBody("哈哈哈哈");
        viewModel.zfbPay(request);
    }

    /**
     * 向服务器发送微信支付请求
     */
    private void wechatPay() {
        ZfbPayRequest request = new ZfbPayRequest();
        request.setOrderId(orderId);
        request.setSubject("溧电");
        request.setBody("哈哈哈哈");
        viewModel.wechatPay(request);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {

                    AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        LogUtil.d("支付成功");
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        LogUtil.d("支付失败" + payResult.getResult());
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 支付宝支付业务示例
     */
    public void alipayV2(String orderInfo) {
        //orderInfo来自服务器
        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PaymentActivity.this);
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

    @Override
    protected PayViewModel initViewModel() {
        return ViewModelProviders.of(this).get(PayViewModel.class);
    }

    @Override
    protected void showError(Object o) {
        if(o instanceof Error){
            Error error = (Error)o;
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
                        if ((order != null && order.getCycleId().equals(content)) || orderId.equals(content)) {
                            //支付成功
                            ToastUtils.showShort(R.string.pay_success);
                            Intent intent1;
                            if (paymentType != null && (paymentType.equals("order") || paymentType.equals("recharge"))) {
                                intent1 = new Intent();
                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                PaymentActivity.this.setResult(RESULT_OK, intent1);
                            } else {
                                intent1 = new Intent(PaymentActivity.this, MapsActivity.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent1);
                            }
                            finish();
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

    public void wxPaySuccess(PayResp payResp) {
        ToastUtils.showShort("支付成功");
    }

    @Override
    public void onBackPressed() {
        if (paymentType == null) {
//            Intent intent = new Intent(PaymentActivity.this, MapsActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);

            Intent intent = new Intent(PaymentActivity.this, MapsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    /**
     * 动态注册广播
     */
    private void doRegisterReceiver() {
        chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter(Constant.WEBSOCKET_SERVICE_ACTION);
        registerReceiver(chatMessageReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        LogUtil.e("ondestroy");
        unregisterReceiver(chatMessageReceiver);
        super.onDestroy();
    }
}
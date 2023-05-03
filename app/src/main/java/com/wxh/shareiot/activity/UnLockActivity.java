package com.wxh.shareiot.activity;

import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.response.BleMtuResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.wxh.basiclib.base.BaseActivity;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.EBikeDetail;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.ble.BleDataConvertUtil;
import com.wxh.shareiot.ble.BleUtils;
import com.wxh.shareiot.ble.CRC8Util;
import com.wxh.shareiot.ble.ClientManager;
import com.wxh.shareiot.ble.Device;
import com.wxh.shareiot.databinding.ActivityUnLockBinding;
import com.wxh.shareiot.socket.JWebSocketClientService;
import com.wxh.shareiot.utils.CommonUtil;
import com.wxh.shareiot.utils.Constant;
import com.wxh.shareiot.utils.UserUtil;
import com.wxh.shareiot.viewmodel.LockViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * 由于蓝牙连接过慢，解锁改为4G开锁
 */
public class UnLockActivity extends BaseActivity<LockViewModel, ActivityUnLockBinding> {
    private static final int UNLOCK_TIMEOUT = 0x11;
    private static final long UNLOCK_TIMEOUT_VALUE = 30 * 1000;
    private BluetoothClient bluetoothClient;
    private byte messageKey;
    private String ebikeId;
    private String mac;
    private boolean isBleConnect;
    private ChatMessageReceiver chatMessageReceiver;

    @Override
    protected int getLayout() {
        return R.layout.activity_un_lock;
    }

    @Override
    protected void initView(Bundle bundle) {
        dataBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
        dataBinding.btOpen.setOnClickListener(view -> {
            dataBinding.openLoading.setVisibility(View.VISIBLE);
            dataBinding.rotateloading.start();
//
            if (isBleConnect && bluetoothClient.isBluetoothOpened()) {
                //向服务器申请开锁
                viewModel.btUnlockApply(ebikeId);
            } else {
            viewModel.iot4GUnlock(ebikeId);
            }

        });
    }

    @Override
    public void onBackPressed() {
        back();
        super.onBackPressed();
    }

    @Override
    protected void initData() {
        doRegisterReceiver();
        bluetoothClient = ClientManager.getClient(getApplicationContext());
        setDataObserve();
        LogUtils.e(getIntent().getStringExtra("code"));
        ebikeId = getIntent().getStringExtra("code");
        try {
            viewModel.getEbikeDetail(ebikeId);
            viewModel.getEbikeAgreement(ebikeId);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 设置数据观察者
     */
    private void setDataObserve() {
        viewModel.getEbikeDetailResult().observe(this, detailResponseMyResult -> {
                EBikeDetail detail = detailResponseMyResult.getIotEbike();
                setDataView(detail);
                mac = detail.getMac();
                //拿到设备mac地址，如果蓝牙开着，连接蓝牙
                if (bluetoothClient.isBluetoothOpened()) {
                    connect();
                }
                dataBinding.btOpen.setEnabled(true);
        });

//        viewModel.getAgreeMentResult().observe(this, new Observer<MyResult<Km5sSettingData>>() {
//            @Override
//            public void onChanged(MyResult<Km5sSettingData> km5sSettingDataMyResult) {
//                if (km5sSettingDataMyResult.getCode() == 200) {
//                    if (km5sSettingDataMyResult.getData().getControllerAgreement() != null) {
//                        //当前车为5s协议
//                        KM5SBluetoothData.settingValue = km5sSettingDataMyResult.getData().getControllerAgreement().getData();
//                        UserUtil.protocolType = 0;
//                    }
//                } else if (km5sSettingDataMyResult.getCode() == 400) {
//                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    UserUtil.protocolType = 1;
//                    //ToastUtils.showShort(km5sSettingDataMyResult.getErrorMsg());
//                }
//            }
//        });

        viewModel.getBtApplyResult().observe(this, emptyBeanMyResult -> {
                //发送开锁指令
                sendUnLockMessage();
                //等待推送过来的开锁消息
                startRequestTiming();
        });

        viewModel.getIotUnlockResult().observe(this, emptyBeanMyResult -> {
                //等待推送过来的开锁消息
                startRequestTiming();
        });
    }

    private BleConnectStatusListener statusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String s, int status) {
            switch (status) {
                case STATUS_CONNECTED:
                    isBleConnect = true;
                    bluetoothClient.requestMtu(mac, 200, new BleMtuResponse() {
                        @Override
                        public void onResponse(int i, Integer integer) {
                            LogUtils.e(integer.toString());
                        }
                    });
                    LogUtils.e("蓝牙连接成功");
                    break;
                case STATUS_DISCONNECTED:
                    isBleConnect = false;
                    LogUtils.e("蓝牙断开连接");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 设置页面数据
     *
     * @param detail
     */
    private void setDataView(EBikeDetail detail) {
        mac = detail.getMac();
        dataBinding.tvCode.setText(detail.getEbikeCode());
        dataBinding.tvSoc.setText(String.valueOf(detail.getSoc()));
    }

    private void connect() {
        if (!BleUtils.deviceMap.containsKey(mac.toUpperCase())) {
            // ToastUtils.showShort("未发现设备");
            // setResult(2);
            // finish();
        } else {
            ClientManager.setSeriveUUID("00001802-0000-1000-8000-00805f9b34fb");
            ClientManager.setNotifyUUID("00002a06-0000-1000-8000-00805f9b34fb");
            ClientManager.setWriteUUID("00002a06-0000-1000-8000-00805f9b34fb");
            //得到正确的设备
            Device device = BleUtils.deviceMap.get(mac.toUpperCase());
            if (device == null) {
                return;
            }

            BleUtils.connect(bluetoothClient, device, statusListener, new BleUtils.ConnectResult() {
                @Override
                public void success() {
//                    sendKeyMessage();

                    BleUtils.indicateBle(bluetoothClient, new BleNotifyResponse() {
                        @Override
                        public void onNotify(UUID service, UUID character, byte[] value) {
                            if (value.length < 3) {
                                return;
                            }
                            if (value[3] == 0x01) {//通讯秘钥
//                            ToastUtils.showShort(BleDataConvertUtil.byte2hex(value));
                                //保存秘钥
                                dealKeyResponse(value);
                                //开锁
                                // sendUnLockMessage();
                            } else if (value[3] == 0x05) {
                                dealUnLockRsponse(value);
                            }

                            LogUtils.e("接收到--" + BleDataConvertUtil.byte2hex(value));
                        }

                        @Override
                        public void onResponse(int code) {

                        }
                    });
                }

                @Override
                public void fail() {
                    ToastUtils.showShort(R.string.connect_fail);
                }
            });
        }
    }

    /**
     * 用来计时，判断消息是否有回复
     */
    protected Handler mHandler = new Handler(Looper.myLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            dismissDialog();
            ToastUtils.showShort(R.string.unlock_fail_retry);
            setResult(2);
            finish();
            return false;
        }
    });

    protected void startRequestTiming() {
        mHandler.sendEmptyMessageDelayed(UNLOCK_TIMEOUT, UNLOCK_TIMEOUT_VALUE);
    }

    protected void stopRequestTiming() {
        dismissDialog();
        mHandler.removeMessages(UNLOCK_TIMEOUT);
    }

    /**
     * 获取key指令
     */
    private void sendKeyMessage() {
        byte[] data = new byte[14];
        data[0] = (byte) 0xa1;
        data[1] = 10;
        data[2] = 10;
        data[3] = 0x01;
        //8位设备key
        String key = CommonUtil.genSecret(ClientManager.getDevice().getMac());
        byte[] keys = BleDataConvertUtil.HexString2Bytes(key);
        for (int i = 0; i < keys.length; i++) {
            data[4 + i] = keys[i];
        }
        int len = keys.length;
        data[4 + len] = CRC8Util.calcCrc8(data);
        data[5 + len] = (byte) 0xD1;
        BleUtils.writeBle(bluetoothClient, data, writeResponse);
    }

    /**
     * 开锁指令
     */
    private void sendUnLockMessage() {
        byte[] data = new byte[7];
        data[0] = (byte) 0xa1;
        data[1] = 3;
        data[2] = messageKey;
        data[3] = 0x05;
        data[4] = 0x01;
        data[5] = CRC8Util.calcCrc8(data);
        data[6] = (byte) 0xD1;
        BleUtils.writeBle(bluetoothClient, data, writeResponse);
    }

    /**
     * 处理获取到的通信key
     *
     * @param data
     */
    private void dealKeyResponse(byte[] data) {
        dataBinding.btOpen.setEnabled(true);
        messageKey = data[2];
    }


    /**
     * 解锁指令返回
     *
     * @param data
     */
    private void dealUnLockRsponse(byte[] data) {
        if (data[2] == messageKey) {
            //TODO
            int result = data[4];//1：成功 2：失败或超时
            if (result == 1) {
                BleUtils.unIndicateBle(bluetoothClient, new BleUnnotifyResponse() {
                    @Override
                    public void onResponse(int i) {
                    }
                });
//                goMap();
            }
        } else {
            //todo 开锁失败
            setResult(2);
            finish();
        }
    }

    BleWriteResponse writeResponse = new BleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

    @Override
    protected void onPause() {
        dataBinding.rotateloading.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(chatMessageReceiver);
        super.onDestroy();
    }

    @Override
    protected LockViewModel initViewModel() {
        return ViewModelProviders.of(this).get(LockViewModel.class);
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
     * 动态注册广播
     */
    private void doRegisterReceiver() {
        chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter(Constant.WEBSOCKET_SERVICE_ACTION);
        registerReceiver(chatMessageReceiver, filter);
    }

    /**
     * websocket消息接收及处理
     */
    private class ChatMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            LogUtils.e(message);
            //todo deal message
            JSONObject object = null;
            try {
                object = new JSONObject(message);
                if (object.getInt("msgType") == 0) {//文本内容
                    //收到推送过来的骑行编号
                    if (object.getString("bizCode").equals("cycleId")) {
                        stopRequestTiming();
                        UserUtil.cycleId = object.getString("content");
                        goMap();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void goMap() {
        UserUtil.ebikeId = ebikeId;
        Intent intent = new Intent();
//        intent.putExtra("key", messageKey);
        intent.putExtra("bleConnect", isBleConnect);
        setResult(100, intent);
        finish();
    }

    private void back() {
        if (isBleConnect && bluetoothClient.isBluetoothOpened()) {
            BleUtils.unRegisterConnectStatus(bluetoothClient, statusListener);
            BleUtils.disConnect(bluetoothClient);
            ClientManager.setDevice(null);
        }
        finish();
    }
}
package com.wxh.shareiot.ble;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.wxh.shareiot.utils.Constant;

import java.util.UUID;

public class BleNotifyService extends Service {
    @Override
    public void onCreate() {
//        super.onCreate();
        //notify

    }

    public void notifyBle() {
        BleUtils.notifyBle(ClientManager.getClient(this), new BleNotifyResponse() {
            @Override
            public void onNotify(UUID uuid, UUID uuid1, byte[] bytes) {
                if (uuid == ClientManager.getservice().getUUID() && uuid1 == ClientManager.getNotifyCharacter().getUuid()) {
                    if (bytes.length > 4) {
                        Intent intent = new Intent();
                        intent.setAction(Constant.NOTIFY_SERVICE_ACTION);
                        intent.putExtra("message", bytes);
                        sendBroadcast(intent);
                    }
                }
            }

            @Override
            public void onResponse(int i) {

            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        BleUtils.unNotifyBle(ClientManager.getClient(this), new BleUnnotifyResponse() {
            @Override
            public void onResponse(int i) {
                
            }
        });
        super.onDestroy();
    }
}

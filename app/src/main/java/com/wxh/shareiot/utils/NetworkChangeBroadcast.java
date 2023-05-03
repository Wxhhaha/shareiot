package com.wxh.shareiot.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.wxh.basiclib.utils.LogUtil;

public class NetworkChangeBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NetworkChangeBroadcast", "network changed!");

        boolean isNetworkAvailable = false, isWifiAvailable = false, isMobileAvailable = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            isNetworkAvailable = networkInfo.isAvailable();
            isWifiAvailable = networkInfo.isAvailable() && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
            isMobileAvailable = networkInfo.isAvailable() && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
        }

        if (isNetworkAvailable) {
            if (isWifiAvailable) {
                LogUtil.d("当前网络状态：Wifi 可用");
            } else if (isMobileAvailable) {
                LogUtil.d("当前网络状态：移动网络可用");
            }
        } else {
            LogUtil.d("当前网络状态：网络不可用");
        }
    }
}

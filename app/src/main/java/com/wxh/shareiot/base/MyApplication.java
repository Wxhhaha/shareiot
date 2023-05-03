package com.wxh.shareiot.base;

import com.blankj.utilcode.util.LogUtils;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.wxh.basiclib.base.BaseApplication;
import com.wxh.basiclib.http.HttpManager;
import com.wxh.shareiot.ble.BleUtils;
import com.wxh.shareiot.ble.ClientManager;
import com.wxh.shareiot.utils.Urls;

import java.util.HashMap;

/**
 * 创建时间:2021/12/10 15:06
 * 作者:wxh
 */
public class MyApplication extends BaseApplication{

    @Override
    public void setBaseUrl() {
        HttpManager.setBaseUrl(Urls.HTTP_URL);
    }

    @Override
    public void showLog() {
        HttpManager.setShowLog(false);
    }

    @Override
    public void configAutoSize() {
       // AutoSizeConfig.getInstance().getUnitsManager().setSupportSubunits(Subunits.MM);
    }

    @Override
    public void create() {
        ClientManager.setSeriveUUID("0000ffe0-0000-1000-8000-00805f9b34fb");
        ClientManager.setNotifyUUID("0000ffe1-0000-1000-8000-00805f9b34fb");
        ClientManager.setWriteUUID("0000ffe1-0000-1000-8000-00805f9b34fb");
        BleUtils.setBluetooth(this);
        initX5();
    }

    private void initX5(){
        HashMap<String,Object> map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);

        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                // 内核初始化完成，可能为系统内核，也可能为系统内核
            }

            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖网络动态下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             * @param isX5 是否使用X5内核
             */
            @Override
            public void onViewInitFinished(boolean isX5) {

            }
        });
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                LogUtils.e("下载完成");
            }

            @Override
            public void onInstallFinish(int i) {
                LogUtils.e("正在安装"+i);
            }

            @Override
            public void onDownloadProgress(int i) {
                LogUtils.e("下载下载"+i);
            }
        });
    }

}

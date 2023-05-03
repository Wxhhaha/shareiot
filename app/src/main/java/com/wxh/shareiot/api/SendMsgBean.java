package com.wxh.shareiot.api;

/**
 * 创建时间:2022/1/26 11:01
 * 作者:wxh
 */
public class SendMsgBean {
    private String deviceId;
    private String requestId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

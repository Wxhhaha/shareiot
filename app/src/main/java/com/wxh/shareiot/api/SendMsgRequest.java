package com.wxh.shareiot.api;

/**
 * 创建时间:2022/1/26 11:00
 * 作者:wxh
 */
public class SendMsgRequest {
    private String deviceId;
    private String msg;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

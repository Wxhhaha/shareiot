package com.wxh.shareiot.api;

/**
 * 创建时间:2022/1/24 15:37
 * 作者:wxh
 */
public class LoginRequest {
    private String loginName;
    private String loginPwd;
    private int clientType;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }
}

package com.wxh.shareiot.api;

public class CreateRechargeOrderRequest {
    private int amount;
    private int platform;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }
}

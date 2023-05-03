package com.wxh.shareiot.api;

public class UserBalanceResponse {

    private UserBalanceDTO userBalance;

    public UserBalanceDTO getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(UserBalanceDTO userBalance) {
        this.userBalance = userBalance;
    }

    public static class UserBalanceDTO {
        private String userId;
        private Integer balance;
        private String updateTime;
        private String cert;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Integer getBalance() {
            return balance;
        }

        public void setBalance(Integer balance) {
            this.balance = balance;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getCert() {
            return cert;
        }

        public void setCert(String cert) {
            this.cert = cert;
        }
    }
}

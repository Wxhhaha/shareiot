package com.wxh.shareiot.api;

public class CycleOrderRecords {

    /**
     * cycleId
     */
    private String cycleId;
    /**
     * startTime
     */
    private String startTime;
    /**
     * endTime
     */
    private String endTime;
    /**
     * ebikeId
     */
    private String ebikeId;
    /**
     * userId
     */
    private String userId;
    /**
     * cycleStatus
     */
    private int cycleStatus;

    private PayOrder payOrder;

    public int getCycleStatus() {
        return cycleStatus;
    }

    public void setCycleStatus(int cycleStatus) {
        this.cycleStatus = cycleStatus;
    }

    public PayOrder getPayOrder() {
        return payOrder;
    }

    public void setPayOrder(PayOrder payOrder) {
        this.payOrder = payOrder;
    }

    public String getCycleId() {
        return cycleId;
    }

    public void setCycleId(String cycleId) {
        this.cycleId = cycleId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEbikeId() {
        return ebikeId;
    }

    public void setEbikeId(String ebikeId) {
        this.ebikeId = ebikeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public class PayOrder{

        /**
         * 金额（单位:分）
         */
        private int amount;
        /**
         * 支付订单编号
         */
        private String orderId;
        /**
         * 骑行编号
         */
        private String cycleId;
        /**
         * 订单状态0:已取消1:激活2:完成3:关闭
         */
        private int orderStatus;
        /**
         * 开始时间
         */
        private String startTime;
        /**
         * 结束时间
         */
        private String endTime;
        /**
         * 用户编号
         */
        private String userId;
        /**
         * 支付状态0:未支付1:支付成功2:支付失败3:支付关闭
         */
        private int payStatus;

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getCycleId() {
            return cycleId;
        }

        public void setCycleId(String cycleId) {
            this.cycleId = cycleId;
        }

        public int getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(int orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getPayStatus() {
            return payStatus;
        }

        public void setPayStatus(int payStatus) {
            this.payStatus = payStatus;
        }
    }
}

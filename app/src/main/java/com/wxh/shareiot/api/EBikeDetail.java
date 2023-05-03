package com.wxh.shareiot.api;

public class EBikeDetail {

    /**
     * ebikeId
     */
    private String ebikeId;
    /**
     * ebikeCode
     */
    private String ebikeCode;
    /**
     * mac
     */
    private String mac;
    /**
     * type
     */
    private Object type;
    /**
     * storageTime
     */
    private String storageTime;
    /**
     * status
     */
    private Integer status;
    /**
     * remark
     */
    private Object remark;
    /**
     * soc
     */
    private Integer soc;

    public String getEbikeId() {
        return ebikeId;
    }

    public void setEbikeId(String ebikeId) {
        this.ebikeId = ebikeId;
    }

    public String getEbikeCode() {
        return ebikeCode;
    }

    public void setEbikeCode(String ebikeCode) {
        this.ebikeCode = ebikeCode;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public String getStorageTime() {
        return storageTime;
    }

    public void setStorageTime(String storageTime) {
        this.storageTime = storageTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getRemark() {
        return remark;
    }

    public void setRemark(Object remark) {
        this.remark = remark;
    }

    public Integer getSoc() {
        return soc;
    }

    public void setSoc(Integer soc) {
        this.soc = soc;
    }
}

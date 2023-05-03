package com.wxh.shareiot.socket;

public class ControllerBData {
    /**
     * 必填车辆编号字符
     */
    private String ebikeId;
    /**
     * 必填车辆编号字符
     */
    private String cycleId;
    /**
     * 里程整数
     */
    private int odo;
    /**
     * 控制器温度整数
     */
    private int contTemp;
    /**
     * 电机温度整数
     */
    private int motorTemp;
    /**
     * 电池温度整数
     */
    private int batTemp;
    /**
     * 电池电压整数
     */
    private int batVolt;
    /**
     * 电池电流整数
     */
    private int batCur;
    /**
     * 电池剩余容量整数
     */
    private int batQuan;
    /**
     * 剩余电量百分比整数
     */
    private int soc;
    /**
     * 实时功率百分比整数
     */
    private int curPower;

    public String getEbikeId() {
        return ebikeId;
    }

    public void setEbikeId(String ebikeId) {
        this.ebikeId = ebikeId;
    }

    public String getCycleId() {
        return cycleId;
    }

    public void setCycleId(String cycleId) {
        this.cycleId = cycleId;
    }

    public int getOdo() {
        return odo;
    }

    public void setOdo(int odo) {
        this.odo = odo;
    }

    public int getContTemp() {
        return contTemp;
    }

    public void setContTemp(int contTemp) {
        this.contTemp = contTemp;
    }

    public int getMotorTemp() {
        return motorTemp;
    }

    public void setMotorTemp(int motorTemp) {
        this.motorTemp = motorTemp;
    }

    public int getBatTemp() {
        return batTemp;
    }

    public void setBatTemp(int batTemp) {
        this.batTemp = batTemp;
    }

    public int getBatVolt() {
        return batVolt;
    }

    public void setBatVolt(int batVolt) {
        this.batVolt = batVolt;
    }

    public int getBatCur() {
        return batCur;
    }

    public void setBatCur(int batCur) {
        this.batCur = batCur;
    }

    public int getBatQuan() {
        return batQuan;
    }

    public void setBatQuan(int batQuan) {
        this.batQuan = batQuan;
    }

    public int getSoc() {
        return soc;
    }

    public void setSoc(int soc) {
        this.soc = soc;
    }

    public int getCurPower() {
        return curPower;
    }

    public void setCurPower(int curPower) {
        this.curPower = curPower;
    }
}

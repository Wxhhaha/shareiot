package com.wxh.shareiot.socket;

public class ControllerAData {
    /**
     * 必填车辆编号字符串
     */
    private String ebikeId;
    /**
     * 必填骑行编号字符串
     */
    private String cycleId;
    /**
     * 助力档位整数
     */
    private int gear;
    /**
     * 6KM推行开关整数
     */
    private int swkm6;
    /**
     * 大灯开关整数
     */
    private int lamp;
    /**
     * 刹车状态开关整数
     */
    private int brake;
    /**
     * 电机运转整数
     */
    private int motor;
    /**
     * 仪表屏幕显示开关整数
     */
    private int screen;
    /**
     * 电源键状态整数
     */
    private int power;
    /**
     * 速度整数
     */
    private int speed;
    /**
     * 踏频整数
     */
    private int cadence;
    /**
     * 脚踏力矩整数
     */
    private int pedal;
    /**
     * 故障信息整数
     */
    private String fault;

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

    public int getGear() {
        return gear;
    }

    public void setGear(int gear) {
        this.gear = gear;
    }

    public int getSwkm6() {
        return swkm6;
    }

    public void setSwkm6(int swkm6) {
        this.swkm6 = swkm6;
    }

    public int getLamp() {
        return lamp;
    }

    public void setLamp(int lamp) {
        this.lamp = lamp;
    }

    public int getBrake() {
        return brake;
    }

    public void setBrake(int brake) {
        this.brake = brake;
    }

    public int getMotor() {
        return motor;
    }

    public void setMotor(int motor) {
        this.motor = motor;
    }

    public int getScreen() {
        return screen;
    }

    public void setScreen(int screen) {
        this.screen = screen;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getCadence() {
        return cadence;
    }

    public void setCadence(int cadence) {
        this.cadence = cadence;
    }

    public int getPedal() {
        return pedal;
    }

    public void setPedal(int pedal) {
        this.pedal = pedal;
    }

    public String getFault() {
        return fault;
    }

    public void setFault(String fault) {
        this.fault = fault;
    }
}

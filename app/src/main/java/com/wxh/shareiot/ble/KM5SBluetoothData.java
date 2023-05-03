package com.wxh.shareiot.ble;

public class KM5SBluetoothData {
    public static SettingValue settingValue;
    public int[] Datainput = new int[37];
    // 手动设置的值

    // public int Battery_Voltage;//电量
    public int[] DataSendout;// 发送给蓝牙的数据
    public int[] ReceivedString;//
    // public byte[] voltageReceivedString;//电压收到的数据
//    public boolean Setting_Changed = true;
    public int Re_Counter, wheel_perimeter, temp, Last_Capacity, Last_Speed;
    private int Speed_Zero = 0;
    private int dataTotalNum = 0;// 总数据个数
    private int currentTotal = 0;// 电流累加值
    private float mileage = 0;// 里程数 单位0.1km
    private int totalSpeed = 0;
//    private int BF_Command_ID = 0;

    private static KM5SBluetoothData instance;

    public static KM5SBluetoothData getInstance() {
        if (instance == null) {
            instance = new KM5SBluetoothData();
        }
        return instance;
    }

    public void initInput() {
        Datainput[0] = 0; // 协议类型,0:KM5S,1:JLCD,
        // 2:APT
        // 4：BF5.0
        Datainput[1] = settingValue.getCurrentGear(); // 当前档位值，0~9
        Datainput[2] = settingValue.getLightStatus();// 当前大灯状态，0：灭，1：亮
        Datainput[3] = 0;// 当前6km推行状态，0：无，1：有
        Datainput[4] = settingValue.getUnit(); // 公里、英里切换，0：公里，1：英里
        Datainput[5] = settingValue.getWheelPerimeter(); // 轮径 0:16 inch,
        // 1:18inch, 2:20inch,
        // 3:22inch,
        // 4:24inch, 5:26inch, 6:700c, 7:28inch
        Datainput[6] = settingValue.getSpeedLimit(); // 限速值
        // km/h,如果界面上设的是英里，要先转换成公里，再传值
        Datainput[7] = settingValue.getSpeedSensorMagnets(); // 每圈测速磁钢数
        Datainput[8] = (int) (settingValue.getCurrentLimit() * 10); // 限流值，实际值x10,
        // 150代表15A
        Datainput[9] = settingValue.getStartupAcceleration(); // 缓启动参数，3：20%，2：40%，1：70%，0：100%
        Datainput[10] = (int) (settingValue.getFullPowerVoltage() * 10); // 满电电压值，实际值x10,
        // 380代表38V
        Datainput[11] = (int) (settingValue.getMinPowerVoltage() * 10); // 欠压值，实际值x10,
        // 300代表30V
        Datainput[12] = settingValue.getLevelLimit1(); // 1档最大百分比，40代表40%
        Datainput[13] = settingValue.getLevelLimit2();// 2档最大百分比，55代表55%
        Datainput[14] = settingValue.getLevelLimit3(); // 3档最大百分比，70代表70%
        Datainput[15] = settingValue.getLevelLimit4(); // 4档最大百分比，85代表85%
        Datainput[16] = settingValue.getLevelLimit5(); // 5档最大百分比，100代表100%
        Datainput[17] = settingValue.getLevelLimit6(); // 6档最大百分比，100代表100%
        Datainput[18] = settingValue.getLevelLimit7(); // 7档最大百分比，100代表100%
        Datainput[19] = settingValue.getLevelLimit8(); // 8档最大百分比，100代表100%
        Datainput[20] = settingValue.getLevelLimit9(); // 9档最大百分比，100代表100%
        Datainput[21] = settingValue.getPedalSensorMagnets(); // 助力传感器一圈脉冲数
        Datainput[22] = settingValue.getPedalPulseDutyCycle(); // 助力方向<50%
        // 为0，>50%为1
        Datainput[23] = settingValue.getStartAfterMagnets(); // 启动等待过磁钢数
        Datainput[24] = settingValue.getThrottleLimitedByPASLevels(); // 转把分档与否，0不分，1分
        Datainput[25] = settingValue.getThrottleMaxSpeed(); // 转把6km/h限速，0不限，1限
        Datainput[26] = 0;// 电池电压值
        Datainput[27] = 0;// 电池电流值x10
        Datainput[28] = 0;// 当前速度x10
        Datainput[29] = 0;// 故障代码
        Datainput[30] = 0; // odo
        Datainput[31] = 0; // tripa
        Datainput[32] = 0; // tripb
        Datainput[33] = (int) (settingValue.getBatteryCapacity() * 10); // 电池容量，实际值x10,
        // 120代表12Ah
        Datainput[34] = settingValue.getTotalPasLevels();
        Datainput[36] = settingValue.getPasType();
        DataSendout = new int[16];
        ReceivedString = new int[20];
        Speed_Zero = 0;
        dataTotalNum = 0;
        currentTotal = 0;
        mileage = 0;
        totalSpeed = 0;
    }

    public void setLight(int status) {
        settingValue.setLightStatus(status);
        Datainput[2] = status;
    }

    /**
     * 初始化input并给控制器发送数据 等待回收数据
     */
    public String sendOutData() {
//		if (Setting_Changed == true) { // Setting_Changed,
//										// 开机时一定为true，用户从设置界面返回主界面时，也要设为true，防止用户更改了参数
//			// 发送数据给控制器,最后一个1代表为设置参数状态，0代表为正常通讯状态
//			return sendToController(Datainput, DataSendout, 1);
//		} else {
        return sendToController();
//		}
    }

    public void voltageData(String voltageReceivedString, boolean isMD) {
        int Battery_Voltage = CalculateVoltage(voltageReceivedString, isMD); // OK+Get:1.23,这个函数先要提取出1.23，然后转成浮点数，再x220
        if (Battery_Voltage > Datainput[26]) {
            Datainput[26] = Battery_Voltage;
        } else {
            Datainput[26] = (Battery_Voltage + Datainput[26] * 11) / 12;
        }
        if (Battery_Voltage < 100) {
            Datainput[26] = Battery_Voltage;
        }
    }

    public int CalculateVoltage(String str, boolean isMD) {
        int ret = 0;
        if (str.contains("OK+Get:")) {
            str = str.replace("OK+Get:", "");
            float f = Float.parseFloat(str);
            if (isMD) {
                ret = (int) (f * 210);
            } else {
                ret = (int) (f * 210 + 48);
            }
        } else if (str.contains("OK+ADC4:")) {
            str = str.replace("OK+ADC4:", "");
            float f = Float.parseFloat(str);
            if (isMD) {
                ret = (int) (f * 210);
            } else {
                ret = (int) (f * 210 + 48);
            }
        }
        return ret;
    }

//    public void setReceivedString(byte[] byteString) {
//        int[] a = BleDataConvertUtil.hexByte2byte(byteString);
//        LogUtils.e(ArrayUtils.toString(a));
//        if (ReceivedString != null) {
//            for (int i = 0; i < a.length; i++) {
//                ReceivedString[i] = a[i];
//            }
//        }
//    }

    /**
     * 处理数据
     *
     * @param mstime 毫秒
     */
    public int[] dealReceiveData(byte[] ReceivedString, long mstime) {
        // 电池电压值x10,低通滤波，求平均
        if (ReceivedString[0] == 0x3A && ReceivedString[1] == 0x1A) {
            settingValue.setProtocolType(0);
            if ((ReceivedString[4] & 0x80) != 0) {
                Datainput[26] = Datainput[11] - 10;
            } // 控制器发来了欠压信息，则APP的电压值也设为欠压点以下1V
            Datainput[27] = ReceivedString[5] * 10 / 3; // 电池电流值x10
            if (Datainput[27] < 4)
                Datainput[27] = 0;

            if (Datainput[5] == 0) {
                wheel_perimeter = 1276;
            } else if (Datainput[5] == 1) {
                wheel_perimeter = 1436;
            } else if (Datainput[5] == 2) {
                wheel_perimeter = 1595;
            } else if (Datainput[5] == 3) {
                wheel_perimeter = 1755;
            } else if (Datainput[5] == 4) {
                wheel_perimeter = 1914;
            } else if (Datainput[5] == 5) {
                wheel_perimeter = 2074;
            } else if (Datainput[5] == 6) {
                wheel_perimeter = 2154;
            } else if (Datainput[5] == 7) {
                wheel_perimeter = 2233;
            }
            Re_Counter = (ReceivedString[6] << 8) + ReceivedString[7];
            if (Re_Counter > 50) {
                Datainput[28] = wheel_perimeter * 36 / Re_Counter;
            } // 当前速度x10
            if (Re_Counter >= 3500) {
                Datainput[28] = 0;
            }
            Datainput[29] = ReceivedString[8];// 故障代码
        }
        return DataProcess(Datainput, mstime);
    }

    //km5s
    private String sendToController() {
        int[] DataSendout = new int[16];
        DataSendout[0] = 0x3A; // 起始符，固定不变
        DataSendout[1] = 0x1A; // 控制器地址，固定不变

        // 正常通讯数据
        DataSendout[2] = 0x52; // ’R' 当前为设正常运行数据包，固定不变
        DataSendout[3] = 0x02; // 有效数据长度为2，固定不变
        if (Datainput[36] == 0) {// 速度档
            if (Datainput[1] == 0) {
                DataSendout[4] = 0;
            } else {
                DataSendout[4] = (Datainput[11 + Datainput[1]] * 255 / 100);
            } // PWM最大值（255最大）
        } else {// 电流档
            if (Datainput[1] == 0) {
                DataSendout[4] = 0;
            } else {
                DataSendout[4] = 255;
            }
        }

        if (Datainput[2] != 0) { // 大灯
            DataSendout[5] |= 0x80;
        } else {
            DataSendout[5] &= ~0x80;
        }
        if (Datainput[3] != 0) { // 6km推行
            DataSendout[5] |= 0x10;
            DataSendout[4] = 51;
        } else {
            DataSendout[5] &= ~0x10;
        }

//        if (Datainput[26] < Datainput[11]) {// 欠压
//            DataSendout[5] |= 0x20;
//        } else {
//            DataSendout[5] &= ~0x20;
//        }

//        if (Datainput[28] > Datainput[6] * 10) { // 超速
//            DataSendout[5] |= 0x01;
//        } else {
//            DataSendout[5] &= ~0x01;
//        }
        // 蓝牙小仪表处理数据
//        if ((DataIn[1] & 0x01) != 0)
//            DataSendout[4] |= 0x01;
//
//        else
//            DataSendout[4] &= ~0x01;
//
//        if ((DataIn[1] & 0x02) != 0)
//            DataSendout[5] |= 0x40;
//
//        else
//            DataSendout[5] &= ~0x40;
//
//        if ((DataIn[1] & 0x04) != 0)
//            DataSendout[5] |= 0x02;
//
//        else
//            DataSendout[5] &= ~0x02;
//
//        if ((DataIn[1] & 0x08) != 0)
//            DataSendout[5] |= 0x08;
//
//        else
//            DataSendout[5] &= ~0x08;
        // 将当前的档位值，穿插在没有定义的数据位中，发给控制器

        int sum = 0;
        for (int i = 1; i <= 5; i++) {
            sum += DataSendout[i];
        }
        DataSendout[6] = (sum & 0xff);
        DataSendout[7] = (sum >> 8);
        DataSendout[8] = 0x0D;
        DataSendout[9] = 0x0A;

        return BluetoothSend(DataSendout, 10); // 蓝牙发送数据函数，10是数据长度

    }

    private String BluetoothSend(int[] DataSendout, int length) {
        StringBuilder sbBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String str = Integer.toHexString(DataSendout[i]);
            if (str.length() == 1) {
                sbBuilder.append("0" + str);
            } else {
                sbBuilder.append(str);
            }

        }
        return sbBuilder.toString();
    }

    private int[] DataProcess(int[] DataIn, long msTime) {
        // 23;0;0;0;99;5;0;0;0;0;
        // DataOut数据定义：0：速度，1：功率百分比，2：实际功率，3：剩余电量百分比, 4:剩余里程, 5:是否6km推行，6：电流，7：大灯，8:故障代码
        int[] DataOut = new int[9];// 外部可直接显示的值
        DataOut[0] = DataIn[28]; // 当前速度，单位为0.1km/h

        if (DataOut[0] > Last_Speed * 2 && DataOut[0] > 250) {
            DataOut[0] = Last_Speed + (DataOut[0] - Last_Speed) / 8;
        }
        Last_Speed = DataOut[0]; // 让速度信号不要突变
        if (DataOut[0] <= 23) {
            DataOut[0] = 0;
        }
        mileage += (float) DataOut[0] / 7200;

        DataOut[1] = DataIn[27] * 100 / DataIn[8]; // 功率百分比，当前电流/限流值
        if (DataOut[1] > 100) {
            DataOut[1] = 100;
        }

        DataOut[2] = DataIn[27] * DataIn[26] / 100; // 实际功率值，watt

        try {
            DataOut[3] = (DataIn[26] - DataIn[11]) * 100 / (DataIn[10] - DataIn[11]);
        } catch (Exception e) {
        }
        if (DataIn[26] < DataIn[11]) {
            DataOut[3] = 0;
        }
        if (DataIn[26] > DataIn[10]) {
            DataOut[3] = 100;
        }
        if (DataIn[10] <= DataIn[11]) {
            DataOut[3] = 0;
        } // 剩余电量百分比

        if (DataIn[27] > 20) {
            DataOut[3] = Last_Capacity; // 当电流大于2A的时候，不更新电池容量
        } else {
            Last_Capacity = DataOut[3];
        }

        if (msTime > 0) {
            if (DataOut[0] > 0) {
                dataTotalNum++;
                currentTotal += DataIn[27];
                totalSpeed += DataOut[0];
            }
        }

        // if(DataIn[27] == 0)
        if (dataTotalNum == 0 || currentTotal == 0) {// 电流为0时，显示最大剩余里程
            DataOut[4] = 199;
        } else {
            // DataOut[4] = DataOut[3] * DataIn[33] * DataIn[28] / (1000 *
            // DataIn[27]); // 剩余里程=剩余容量*速度/放电电流
            DataOut[4] = (int) ((DataOut[3] * DataIn[33] * ((double) totalSpeed / dataTotalNum) / (currentTotal
                    / dataTotalNum * 1000)));// 剩余里程=剩余容量*平均速度/平均放电电流
        }
        DataOut[5] = DataIn[3];
        DataOut[6] = DataIn[27];
        DataOut[7] = DataIn[2];
        DataOut[8] = DataIn[29];

        return DataOut;
    }

    public void defaultValue() {
        settingValue = new SettingValue();
        settingValue.setProtocolType(0);// 协议类型
        settingValue.setCurrentGear(5);// 当前档位值
        settingValue.setLightStatus(0);// 当前大灯状态
        settingValue.setWalkMode(0);// 当前6km推行状态
        settingValue.setUnit(0);// 公里、英里切换
        settingValue.setWheelPerimeter(5);// 轮径
        settingValue.setSpeedLimit(25);// 限速值
        settingValue.setSpeedSensorMagnets(1);// 每圈测速磁钢数
        settingValue.setCurrentLimit(15);// 限流值
        settingValue.setStartupAcceleration(0);// 缓启动参数
        settingValue.setFullPowerVoltage(38);// 满电电压值
        settingValue.setMinPowerVoltage(30);// 欠压值
        settingValue.setLevelLimit1(40);
        settingValue.setLevelLimit2(55);
        settingValue.setLevelLimit3(70);
        settingValue.setLevelLimit4(85);
        settingValue.setLevelLimit5(100);
        settingValue.setLevelLimit6(100);
        settingValue.setLevelLimit7(100);
        settingValue.setLevelLimit8(100);
        settingValue.setLevelLimit9(100);
        settingValue.setPedalSensorMagnets(12);// 助力传感器一圈脉冲数
        settingValue.setPedalPulseDutyCycle(0);// 助力方向
        settingValue.setStartAfterMagnets(2);// 启动等待过磁钢数
        settingValue.setThrottleLimitedByPASLevels(0);// 转把分档与否
        settingValue.setThrottleMaxSpeed(0);// 转把6km/h限速
        settingValue.setBatteryCapacity(10);
        settingValue.setTotalPasLevels(5);
        settingValue.setPasType(0);

        initInput();
    }
}

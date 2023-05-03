package com.wxh.shareiot.ble;

import java.util.Date;

public class ControllerConfig{

	/**
	 *	车辆编号
	 */
	private String ebikeId;

	/**
	 *	UUID0
	 */
	private String uuid0;

	/**
	 *	UUID1
	 */
	private String uuid1;

	/**
	 *	UUID2
	 */
	private String uuid2;

	/**
	 *	UUID3
	 */
	private String uuid3;

	/**
	 *	UUID4
	 */
	private String uuid4;

	/**
	 *	UUID5
	 */
	private String uuid5;

	/**
	 *	控制器客户编号
	 */
	private String controllerCustomerNumber;

	/**
	 *	图纸客户型号
	 */
	private String controllerPartNumber;

	/**
	 *	固件版本号高字节
	 */
	private String controllerFirmwareVersionHigh;

	/**
	 *	固件版本号低字节
	 */
	private String controllerFirmwareVersionLow;

	/**
	 *	生产年月
	 */
	private String productionDateYm;

	/**
	 *	生产日时
	 */
	private String productionDateDh;

	/**
	 *	高里程
	 */
	private String odoH;

	/**
	 *	低里程
	 */
	private String odoL;

	/**
	 *	车架号0
	 */
	private String vehicleNum0;

	/**
	 *	车架号1
	 */
	private String vehicleNum1;

	/**
	 *	车架号2
	 */
	private String vehicleNum2;

	/**
	 *	车架号3
	 */
	private String vehicleNum3;

	/**
	 *	车架号4
	 */
	private String vehicleNum4;

	/**
	 *	加速感觉
	 */
	private String acceleration;

	/**
	 *	最大相电流
	 */
	private String maxMotorPhaseCurrent;

	/**
	 *	推行助力时速度百分比
	 */
	private String walkAssistancePercentage;

	/**
	 *	自动休眠等待时间
	 */
	private String autoSleepWaitTime;

	/**
	 *	档位总数
	 */
	private String numberOfPasSteps;

	/**
	 *	开启档位速度
	 */
	private String speedStepsEnable;

	/**
	 *	0 档速度百分比
	 */
	private String step0SpeedLimit;

	/**
	 *	1 档速度百分比
	 */
	private String step1SpeedLimit;

	/**
	 *	2档速度百分比
	 */
	private String step2SpeedLimit;

	/**
	 *	3档速度百分比
	 */
	private String step3SpeedLimit;

	/**
	 *	4档速度百分比
	 */
	private String step4SpeedLimit;

	/**
	 *	5档速度百分比
	 */
	private String step5SpeedLimit;

	/**
	 *	6档速度百分比
	 */
	private String step6SpeedLimit;

	/**
	 *	7档速度百分比
	 */
	private String step7SpeedLimit;

	/**
	 *	8档速度百分比
	 */
	private String step8SpeedLimit;

	/**
	 *	9档速度百分比
	 */
	private String step9SpeedLimit;

	/**
	 *	分档电流
	 */
	private String powerStepsEnable;

	/**
	 *	0档功率百分比
	 */
	private String step0PowerLimit;

	/**
	 *	1档功率百分比
	 */
	private String step1PowerLimit;

	/**
	 *	2档功率百分比
	 */
	private String step2PowerLimit;

	/**
	 *	3档功率百分比
	 */
	private String step3PowerLimit;

	/**
	 *	4档功率百分比
	 */
	private String step4PowerLimit;

	/**
	 *	5档功率百分比
	 */
	private String step5PowerLimit;

	/**
	 *	6档功率百分比
	 */
	private String step6PowerLimit;

	/**
	 *	7档功率百分比
	 */
	private String step7PowerLimit;

	/**
	 *	8档功率百分比
	 */
	private String step8PowerLimit;

	/**
	 *	9档功率百分比
	 */
	private String step9PowerLimit;

	/**
	 *	控制器温控将功
	 */
	private String controllerTemperatureProtectionPowerReduce;

	/**
	 *	控制器温控断电
	 */
	private String controllerTemperatureProtectionPowerCutoff;

	/**
	 *	电机有温度传感器
	 */
	private String motorHasTemperatureSensor;

	/**
	 *	电机温度保
            护开始降功率的热敏电阻阻值
	 */
	private String motorTemperatureProtectionPowerReduce;

	/**
	 *	电机温度保
            护彻底关断的热敏电阻阻值
	 */
	private String motorTemperatureProtectionPowerCutoff;

	/**
	 *	尾灯联动方式
	 */
	private String rearLightLinkage;

	/**
	 *	尾灯工作模式
	 */
	private String rearLightWorkStyle;

	/**
	 *	尾灯亮度级别
	 */
	private String rearLightOnLevel;

	/**
	 *	母线电流限流值
	 */
	private String currentLimit;

	/**
	 *	电池欠压值
	 */
	private String batteryLowProtectionVoltage;

	/**
	 *	电池高压保护
	 */
	private String batteryHighProtectionVoltage;

	/**
	 *	接近欠压点以上几V开始降功率
	 */
	private String lowVoltagePowerReduceThreshold;

	/**
	 *	降到欠压点之上0.1V
	 */
	private String powerRemainPercentage;

	/**
	 *	电池内温度
	 */
	private String batteryPackTemperature;

	/**
	 *	电池电压
	 */
	private String batteryVoltage;

	/**
	 *	电池电流
	 */
	private String batteryCurrent;

	/**
	 *	电池健康程度
	 */
	private String soh;

	/**
	 *	相对容量百分比
	 */
	private String relativeCapacity;

	/**
	 *	绝对容量百分比
	 */
	private String absoluteCapacity;

	/**
	 *	剩余电量
	 */
	private String remainCapacity;

	/**
	 *	满载电量
	 */
	private String fullCapacity;

	/**
	 *	循环次数
	 */
	private String recycles;

	/**
	 *	固件和硬件版本号
	 */
	private String firmware;

	/**
	 *	电池0电压
	 */
	private String cell0Voltage;

	/**
	 *	电池1电压
	 */
	private String cell1Voltage;

	/**
	 *	电池2电压
	 */
	private String cell2Voltage;

	/**
	 *	电池3电压
	 */
	private String cell3Voltage;

	/**
	 *	电池4电压
	 */
	private String cell4Voltage;

	/**
	 *	电池5电压
	 */
	private String cell5Voltage;

	/**
	 *	电池6电压
	 */
	private String cell6Voltage;

	/**
	 *	电池7电压
	 */
	private String cell7Voltage;

	/**
	 *	电池8电压
	 */
	private String cell8Voltage;

	/**
	 *	电池9电压
	 */
	private String cell9Voltage;

	/**
	 *	电池10电压
	 */
	private String cell10Voltage;

	/**
	 *	电池11电压
	 */
	private String cell11Voltage;

	/**
	 *	电池12电压
	 */
	private String cell12Voltage;

	/**
	 *	电池13电压
	 */
	private String cell13Voltage;

	/**
	 *	电池编号0
	 */
	private String batteryUuid0;

	/**
	 *	电池编号1
	 */
	private String batteryUuid1;

	/**
	 *	电池编号2
	 */
	private String batteryUuid2;

	/**
	 *	电池编号3
	 */
	private String batteryUuid3;

	/**
	 *	电池编号4
	 */
	private String batteryUuid4;

	/**
	 *	电池编号5
	 */
	private String batteryUuid5;

	/**
	 *	电池编号6
	 */
	private String batteryUuid6;

	/**
	 *	电池编号7
	 */
	private String batteryUuid7;

	/**
	 *	位置传感器类型
	 */
	private String rotorPositionSensorType;

	/**
	 *	带单向离合器
	 */
	private String withOnewayClutch;

	/**
	 *	电机额定电压
	 */
	private String motorRatedVoltage;

	/**
	 *	电机额定功率
	 */
	private String motorRatedPower;

	/**
	 *	转子额定电压下空载转速
	 */
	private String rotorNoloadRpmUnderRatedVoltage;

	/**
	 *	电机极对数
	 */
	private String noOfPolePairs;

	/**
	 *	齿轮减速比
	 */
	private String gearReductionRatio;

	/**
	 *	单相绕组电阻
	 */
	private String statorResistance;

	/**
	 *	单相绕组电感
	 */
	private String statorInductance;

	/**
	 *	反电动势常数
	 */
	private String backEmfContantKe;

	/**
	 *	霍尔类型
	 */
	private String rotorPositionSensorDetection;

	/**
	 *	K1
	 */
	private String k1;

	/**
	 *	K2
	 */
	private String k2;

	/**
	 *	PPL_KP_GAIN
	 */
	private String pplKpGain;

	/**
	 *	PPL_KI_GAIN
	 */
	private String pplKiGain;

	/**
	 *	F1
	 */
	private String f1;

	/**
	 *	F2
	 */
	private String f2;

	/**
	 *	转子转速最小值
	 */
	private String rotorSpeedMin;

	/**
	 *	旋转方向
	 */
	private String directionOfRotation;

	/**
	 *	转把验证
	 */
	private String throttleValid;

	/**
	 *	转把最大输出
	 */
	private String throttleMaxOutput;

	/**
	 *	转把启动电压
	 */
	private String throttleStartupVoltage;

	/**
	 *	转把满载电压
	 */
	private String throttlFullVoltage;

	/**
	 *	转把经过电压
	 */
	private String throttleOverVoltage;

	/**
	 *	转把分档
	 */
	private String throttleLimitedByPasSteps;

	/**
	 *	转把工作模式
	 */
	private String throttleWorkingMode;

	/**
	 *	6km推行生效模式
	 */
	private String throttleAssistanceValidation;

	/**
	 *	助力传感器类型
	 */
	private String pedalSensorType;

	/**
	 *	速度信号类型
	 */
	private String speedSignalType;

	/**
	 *	速度信号向后踩踏板
	 */
	private String speedSignalPedallingBackward;

	/**
	 *	每圈脉冲周期数
	 */
	private String numberOfPulsesPerCircle;

	/**
	 *	起步等待脉冲数
	 */
	private String startingWaitingPulseNumbers;

	/**
	 *	停止脚踏到助力切断延时时间
	 */
	private String stopDelayTime;

	/**
	 *	助力比为 10
	 */
	private String pasSpeedRatio;

	/**
	 *	启动模式
	 */
	private String startupMode;

	/**
	 *	零扭矩自动检测
	 */
	private String zeroTorqueAutodetect;

	/**
	 *	零扭矩固定值
	 */
	private String zeroTorqueFixedValue;

	/**
	 *	50NM扭矩增强值
	 */
	private String nm50TorqueValueIncrement;

	/**
	 *	扭矩启动增量
	 */
	private String torqueStartupValueIncrement;

	/**
	 *	助力比为对应的输出
	 */
	private String pasTorqueAmplifcationRatio;

	/**
	 *	扭矩增加反应时间
	 */
	private String torqueIncreaseReactionTime;

	/**
	 *	扭矩减小反应时间
	 */
	private String torqueDecreaseReactionTime;

	/**
	 *	速度信号维护电源
	 */
	private String speedSignalMaintenancePower;

	/**
	 *	限速功能
	 */
	private String speedLimitFunction;

	/**
	 *	外部测速每圈信号个数
	 */
	private String numberOfMagnetsOfSpeedSensor;

	/**
	 *	轮距
	 */
	private String wheelPerimeter;

	/**
	 *	速度限制值
	 */
	private String speedLimitValue;

	/**
	 *	整车测速传感器个数
	 */
	private String speedSensorNumbers;

	/**
	 *	通信协议
	 */
	private String communicationProtocol;

	/**
	 *	显示优先级
	 */
	private String displayHighPriority;

	/**
	 *	更新时间
	 */
	private Date updateTime;

}


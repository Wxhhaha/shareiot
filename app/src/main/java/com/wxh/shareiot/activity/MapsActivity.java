package com.wxh.shareiot.activity;

import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.response.BleMtuResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.mapbox.geojson.Point;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation;
import com.permissionx.guolindev.PermissionX;
import com.wxh.basiclib.base.BaseActivity;
import com.wxh.basiclib.ble.LDBLCmd;
import com.wxh.basiclib.ble.LDBLResponse;
import com.wxh.basiclib.manager.ActivityManager;
import com.wxh.basiclib.utils.LanguageUtil;
import com.wxh.basiclib.utils.LogUtil;
import com.wxh.basiclib.view.circledialog.CircleDialog;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.CreatePayOrderBean;
import com.wxh.shareiot.api.CycleOrder;
import com.wxh.shareiot.api.CycleOrderRecords;
import com.wxh.shareiot.api.EBikeDetail;
import com.wxh.shareiot.api.EbikeGis;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.api.RegionFenceResponse;
import com.wxh.shareiot.ble.BleDataConvertUtil;
import com.wxh.shareiot.ble.BleScanService;
import com.wxh.shareiot.ble.BleUtils;
import com.wxh.shareiot.ble.ClientManager;
import com.wxh.shareiot.ble.Device;
import com.wxh.shareiot.databinding.ActivityMapsBinding;
import com.wxh.shareiot.databinding.BottomPayInfoBinding;
import com.wxh.shareiot.databinding.TopSnackInfoBinding;
import com.wxh.shareiot.socket.BikeData;
import com.wxh.shareiot.socket.GisData;
import com.wxh.shareiot.socket.GyroData;
import com.wxh.shareiot.socket.JWebSocketClient;
import com.wxh.shareiot.socket.JWebSocketClientService;
import com.wxh.shareiot.socket.SendMsgData;
import com.wxh.shareiot.socket.SendMsgDataUtil;
import com.wxh.shareiot.utils.CommonUtil;
import com.wxh.shareiot.utils.Constant;
import com.wxh.shareiot.utils.MapBoxUtils;
import com.wxh.shareiot.utils.RideDialog;
import com.wxh.shareiot.utils.StatusBarUtil;
import com.wxh.shareiot.utils.UserUtil;
import com.wxh.shareiot.viewmodel.CycleViewModel;
import com.wxh.shareiot.views.LockLoadingDialog;
import com.wxh.shareiot.views.UnfinishedDialog;
import com.xiaoguang.widget.mlkitscanner.ScanManager;
import com.xiaoguang.widget.mlkitscanner.model.ScanConfig;

import org.java_websocket.enums.ReadyState;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MapsActivity extends BaseActivity<CycleViewModel, ActivityMapsBinding> {

    private static final int LOCATION_TIMEOUT = 0x11;
    private static final int GYRO_TIMEOUT = 0x12;
    private static final int CONTROLLER_TIMEOUT = 0x13;
    private static final int UNLOCK_TIMEOUT = 0x14;
    private static final int CONNECT_TIMEOUT = 0x15;
    private static final int LOCK_TIMEOUT = 0x16;

    private static final long UNLOCK_TIMEOUT_VALUE = 30 * 1000;
    private static final long LOCK_TIMEOUT_VALUE = 30 * 1000;
    private static final int CONNECT_TIMEOUT_VALUE = 10 * 1000;
    private static final long LOCATION_TIMEOUT_VALUE = 60 * 1000;
    private static final long GYRO_TIMEOUT_VALUE1 = 50 * 1000;
    private static final long GYRO_TIMEOUT_VALUE2 = 10 * 1000;
    private static final int CONTROLLER_TIMEOUT_VALUE = 10 * 1000;
    private static final int FAKE_STATUS_BAR_VIEW_ID = 0x1;

    private boolean setLightOn;//大灯开
    private boolean setLightOff;//大灯关
    private boolean setPas;//档位设置
    private boolean setWalkOn;//推行开
    private boolean setPowerOff;//关机
    private int pas;
    private int newpas;

    //获取参数设置保存的内容
    private HashMap<Integer, String> paraAddressNameMap;
    private int[] paraAddress;
    private int paraNum;

//    private GoogleMap mMap;
//    private List<Marker> markers;
//    private LatLng currentLatLng;
//    private Polyline polyline;
//    private Marker selectedMarker;
//    private GLocOverlay gLocOverlay;

    //websocket相关
    private JWebSocketClient client;
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;
    private ChatMessageReceiver chatMessageReceiver;

    //蓝牙相关
    private BleScanService bleScanService;//蓝牙扫描服务
    private static int gpsType = 0;//0:基站定位 1：搜星定位
    private BluetoothClient bluetoothClient;
    private boolean isBleConnect;

    //mapbox
    private MapBoxUtils mapBoxUtils;
    private boolean isMapLoadFinished;
    private List<PointAnnotation> markers;
    private PolylineAnnotation polyline;
    private List<Point> linePoints;


    BleConnectStatusListener bleConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String s, int status) {
            switch (status) {
                case STATUS_CONNECTED:
                    LogUtil.e("蓝牙连接成功");
                    dataBinding.bleStatus.setImageResource(R.mipmap.ic_bt_on);
                    bluetoothClient.requestMtu(s, 200, new BleMtuResponse() {
                        @Override
                        public void onResponse(int i, Integer integer) {
                            LogUtil.e(integer.toString());
                        }
                    });
                    break;
                case STATUS_DISCONNECTED:
                    dataBinding.bleStatus.setImageResource(R.mipmap.ic_bt);
                    LogUtil.e("蓝牙断开连接");
                    isBleConnect = false;
                    if (isInRide && ebikeMac != null) {
                        startScanTimer();
                        dataBinding.ivRide.setVisibility(View.GONE);
                        if (rideDialog != null && rideDialog.isShowing()) {
                            rideDialog.dismiss();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private boolean bluetoothState;
    BluetoothStateListener stateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean b) {
            bluetoothState = b;
        }
    };

    private boolean hasNotifyLock;
    private boolean hasNotifyUnlock;
    private boolean isLock;
    BleNotifyResponse notifyResponse = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID uuid, UUID uuid1, byte[] value) {
            if (value.length < 3) {
                return;
            }
            runOnUiThread(() -> {
                if (value[0] == (byte) 0x3a && value[1] == (byte) 0xAB) {
                    dataBinding.ivRide.setVisibility(View.VISIBLE);
                    //行车数据回复
                    int[] result = LDBLResponse.dealMeterMessage(value);
                    switch (result[0]) {//当前车状态
                        case 1://开机状态
                        case 2://已靠近自动开锁
                            isLock = false;
                            break;
                        case 0://关机状态
                        case 3://已锁车
                            isLock = true;
                            break;
                    }

                    if (isLock && setPowerOff) {
                        if (!hasNotifyLock) {
                            hasNotifyLock = true;
                            viewModel.notifyLock(ebikeId);
                        }
                        return;
                    }
                    if (isLock) {
                        return;
                    }

                    if (!isInRide) {
                        if (!hasNotifyUnlock) {
                            hasNotifyUnlock = true;
                            viewModel.notifyUnlock(ebikeId);
                        }
                        return;
                    }


                    int light = result[1];
                    pas = result[2];
                    int mileage = result[3];
                    int current = result[4];
                    int speedLimit = result[5];

                    int currentLimit = result[9];//限流

                    int battery = result[10];
                    int speed = result[11];
                    int errorCode = result[12];
                    int cadence = result[18];
                    int voltage = result[21];
                    int power = (int) ((voltage / 10f) * (current / 3f));

                    int controllerTemp = result[19];
                    int motorTemp = result[20];
                    int brake = result[23];

                    if (rideDialog != null && rideDialog.isShowing()) {
                        rideDialog.resfreshView(CommonUtil.formatFloatOneNum(String.valueOf(speed * 0.1)), pas, light, CommonUtil.formatFloatOneNum(String.valueOf(mileage * 0.1)), battery + "", getError((byte) errorCode), cadence + "", power + "");
                    }
                    BikeData bikeData = new BikeData();
                    bikeData.setEbikeId(ebikeId);
                    bikeData.setCycleId(UserUtil.cycleId);
                    bikeData.setRecTime(System.currentTimeMillis());
                    bikeData.setControllerTemperature(controllerTemp);
                    bikeData.setMotorTemperature(motorTemp);
                    bikeData.setCurrent((int) (current / 3f * 10));//0.1A
                    bikeData.setVoltage(voltage);
                    bikeData.setCadence(cadence);
                    bikeData.setPower(power);
                    bikeData.setSpeed(speed);
                    bikeData.setMileage(mileage);
                    bikeData.setFaultId(CommonUtil.byteToString(errorCode));
                    bikeData.setLamp(light);
                    if (pas == 10) {
                        bikeData.setSwkm6(1);
                    } else {
                        bikeData.setSwkm6(0);
                    }
                    bikeData.setBrake(brake);
                    bikeData.setSoc(battery);
                    SendMsgData bike = SendMsgDataUtil.createJsonData(2, 1, UserUtil.token, Constant.ebikeBizCode, bikeData);
                    sendMsg(new Gson().toJson(bike));
                } else if (value[0] == (byte) 0x3a && value[1] == (byte) 0x28) {
                    //GPS数据回复
                    String[] gpsResult = LDBLResponse.dealGPSData(value);
                    if (gpsResult.length < 7) {
                        return;
                    }
                    if (!StringUtils.isEmpty(gpsResult[0])) {
                        //推送位置数据
                        GisData gisData = new GisData();
                        gisData.setEbikeId(ebikeId);
                        gisData.setCycleId(UserUtil.cycleId);
                        gisData.setRecTime(System.currentTimeMillis());
                        gisData.setLng(gpsResult[1]);
                        gisData.setLat(gpsResult[0]);
                        gisData.setAlt(gpsResult[2]);
                        gisData.setSpeed(gpsResult[3]);
                        SendMsgData data1 = SendMsgDataUtil.createJsonData(2, 1, UserUtil.token, Constant.gisBizCode, gisData);
                        sendMsg(new Gson().toJson(data1));
                    }

                    GyroData gyroData = new GyroData();
                    gyroData.setRecTime(System.currentTimeMillis());
                    gyroData.setEbikeId(ebikeId);
                    gyroData.setCycleId(UserUtil.cycleId);
                    gyroData.setGyroYaw(String.valueOf(gpsResult[6]));
                    gyroData.setGyroPitc(String.valueOf(gpsResult[5]));
                    gyroData.setGyroRoll(String.valueOf(gpsResult[4]));
                    SendMsgData data1 = SendMsgDataUtil.createJsonData(2, 1, UserUtil.token, Constant.gyroBizCode, gyroData);
                    sendMsg(new Gson().toJson(data1));
                    LogUtil.d("纬度=" + gpsResult[0] + ",经度=" + gpsResult[1] + ",海拔=" + gpsResult[2] + ",速度=" + gpsResult[3]);
                }
                Log.d("接收到", BleDataConvertUtil.byte2hex(value));
            });
        }

        @Override
        public void onResponse(int i) {

        }
    };

//    // 收取位置变化
//    private MyReceiver myReceiver;
//    // 位置更新服务
//    private LocationUpdatesService mService = null;
//    private boolean mBound = false;//服务是否绑定
//    private final ServiceConnection locationServiceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
//            mService = binder.getService();
//            mBound = true;
//            startLocation();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mService = null;
//            mBound = false;
//        }
//    };

    private boolean mIsFlashOn = false;//是否打开电筒

    private String ebikeId;//车辆iot编码
    private String ebikeMac;
    private String ebikeSoc;
    private boolean isInRide;//是否处于骑行状态


    //控件
    private TopSnackInfoBinding snackInfoBinding;
    private BottomPayInfoBinding payInfoBinding;
    private byte messageKey = 0x02;

    @Override
    protected int getLayout() {
        return R.layout.activity_maps;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        StatusBarUtil.setDrawable(this, R.drawable.bg_shape_gradient);
        //谷歌添加地图
//        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
//        getSupportFragmentManager().beginTransaction().add(R.id.my_container, mapFragment).commit();
//        mapFragment.getMapAsync(this);

        dataBinding.llScan.setOnClickListener(view -> {
            scanQr();

        });

        dataBinding.topAppBar.setNavigationOnClickListener(view -> {
            dataBinding.drawerLayout.openDrawer(GravityCompat.START);
        });

        dataBinding.nvDrawer.setItemIconTintList(null);
        dataBinding.nvDrawer.setNavigationItemSelectedListener(item -> {
            dataBinding.drawerLayout.closeDrawer(GravityCompat.START);
            switch (item.getItemId()) {
                case R.id.item2:
                    //todo 余额
                    ActivityUtils.startActivity(BalanceActivity.class);
                    break;
                case R.id.item3:
                    ActivityUtils.startActivity(RideListActivity.class);
                    break;
                case R.id.item5:
                    showSwitchLanguageDialog();
                    break;
                case R.id.item4:
                    Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivity(intent);
                    break;
                case R.id.item6:
                    //退出登录
                    UserUtil.cleanUserData();
                    ActivityUtils.startActivity(LoginActivity.class);
                    break;
            }
            return false;
        });

        dataBinding.ivLocate.setOnClickListener(view -> {
//            if (mMap != null && currentLatLng != null) {
//                MapUtils.setCenterByLatLng(mMap, currentLatLng, mMap.getCameraPosition().zoom);
//            }
            if (mapBoxUtils != null) {
                mapBoxUtils.moveCameraToUserPos();
            }
        });

        dataBinding.ivRefresh.setOnClickListener(view -> {
            getEbikeList();
        });
        snackInfoBinding = dataBinding.snackInfo;
        payInfoBinding = dataBinding.payInfo;
        mapBoxUtils = new MapBoxUtils(dataBinding.mapView, this, true, new MapBoxUtils.OnStyleLoadFinishListener() {
            @Override
            public void onFinish() {
                isMapLoadFinished = true;
                if (UserUtil.isLogin && !hasGetlist) {
                    //获取车辆列表
                    getEbikeList();
                }
                if (UserUtil.isLogin) {
                    //获取电子围栏
                    viewModel.findRegionFencePoints();
                }
            }
        }).hideLogo().hideScaleBar().setCompass();
        mapBoxUtils.setLocationChanged(new MapBoxUtils.LocationChanged() {
            @Override
            public void onChanged(Point point) {
                if (isInRide) {
                    if (linePoints == null) {
                        linePoints = new ArrayList<>();
                    }
                    linePoints.add(point);
                    if (polyline != null) {
                        mapBoxUtils.refreshPolyline(point);
                    } else {
                        polyline = mapBoxUtils.createPolyline(linePoints);
                    }
                }
            }
        });
    }

    /**
     * 动态申请权限
     */
    private void requestPermissions() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        } else {
            permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }

        PermissionX.init(this)
                .permissions(permissions)
                .onExplainRequestReason((scope, deniedList) -> {
                    scope.showRequestReasonDialog(deniedList, "应用需要获取以下权限来更好地为你服务：相机、位置、蓝牙", "确定", "取消");
                })
                .onForwardToSettings((scope, deniedList) ->
                        scope.showForwardToSettingsDialog(deniedList, "请前往设置页面进行授权", "去授权", "拒绝")
                )
                .request((allGranted, grantedList, deniedList) -> {
//                    if (grantedList.contains(Manifest.permission.BLUETOOTH)) {
//                        if (!bluetoothClient.isBluetoothOpened()) {
//                            bluetoothClient.openBluetooth();
//                        }
//
//                    }

                    if (allGranted) {
                        // ToastUtils.showShort("所有权限均已授权");
                    } else {
                        ToastUtils.showShort("These permissions are denied:", deniedList.get(0));
                    }
                });
    }

    long miss = 0;

    @Override
    protected void initData() {
        //token登录
        if (!UserUtil.isLogin) {
            String token = SPUtils.getInstance().getString("token");
            if (StringUtils.isEmpty(token)) {
                ActivityUtils.startActivity(LoginActivity.class);
                finish();
            } else {
                try {
                    viewModel.tokenLogin(token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            //获取最近一次骑行
            viewModel.findLastCycle(ebikeId);
//            //获取电子围栏
//            viewModel.findRegionFencePoints();
            //启动服务
            startJWebSClientService();
            //绑定服务
            bindService();
            //注册广播
            doRegisterReceiver();
        }
        requestPermissions();

        //设置参数生成地址-描述哈希表，保存地址为int
        String[] paraName = getResources().getStringArray(R.array.paraName);
        String[] paraAddressString = getResources().getStringArray(R.array.paraAddress);
        paraAddressNameMap = new HashMap<>();
        paraAddress = new int[paraName.length];
        paraNum = paraName.length;
        for (int i = 0; i < paraName.length; i++) {
            int address = ConvertUtils.hexString2Int(paraAddressString[i]);
            paraAddress[i] = address;
            paraAddressNameMap.put(address, paraName[i]);
        }

        //设置数据观察
        if (viewModel.getLoginData().getValue() == null) {
            setDataObserve();
        }

        //蓝牙状态监听
        bluetoothClient = ClientManager.getClient(getApplicationContext());
        bluetoothClient.registerBluetoothStateListener(stateListener);

        /**开始后台扫描附近蓝牙设备**/
        Intent bleScanServiceIntent = new Intent(getApplicationContext(), BleScanService.class);
        bindService(bleScanServiceIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                bleScanService = ((BleScanService.MyBinder) iBinder).getService();
                //定时获取附近蓝牙列表
                bleScanService.startScan(bluetoothClient);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        }, BIND_AUTO_CREATE);
        /**后台扫描附近蓝牙设备结束**/

//        /**
//         * 定位服务
//         */
//        Intent locationServiceIntent = new Intent(getApplicationContext(), LocationUpdatesService.class);
//        bindService(locationServiceIntent, locationServiceConnection, BIND_AUTO_CREATE);
//        myReceiver = new MyReceiver();
    }

    private Timer timer;
    private TimerTask timerTask;

    private void startScanTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (bleScanService != null && bluetoothClient != null) {
                    if (ebikeMac != null && !isBleConnect) {
                        if (BleUtils.deviceMap != null && BleUtils.deviceMap.containsKey(ebikeMac.toUpperCase())) {
                            connect(ebikeMac);
                            this.cancel();
                            timer.cancel();
                        } else {
                            bleScanService.startScan(bluetoothClient);
                        }
                    } else if (!isBleConnect) {
                        bleScanService.startScan(bluetoothClient);
                    }
                }
            }
        };
        timer.schedule(timerTask, 0, 10000);
    }

    private void startRide(long start) {
        miss = start;
        if (isInRide) {
            return;
        }
        dismissWaitDialog();
        mHandler.removeMessages(UNLOCK_TIMEOUT);

        //开始骑行
        isInRide = true;
        setPowerOff = false;
        if (isBleConnect) {
            getMeterData();
        }

        //设置UI
        setInRideView();
        //计时
        snackInfoBinding.chronometer.setBase(SystemClock.elapsedRealtime());
        snackInfoBinding.chronometer.setText(formatMiss(start));
        snackInfoBinding.chronometer.start();
        snackInfoBinding.chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                miss++;
                long time = SystemClock.elapsedRealtime() + start - chronometer.getBase();
                snackInfoBinding.chronometer.setText(formatMiss(time));
            }
        });

        snackInfoBinding.tvBattery.setText(ebikeSoc + "%");
        //添加关锁按钮-仅针对无锁欧展特定车
        snackInfoBinding.llLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog(getString(R.string.locking), R.mipmap.ic_locking);
                ebikeMac = null;
                setPowerOff = true;
//                showDialog("Locking");
                mHandler.sendEmptyMessageDelayed(LOCK_TIMEOUT, LOCK_TIMEOUT_VALUE);
//                if (!isBleConnect || !bluetoothClient.isBluetoothOpened()) {
                viewModel.ioT4GLock(ebikeId);
//                }
            }
        });
    }

    private String formatMiss(long time) {
        int h = (int) (time / 3600000);
        int m = (int) (time - h * 3600000) / 60000;
        int s = (int) (time - h * 3600000 - m * 60000) / 1000;
        String hh = h < 10 ? "0" + h : h + "";
        String mm = m < 10 ? "0" + m : m + "";
        String ss = s < 10 ? "0" + s : s + "";
        return hh + ":" + mm + ":" + ss;
    }

    /**
     * 设置数据观察者
     */
    private void setDataObserve() {
        viewModel.getLoginData().observe(this, loginBeanMyResult -> {
            UserUtil.token = loginBeanMyResult.getToken();
            UserUtil.isLogin = true;
            SPUtils.getInstance().put("token", UserUtil.token);
            //获取最近一次骑行
            viewModel.findLastCycle(ebikeId);
            //启动服务
            startJWebSClientService();
            //绑定服务
            bindService();
            //注册广播
            doRegisterReceiver();
            if (!hasGetlist) {
                getEbikeList();
            }
            if (UserUtil.isLogin) {
                //获取电子围栏
                viewModel.findRegionFencePoints();
            }
        });

        viewModel.getGisResult().observe(this, ebikeGisResponseMyResult -> {
            ebikeGisList = ebikeGisResponseMyResult.getIotEbikeProfileList();
            if (isMapLoadFinished) {
                setMarkerdOnMap();
            }
        });

        viewModel.getEbikeDetailResult().observe(this, detailResponseMyResult -> {
            EBikeDetail detail = detailResponseMyResult.getIotEbike();
            ebikeId = detail.getEbikeId();
            ebikeMac = detail.getMac().toUpperCase(Locale.ROOT);
            ebikeSoc = detail.getSoc().toString();
            snackInfoBinding.tvUnlockCode.setText(detail.getEbikeId());
            if (isInRide) {
                snackInfoBinding.tvBattery.setText(detail.getSoc() + "%");
            } else {
                //扫码查询车辆信息
                setUnlockView();
                snackInfoBinding.tvSoc.setText(detail.getSoc() + "%");

                snackInfoBinding.tvUnlock.setOnClickListener(v -> {
                    //todo 4G解锁
                    showWaitDialog(getString(R.string.unlocking), R.mipmap.ic_unlocking);
                    if (isBleConnect && bluetoothClient.isBluetoothOpened()) {
                        //向服务器申请蓝牙开锁
                        viewModel.btUnlockApply(detail.getEbikeId());
                    } else {
                        viewModel.iot4GUnlock(detail.getEbikeId());
                    }
                });

                snackInfoBinding.ivClose.setOnClickListener(v -> {
                    //todo 取消使用，断开蓝牙连接（如果已连接）
                    ebikeId = null;
                    ebikeMac = null;
                    setCustomView();
                    getEbikeList();
                    disConnectBle();
                });
                hasNotifyUnlock = false;
                hasNotifyLock = false;

                //拿到设备mac地址，如果蓝牙开着，连接蓝牙
                if (bluetoothClient.isBluetoothOpened()) {
                    connect(ebikeMac);
                }
            }
        });

        viewModel.getIotUnlockResult().observe(this, emptyBeanMyResult -> {
            //等待推送过来的开锁消息
//            showDialog(getString(R.string.unlocking));
            mHandler.sendEmptyMessageDelayed(UNLOCK_TIMEOUT, UNLOCK_TIMEOUT_VALUE);
        });

        viewModel.getBtApplyResult().observe(this, emptyBeanMyResult -> {
            //发送开锁指令
            writeMessage(LDBLCmd.bikePowerOn());
            //等待推送过来的开锁消息
            mHandler.sendEmptyMessageDelayed(UNLOCK_TIMEOUT, UNLOCK_TIMEOUT_VALUE);
        });

        viewModel.getNotifyUnlockResult().observe(this, notifyUnLockResponseMyResult -> {
            //解锁成功通知，保存cycleId,开始骑行
            UserUtil.cycleId = notifyUnLockResponseMyResult.getCycleOrder().getCycleId();
            UserUtil.ebikeId = ebikeId;
            startRide(0);
        });

        viewModel.getNotifylockResult().observe(this, new Observer<CreatePayOrderBean>() {
            @Override
            public void onChanged(CreatePayOrderBean createPayOrderBean) {
                mHandler.removeMessages(LOCK_TIMEOUT);
                dismissWaitDialog();
                dealOrder(createPayOrderBean.getPayOrder());
            }
        });

        viewModel.getLastCycleResult().observe(this, cycleOrderRecordsMyResult -> {
            // 关锁成功，创建订单，跳至付款页面
            CycleOrderRecords order = cycleOrderRecordsMyResult.getCycleOrder();
            if (order != null) {
                dealLastCycleOrder(order);
            }
        });

        viewModel.getRegionFenceResult().observe(this, regionFenceResponseMyResult -> {
            List<RegionFenceResponse.RegionFencePointsDTO> list = regionFenceResponseMyResult.getRegionFencePoints();
            if (list != null && list.size() > 0) {
                dealRegionData(list);
            }
        });
    }

    /**
     * 绘制围栏
     *
     * @param list
     */
    private void dealRegionData(List<RegionFenceResponse.RegionFencePointsDTO> list) {
        //TODO 先隐藏
//        for (RegionFenceResponse.RegionFencePointsDTO fence : list) {
//            List<Point> points = new ArrayList<>();
//            List<RegionFencePointListDTO> regionPoints = fence.getRegionFencePointList();
//            if (regionPoints != null) {
//                regionPoints.sort(Comparator.comparing(RegionFencePointListDTO::getSerialNo));
//                for (RegionFencePointListDTO point : regionPoints) {
//                    if (!StringUtils.isEmpty(point.getLat()) && !StringUtils.isEmpty(point.getLng())) {
//                        //double[] c = GpsCoordinateUtils.calWGS84toGCJ02(Double.parseDouble(point.getLat()), Double.parseDouble(point.getLng()));
//                        // points.add(new LatLng(c[0], c[1]));
//                        points.add(Point.fromLngLat(Double.parseDouble(point.getLng()), Double.parseDouble(point.getLat())));
//                    }
//                }
//                // Polygon fencePolygen = MapUtils.addPolygon(mMap, points, null);
//                mapBoxUtils.createPolygon(points);
//
//            }
//        }
    }

    /**
     * 处理上一次未完成的骑行
     *
     * @param order
     */
    private void dealLastCycleOrder(CycleOrderRecords order) {
        if (order.getCycleStatus() == 1) {
            dataBinding.llScan.setVisibility(View.INVISIBLE);
            //正在骑行中，提示用户
            UnfinishedDialog dialog = new UnfinishedDialog(MapsActivity.this, 1);
            dialog.setOnDismissListener(dialogInterface -> dataBinding.llScan.setVisibility(View.VISIBLE));
            dialog.setDialogCallback(new UnfinishedDialog.DialogCallback() {
                @Override
                public void ok() {
                    //当前骑行的id
                    UserUtil.cycleId = order.getCycleId();
                    ebikeId = order.getEbikeId();
                    UserUtil.ebikeId = order.getEbikeId();
                    String startTime = order.getStartTime();
                    viewModel.getEbikeDetail(ebikeId);
                    SimpleDateFormat formater = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                    try {
                        Date startDate = formater.parse(startTime);
                        //计时器
                        startRide(System.currentTimeMillis() - startDate.getTime());
                    } catch (Exception e) {
                    }

                }

                @Override
                public void end() {
                    showWaitDialog(getString(R.string.locking), R.mipmap.ic_locking);
                    UserUtil.cycleId = order.getCycleId();
                    ebikeId = order.getEbikeId();
                    UserUtil.ebikeId = order.getEbikeId();
                    isInRide = true;
                    setPowerOff = true;

//                showDialog("Locking");
                    mHandler.sendEmptyMessageDelayed(LOCK_TIMEOUT, LOCK_TIMEOUT_VALUE);
//                if (!isBleConnect || !bluetoothClient.isBluetoothOpened()) {
                    viewModel.ioT4GLock(ebikeId);
                }
            });
            dialog.show();
        } else if (order.getPayOrder() != null) {
            if (order.getPayOrder().getOrderStatus() != 2) {
                dataBinding.llScan.setVisibility(View.INVISIBLE);
                //有未支付订单，提示用户
                UnfinishedDialog dialog = new UnfinishedDialog(MapsActivity.this, 0);
                dialog.setOnDismissListener(dialogInterface -> dataBinding.llScan.setVisibility(View.VISIBLE));
                dialog.setDialogCallback(new UnfinishedDialog.DialogCallback() {
                    @Override
                    public void ok() {
                        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                        intent.putExtra("order", new Gson().toJson(order.getPayOrder()));
                        intent.putExtra("type", "order");
                        startActivity(intent);
                    }

                    @Override
                    public void end() {

                    }
                });
                dialog.show();
            }
        }
    }

    private void setInRideView() {
        //暂不现实骑行按钮，等蓝牙连上有数据之后再显示
        //snackInfoBinding.ivRide.setVisibility(View.VISIBLE);
        dataBinding.llScan.setVisibility(View.GONE);
        mapBoxUtils.clearMarkers();
        snackInfoBinding.getRoot().setVisibility(View.VISIBLE);
        snackInfoBinding.ivClose.setVisibility(View.GONE);
        snackInfoBinding.rlRiding.setVisibility(View.VISIBLE);
        snackInfoBinding.rlUnlock.setVisibility(View.GONE);
        dataBinding.ivRefresh.setVisibility(View.GONE);
        dataBinding.ivRide.setOnClickListener(v -> {
            showRideSetDialog();
        });
    }

    private void setUnlockView() {
        dataBinding.llScan.setVisibility(View.GONE);
        mapBoxUtils.clearMarkers();
        snackInfoBinding.getRoot().setVisibility(View.VISIBLE);
        snackInfoBinding.rlRiding.setVisibility(View.GONE);
        snackInfoBinding.rlUnlock.setVisibility(View.VISIBLE);
    }

    private void setCustomView() {
        dataBinding.llScan.setVisibility(View.VISIBLE);
        snackInfoBinding.getRoot().setVisibility(View.GONE);
    }

    /**
     * 收到推送的账单后展示
     */
    private void showFinishView(String order, CreatePayOrderBean.PayOrder payOrder) {
        //todo 将地图范围显示整个路径,添加起点和终点point
        if (polyline != null) {
            List<Point> points = polyline.getPoints();
            mapBoxUtils.addTraceMarker(points.get(0), R.mipmap.ic_point_start);
            mapBoxUtils.addLineMarker(points.get(points.size() - 1), R.mipmap.ic_point_end);
            mapBoxUtils.boundsCameraByPoints(points, 50, 50, 100, 50);
        }
        if (rideDialog != null && rideDialog.isShowing()) {
            rideDialog.dismiss();
        }
        snackInfoBinding.getRoot().setVisibility(View.GONE);
        dataBinding.ivRide.setVisibility(View.GONE);
        payInfoBinding.getRoot().setVisibility(View.VISIBLE);
        payInfoBinding.tvCode.setText(ebikeId);
        payInfoBinding.tvCost.setText(String.format("%.2f", payOrder.getAmount() / 100f));
        payInfoBinding.tvTime.setText(snackInfoBinding.chronometer.getText());
        payInfoBinding.llGoPay.setOnClickListener(view -> {
            //todo跳至支付页面
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("order", order);
            startActivity(intent);
        });
    }

    private void connect(String mac) {
//        showDialog("");
//        mHandler.sendEmptyMessageDelayed(CONNECT_TIMEOUT, CONNECT_TIMEOUT_VALUE);
        if (BleUtils.deviceMap == null) {
            return;
        }
        if (!BleUtils.deviceMap.containsKey(mac.toUpperCase())) {
            LogUtil.e("未发现设备");
//            dismissDialog();
//            // setResult(2);
//            // finish();
        } else {
            //得到正确的设备
            Device device = BleUtils.deviceMap.get(mac.toUpperCase());
            if (device == null) {
                return;
            }

            BleUtils.connect(bluetoothClient, device, bleConnectStatusListener, new BleUtils.ConnectResult() {
                @Override
                public void success() {
                    isBleConnect = true;
                    getMeterData();
                    BleUtils.notifyBle(bluetoothClient, notifyResponse);
                }

                @Override
                public void fail() {
                    ToastUtils.showShort(R.string.connect_fail);
                }
            });
        }
    }

    private int sendCount;

    /**
     * 获取行车数据
     */
    private void getMeterData() {
        sendCount = 0;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isInRide && isBleConnect) {
                    if (sendCount == 1) {
                        writeMessage(LDBLCmd.iotGpsData());
                        sendCount = 0;
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (setLightOn) {
//                                    writeMessage(LDBLCmd.bikeLightOn());
//                                    setLightOn = false;
//                                } else if (setLightOff) {
//                                    writeMessage(LDBLCmd.bikeLightOff());
//                                    setLightOff = false;
//                                } else if (setPas) {
//                                    LogUtil.e(newpas + "");
//                                    writeMessage(LDBLCmd.bikeCPas(newpas));
//                                    setPas = false;
//                                } else if (setWalkOn) {
//                                    writeMessage(LDBLCmd.bikeWalk());
//                                } else if (setPowerOff) {
//                                    writeMessage(LDBLCmd.bikePowerOff());
//                                } else {
//                                    writeMessage(LDBLCmd.readBikeData());
//                                }
//                            }
//                        }, 100);
                    } else {
                        if (setLightOn) {
                            writeMessage(LDBLCmd.bikeLightOn());
                            setLightOn = false;
                        } else if (setLightOff) {
                            writeMessage(LDBLCmd.bikeLightOff());
                            setLightOff = false;
                        } else if (setPas) {
                            LogUtil.e(newpas + "");
                            writeMessage(LDBLCmd.bikeCPas(newpas));
                            setPas = false;
                        } else if (setWalkOn) {
                            writeMessage(LDBLCmd.bikeWalk());
                        } else if (setPowerOff) {
                            writeMessage(LDBLCmd.bikePowerOff());
                        } else {
                            writeMessage(LDBLCmd.readBikeData());
                        }
                        sendCount++;
                    }
                    mHandler.postDelayed(this, 250);
                }
            }
        }, 50);
    }

    private RideDialog rideDialog;

    private void showRideSetDialog() {
        if (rideDialog != null && rideDialog.isShowing()) {
            return;
        }
        rideDialog = new RideDialog(MapsActivity.this);
        rideDialog.show();
        rideDialog.setOnClickInterface(new RideDialog.OnClickInterface() {
            @Override
            public void onWalkStart() {
                setWalkOn = true;
            }

            @Override
            public void onWalkStop() {
                setWalkOn = false;
            }

            @Override
            public void onLightOff() {
                setLightOff = true;

            }

            @Override
            public void onLightOn() {
                setLightOn = true;
            }

            @Override
            public void onPas(int npas) {
                newpas = npas;
                setPas = true;
            }
        });
    }

    /**
     * 发送数据
     *
     * @param data
     */
    private void writeMessage(byte[] data) {
        if (ClientManager.getDevice() != null) {
            BleUtils.writeBle(bluetoothClient, data, writeResponse);
        }
    }

    BleWriteResponse writeResponse = new BleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

    /**
     * 在地图上绘制车辆点
     *
     * @param ebikeGisList
     */
    List<EbikeGis> ebikeGisList;

    private void setMarkerdOnMap() {
        mapBoxUtils.clearMarkers();
        if (ebikeGisList.size() > 0) {
            markers = new ArrayList<>();
            List<Point> points = new ArrayList<>();
            for (EbikeGis ebikeGis : ebikeGisList) {
                if (ebikeGis.getLat() != null && ebikeGis.getLng() != null) {
                    //double gps[] = GpsCoordinateUtils.calWGS84toGCJ02(Double.parseDouble((String) ebikeGis.getLat()), Double.parseDouble((String) ebikeGis.getLng()));
                    // LatLng point = new LatLng(gps[0], gps[1]);
//                    if (point.latitude == 0) {
//                        continue;
//                    }
//                    b.include(point);
                    // Marker marker = MapUtils.addMarker(mMap, point, R.mipmap.ic_pop_blue);
                    Point point = Point.fromLngLat(Double.parseDouble((String) ebikeGis.getLng()), Double.parseDouble((String) ebikeGis.getLat()));
                    points.add(point);
                    PointAnnotation pointAnnotation = mapBoxUtils.addBikeMarker(point, ebikeGis);
                    //                    marker.setTag(ebikeGis);
                    markers.add(pointAnnotation);
                }
            }
            mapBoxUtils.addPointClickListener();
//            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(b.build(),
//                    500));
//            if (points.size() > 0) {
//                mapBoxUtils.boundsCameraByPoints(points);
//            }

        }
    }

//    private void resetMarkerImage() {
//        if (selectedMarker != null) {
//            MapUtils.changeMarkerIcon(R.mipmap.ic_pop_blue, selectedMarker);
//        }
//    }

    private List<LatLng> locations = new ArrayList();//保存所有轨迹点

    /**
     * 绘制轨迹线
     *
     * @param latLngs
     */
    private void showGPSPolyline(List<Point> latLngs) {
        if (latLngs == null) {
            return;
        }
        if (polyline == null) {
            polyline = mapBoxUtils.createPolyline(latLngs);
        }
//        polyline.setPoints(latLngs);
//        polyline.setJointType(JointType.ROUND);
    }

    protected Handler mHandler = new Handler(Looper.myLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case UNLOCK_TIMEOUT:
                    dismissWaitDialog();
                    ToastUtils.showShort(R.string.unlock_fail_retry);
                    //解锁失败，断开蓝牙连接，界面重置
                    setCustomView();
                    //断开蓝牙连接
                    disConnectBle();
                    break;
                case LOCK_TIMEOUT:
                    dismissWaitDialog();
                    ToastUtils.showShort(R.string.lock_fail_retry);
                case CONNECT_TIMEOUT:
//                    dismissDialog();
                    break;
            }
            return false;
        }
    });

//    /**
//     * 接收位置点更新，在地图上绘制标记点
//     */
//    private class MyReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
//            if (location != null && mMap != null) {
//
//                LogUtil.e(LocationUtils.getLocationText(location));
//                double[] gps = GpsCoordinateUtils.calWGS84toGCJ02(location.getLatitude(), location.getLongitude());
//                currentLatLng = new LatLng(gps[0], gps[1]);
//                gLocOverlay.locationChanged(currentLatLng);
//                if (isInRide && !isBleConnect) {
//                    //绘制定位点
//                    locations.add(currentLatLng);
//                    showGPSPolyline(locations);
//                }
//            }
//        }
//    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        gLocOverlay = new GLocOverlay(mMap);
//        mMap.setOnMarkerClickListener(marker1 -> {
//            resetMarkerImage();
//            dataBinding.topInfo.getRoot().setVisibility(View.VISIBLE);
//            MapUtils.changeMarkerIcon(R.mipmap.ic_pop_green, marker1);
//            selectedMarker = marker1;
//            return false;
//        });
//        if (UserUtil.isLogin && !hasGetlist) {
//            //获取车辆列表
//            getEbikeList();
//        }
//        if (UserUtil.isLogin) {
//            //获取电子围栏
//            viewModel.findRegionFencePoints();
//        }
//
//    }

//    /**
//     * 开始获取定位
//     */
//    public void startLocation() {
//        mService.requestLocationUpdates();
//    }
//
//    /**
//     * 停止获取定位
//     */
//    public void stopLocation() {
//        mService.removeLocationUpdates();
//    }

    /**
     * 进入二维码扫描页
     */
    public void scanQr() {
        String colorStatusBar = String.format("#%06X", (0xFFFFFF & getColor(R.color.green_left)));
        String colorLineDot = String.format("#%06X", (0xFFFFFF & getColor(R.color.green_left)));
        String colorResultPointStroke = String.format("#%06X", (0xFFFFFF & getColor(R.color.green_right)));
        ScanConfig scanConfig = new ScanConfig.Builder()
                //设置完成震动
                .isShowVibrate(true)
                //扫描完成声音
                .isShowBeep(true)
                //显示相册功能
                .isShowPhotoAlbum(false)
                //显示闪光灯
                .isShowLightController(false)
                //打开扫描页面的动画
                //.setActivityOpenAnime(R.anim.activity_anmie_in)
                //退出扫描页面动画
                .setActivityExitAnime(R.anim.scan_activity_out)
                //自定义文案
                .setScanHintText(getString(R.string.scan_qr_code))
                //                                .setScanHintTextColor("#FF0000")
                //                                .setScanHintTextSize(14)
                //扫描线的颜色
                .setScanColor(colorLineDot)
                //扫描线样式
                //.setLaserStyle(ScanConfig.LaserStyle.Grid/ScanConfig.LaserStyle.Line)
                //背景颜色
                // .setBgColor("")

                //网格扫描线的列数
//                .setGridScanLineColumn(30)
                //网格高度
//                .setGridScanLineHeight(300)
                //是否全屏扫描,默认全屏
                .setFullScreenScan(false)
                //单位dp
                .setResultPointConfigs(36, 12, 3, colorResultPointStroke, colorLineDot)
                //状态栏设置
                .setStatusBarConfigs(colorStatusBar, true)
                //自定义遮罩
                .setCustomShadeViewLayoutID(R.layout.scan_qr_view, customView -> {
                    if (customView == null) {
                        return;
                    }

                    RelativeLayout bleLayout = customView.findViewById(R.id.rl_bluetooth);
                    Button button = bleLayout.findViewById(R.id.bt_open);
                    button.setOnClickListener(view -> {
                        bluetoothClient.openBluetooth();
                        bleScanService.startScan(bluetoothClient);
                        bleLayout.setVisibility(View.GONE);
                    });
                    if (bluetoothClient == null || !bluetoothClient.isBluetoothOpened()) {
                        bleLayout.setVisibility(View.VISIBLE);
                    } else {
                        bleLayout.setVisibility(View.GONE);
                    }

                    customView.findViewById(R.id.iv_back).setOnClickListener(view1 -> ScanManager.closeScanPage());
                    ImageView imageViewLight = customView.findViewById(R.id.iv_scan_light);


                    LinearLayout helpLayout = customView.findViewById(R.id.btn_help);
                    helpLayout.setOnClickListener(view -> {
                    });

                    LinearLayout codeLayout = customView.findViewById(R.id.btn_code);
                    codeLayout.setOnClickListener(view -> {
                        //跳转至输入车辆编码页
                        ScanManager.closeScanPage();
                        Intent intent = new Intent(MapsActivity.this, EnterCodeActivity.class);
                        ActivityUtils.startActivityForResult(MapsActivity.this, intent, 0);
                    });

                    LinearLayout lightLayout = customView.findViewById(R.id.btn_light);
                    lightLayout.setOnClickListener(view -> {
                        if (mIsFlashOn) {
                            ScanManager.closeScanLight();
                            imageViewLight.setImageResource(R.mipmap.ic_flash_off);
                            mIsFlashOn = false;
                        } else {
                            ScanManager.openScanLight();
                            imageViewLight.setImageResource(R.mipmap.ic_flash_on);
                            mIsFlashOn = true;
                        }
                    });

                }).builder();


        ScanManager.startScan(MapsActivity.this, scanConfig, (resultCode, data) -> {
            switch (resultCode) {
                case ScanManager.RESULT_SUCCESS:
                    ArrayList<String> resultSuccess = data.getStringArrayListExtra(ScanManager.INTENT_KEY_RESULT_SUCCESS);
                    LogUtils.d(CollectionUtils.toString(resultSuccess));
                    resultSuccess.size();
                    String code = resultSuccess.get(0);
                    // ToastUtils.showLong(data.getStringExtra("result"));
//                    Intent intent = new Intent(getApplicationContext(), UnLockActivity.class);
//                    intent.putExtra("code", code);
//                    launcher.launch(intent);

                    //获取车辆信息，展示开锁提示框
                    viewModel.getEbikeDetail(code);

                    break;
                case ScanManager.RESULT_FAIL:
                    String resultError = data.getStringExtra(ScanManager.INTENT_KEY_RESULT_ERROR);
                    LogUtils.d(resultError);
                    break;
                case ScanManager.RESULT_CANCLE:
                    //showToast("取消扫码");
                    break;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == 1) {
            String code = data.getStringExtra("code");
//            Intent intent = new Intent(getApplicationContext(), UnLockActivity.class);
//            intent.putExtra("code", code);
//            launcher.launch(intent);
            //todo 获取车辆信息，展示开锁提示框
            try {
                viewModel.getEbikeDetail(code);
                viewModel.getEbikeAgreement(code);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 监听手机位置
//        registerReceiver(myReceiver,
//                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        startScanTimer();
    }

    private boolean hasGetlist = false;

    private void getEbikeList() {
        hasGetlist = true;
        viewModel.findEbikeGis();
    }

    @Override
    public void onBackPressed() {
        if (dataBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            dataBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (snackInfoBinding.getRoot().getVisibility() == View.VISIBLE) {
            if (!isInRide) {
                snackInfoBinding.getRoot().setVisibility(View.GONE);
                dataBinding.llScan.setVisibility(View.VISIBLE);
            } else {
                ActivityManager.getInstance().AppExit();
            }
        } else {
            ActivityManager.getInstance().AppExit();
        }
    }

    @Override
    protected void onPause() {
        hasGetlist = false;
        //  unregisterReceiver(myReceiver);
        if (timer != null) {
            timerTask.cancel();
            timer.cancel();
            timer = null;
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
//        if (mService != null) {
//            mService.removeLocationUpdates();
//        }
//        if (mBound) {
//            unbindService(locationServiceConnection);
//            mBound = false;
//        }

        dataBinding.mapView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtil.e("destroy");
        if (bluetoothClient != null && ClientManager.getDevice() != null) {
            BleUtils.unIndicateBle(bluetoothClient, new BleUnnotifyResponse() {
                @Override
                public void onResponse(int i) {

                }
            });
            BleUtils.disConnect(bluetoothClient);
            ClientManager.setDevice(null);
            bluetoothClient.unregisterBluetoothStateListener(stateListener);
        }
        if (socketServiceConnection != null && jWebSClientService != null) {
            unbindService(socketServiceConnection);
        }
        if (chatMessageReceiver != null) {
            unregisterReceiver(chatMessageReceiver);
        }
        mapBoxUtils.onDestroy();
        super.onDestroy();
    }

    /**
     * 切换语言
     */
    private void showSwitchLanguageDialog() {
        CharSequence[] arrays = {getString(R.string.follow_system), "English", "中文简体"};
        new CircleDialog.Builder().setItems(arrays, (view, position) -> {
            switch (position) {
                case 0:
                    LanguageUtil.updateLanguage(LanguageUtils.getSystemLanguage());
                    break;
                case 1:
                    LanguageUtil.updateLanguage(Locale.ENGLISH);
                    break;
                case 2:
                    LanguageUtil.updateLanguage(Locale.CHINESE);
                    break;
            }
            return true;
        }).show(getSupportFragmentManager());
    }

    @Override
    protected CycleViewModel initViewModel() {
        return ViewModelProviders.of(this).get(CycleViewModel.class);
    }

    @Override
    protected void showError(Object o) {
        if (o instanceof Error) {
            Error error = (Error) o;
            switch (error.getCode()) {
                case 400:
                    ToastUtils.showShort(error.getMsg());
                    //令牌失效
                    stopService(new Intent(this, JWebSocketClientService.class));
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    ActivityUtils.finishAllActivities();
                    break;
                case 403:
                    ToastUtils.showShort("无权访问");
                    break;
                case 504:
                    ToastUtils.showShort("timeout");
                    break;
                default:
                    ToastUtils.showShort(((Error) o).getMsg());
                    break;
            }
        }
    }

    /**
     * 绑定服务
     */
    private void bindService() {
        Intent bindIntent = new Intent(this, JWebSocketClientService.class);
        bindService(bindIntent, socketServiceConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection socketServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtil.d("服务与活动绑定");
            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
            client = jWebSClientService.client;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            LogUtil.d("服务与活动断开");
        }
    };

    /**
     * 启动服务（websocket客户端服务）
     */
    private void startJWebSClientService() {
        Intent intent = new Intent(this, JWebSocketClientService.class);
        startService(intent);
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        if (jWebSClientService != null && client == null) {
            client = jWebSClientService.client;
        }

        if (null != client) {
            LogUtil.d("JWebSocketClientService", "发送的消息：" + msg);
            if (client.getReadyState().equals(ReadyState.OPEN)) {
                client.send(msg);
            }
        }
    }

    /**
     * 动态注册广播
     */
    private void doRegisterReceiver() {
        chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter(Constant.WEBSOCKET_SERVICE_ACTION);
        registerReceiver(chatMessageReceiver, filter);
    }

    /**
     * websocket消息接收及处理
     */
    private class ChatMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            LogUtil.e("收到推送--" + message);
            JSONObject object = null;
            try {
                object = new JSONObject(message);
                dismissWaitDialog();
                if (object.getInt("msgType") == 1) {
                    //收到推送的支付订单
                    if (object.getString("bizCode").equals("payOrder")) {
                        mHandler.removeMessages(LOCK_TIMEOUT);
                        dealOrder(object.getString("content"));
                    } else if (object.getString("bizCode").equals("cycleOrder")) {
                        //开始骑行
                        CycleOrder cycleOrder = new Gson().fromJson(object.getString("content"), CycleOrder.class);
                        UserUtil.cycleId = cycleOrder.getCycleId();
                        UserUtil.ebikeId = ebikeId;
                        startRide(0);
                    }
                } else {
                    if (object.getString("bizCode").equals("authFail")) {
                        Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent1);
                        finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void disConnectBle() {
        if (isBleConnect && ClientManager.getDevice() != null && bluetoothClient != null) {
            //清空蓝牙连接
            BleUtils.unRegisterConnectStatus(bluetoothClient, bleConnectStatusListener);
            BleUtils.disConnect(bluetoothClient);
            ClientManager.setDevice(null);
        }
    }

    /**
     * 骑行结束，传递订单信息
     *
     * @param order
     */
    private void dealOrder(String order) {
        ebikeId = null;
        ebikeMac = null;
        if (!isInRide) {
            return;
        }
        CreatePayOrderBean.PayOrder payOrder = new Gson().fromJson(order, CreatePayOrderBean.PayOrder.class);
        if (payOrder.getCycleId().equals(UserUtil.cycleId)) {
            //结束骑行
            isInRide = false;
//            mHandler.removeMessages(LOCATION_TIMEOUT);
//            mHandler.removeMessages(GYRO_TIMEOUT);
            mHandler.removeMessages(CONTROLLER_TIMEOUT);
            disConnectBle();
            showFinishView(order, payOrder);


//            finish();
        }
    }

    /**
     * 骑行结束，传递订单信息
     *
     * @param payOrder
     */
    private void dealOrder(CreatePayOrderBean.PayOrder payOrder) {
        ebikeId = null;
        ebikeMac = null;
        if (!isInRide) {
            return;
        }
        if (payOrder.getCycleId().equals(UserUtil.cycleId)) {
            //结束骑行
            isInRide = false;
//            mHandler.removeMessages(LOCATION_TIMEOUT);
//            mHandler.removeMessages(GYRO_TIMEOUT);
            mHandler.removeMessages(CONTROLLER_TIMEOUT);
            disConnectBle();
            showFinishView(new Gson().toJson(payOrder), payOrder);


//            finish();
        }
    }

    /**
     * 返回故障描述
     *
     * @param code
     * @return
     */
    private String getError(byte code) {
        String error = "";
        switch (code) {
            case 0x01:
                error = "过流保护故障";
                break;
            case 0x02:
                error = "欠压保护故障";
                break;
            case 0x03:
                error = "过压保护故障";
                break;
            case 0x04:
                error = "堵转保护故障";
                break;
            case 0x05:
                error = "驱动mos管上桥故障";
                break;
            case 0x06:
                error = "驱动mos管下桥故障";
                break;
            case 0x07:
                error = "霍尔故障";
                break;
            case 0x08:
                error = "控制器内部过温";
                break;
            case 0x09:
                error = "刹把故障";
                break;
            case 0x10:
                error = "转把故障";
                break;
            case 0x12:
                error = "控制器通信故障";
                break;
            case 0x13:
                error = "电池通信故障";
                break;
            case 0x31:
                error = "电机过温";
                break;
            case 0x33:
                error = "电池过温";
                break;
            case 0x40:
                error = "后刹车故障";
                break;
            case 0x00:
                error = "空闲无故障";
                break;
        }
        return error;
    }

    @Override
    public void onStart() {
        super.onStart();
        dataBinding.mapView.onStart();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        dataBinding.mapView.onLowMemory();
    }

    private LockLoadingDialog loadingDialog;

    /**
     * 显示用户等待框
     *
     * @param msg 提示信息
     */
    protected void showWaitDialog(String msg, int resId) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.setLoadingMsg(msg, resId);
        } else {
            loadingDialog = new LockLoadingDialog(context);
            loadingDialog.setLoadingMsg(msg, resId);
            loadingDialog.show();
        }
    }

    /**
     * 隐藏等待框
     */
    protected void dismissWaitDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}

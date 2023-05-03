package com.wxh.shareiot.api;


import com.wxh.basiclib.http.MyResult;
import com.wxh.basiclib.http.ResponseResult;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    /**
     * 验证码登录接口
     */
    @POST("login/appCodeLogin")
    Observable<ResponseResult<MyResult<LoginBean>>> codelogin(@Body RequestBody body);

    /**
     * 账号密码登录接口
     */
    @POST("auth/appLogin")
    Observable<ResponseResult<MyResult<LoginBean>>> login(@Body RequestBody body);

    /**
     * 令牌登录接口
     *
     * @param body
     * @return
     */
    @POST("auth/tokenLogin")
    Observable<ResponseResult<MyResult<LoginBean>>> tokenLogin(@Body RequestBody body);

    /**
     * 获取注册验证码接口
     *
     * @param body
     * @return
     */
    @POST("auth/getRegiserUserSmsCode")
    Observable<ResponseResult<MyResult<EmptyBean>>> getSmsCode(@Body RequestBody body);

    /**
     * 注册手机账号
     *
     * @param body
     * @return
     */
    @POST("auth/registerPhoneUser")
    Observable<ResponseResult<MyResult<EmptyBean>>> registerPhoneUser(@Body RequestBody body);

    /**
     * 查询余额
     *
     * @param body
     * @return
     */
    @POST("recharge/getUserBalance")
    Observable<ResponseResult<MyResult<UserBalanceResponse>>> getUserBalance(@Header("authorization") String token,@Body RequestBody body);

    /**
     * 创建充值订单
     * @param body
     * @return
     */
    @POST("recharge/createOrder")
    Observable<ResponseResult<MyResult<CreateRechargeOrderResponse>>> createRechargeOrder(@Header("authorization") String token,@Body RequestBody body);

    /**
     * 查询充值分页
     * @param body
     * @return
     */
    @POST("recharge/findRecharge")
    Observable<ResponseResult<MyResult<RechargeListResponse>>> findRechargeList(@Header("authorization") String token,@Body RequestBody body);



//    @POST("remote/bindDevice")
//    Observable<ResponseResult<MyResult<EmptyBean>>> bindDevice(@Header("authorization") String token,@Body RequestBody body);
//
//    @POST("remote/unbindDevice")
//    Observable<ResponseResult<MyResult<EmptyBean>>> unbindDevice(@Header("authorization") String token,@Body RequestBody body);
//
//    @POST("remote/ttface")
//    Observable<ResponseResult<MyResult<SendMsgBean>>> sendMsg(@Header("authorization") String token,@Body RequestBody body);
//
//    @POST("cycle/startCycle")
//    Observable<ResponseResult<MyResult<CycleBean>>> startCycle(@Header("authorization") String token,@Body RequestBody body);
//
//    @POST("payOrder/createPayOrder/")
//    Observable<ResponseResult<MyResult<CreatePayOrderBean>>> createPayOrder(@Header("authorization") String token,@Body RequestBody body);

    /**
     * 获取微信支付信息
     *
     * @param token
     * @param body
     * @return
     */
    @POST("pay/wxAppPay")
    Observable<ResponseResult<MyResult<WechatResponse>>> wechatPay(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 获取支付宝支付信息
     *
     * @param token
     * @param body
     * @return
     */
    @POST("pay/zfbAppPay")
    Observable<ResponseResult<MyResult<ZfbResponse>>> zfbPay(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 余额支付
     * @param token
     * @param body
     * @return
     */
    @POST("pay/balancePay")
    Observable<ResponseResult<MyResult<EmptyBean>>> balancePay(@Header("authorization") String token, @Body RequestBody body);

    @POST("ebike/findEbikeDetail")
    Observable<ResponseResult<MyResult<EbikeDetailResponse>>> getEbikeDetail(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 蓝牙开锁申请
     */

    @POST("cycle/btUnlockApply")
    Observable<ResponseResult<MyResult<EmptyBean>>> BtUnlockApply(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 4g开锁
     *
     * @param token
     * @param body
     * @return
     */
    @POST("cycle/iot4GUnlock")
    Observable<ResponseResult<MyResult<EmptyBean>>> iot4GUnlock(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 保存控制器参数
     *
     * @param token
     * @param body
     * @return
     */
    @POST("ebike/saveControllerConfig")
    Observable<ResponseResult<MyResult<EmptyBean>>> saveConfig(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 查询车辆GIS分布
     *
     * @param token
     * @return
     */
    @POST("ebike/findEbikeGisProfileList")
    Observable<ResponseResult<MyResult<EbikeGisResponse>>> findEbikeGis(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 查询骑行列表
     *
     * @param token
     * @return
     */
    @POST("cycle/findMyCyclePayPage")
    Observable<ResponseResult<MyResult<CycleListResponse>>> findCycleList(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 查询骑行列表
     *
     * @param token
     * @param body
     * @return
     */
    @POST("cycle/findMyCycleTraceList")
    Observable<ResponseResult<MyResult<CycleTraceResponse>>> findTrace(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 保存5S设置参数
     *
     * @param token
     * @param body
     * @return
     */
    @POST("ebike/saveControllerAgreement")
    Observable<ResponseResult<MyResult<EmptyBean>>> save5sController(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 查询5S设置参数
     *
     * @param token
     * @param body
     * @return
     */
    @POST("ebike/findControllerAgreement")
    Observable<ResponseResult<MyResult<Km5sSettingData>>> find5sController(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 上锁后通知服务器
     *
     * @param token
     * @param body
     * @return
     */
    @POST("cycle/notifyLockedSuccess")
    Observable<ResponseResult<MyResult<CreatePayOrderBean>>> dealLockSuccess(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 上锁后通知服务器
     *
     * @param token
     * @param body
     * @return
     */
    @POST("cycle/notifyUnlockedSuccess")
    Observable<ResponseResult<MyResult<NotifyUnLockResponse>>> dealunLockSuccess(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 通知上锁
     *
     * @param token
     * @param body
     * @return
     */
    @POST("cycle/iot4GLock")
    Observable<ResponseResult<MyResult<EmptyBean>>> iot4Glock(@Header("authorization") String token, @Body RequestBody body);

    /**
     * 获取最近一次骑行
     *
     * @param token
     * @param body
     * @return
     */
    @POST("cycle/findUserLastCycleOrder")
    Observable<ResponseResult<MyResult<LastCycleResponse>>> getLastCycle(@Header("authorization") String token, @Body RequestBody body);

    @POST("fence/findRegionFencePoints")
    Observable<ResponseResult<MyResult<RegionFenceResponse>>> findRegionFencePoints(@Header("authorization") String token, @Body RequestBody body);
}

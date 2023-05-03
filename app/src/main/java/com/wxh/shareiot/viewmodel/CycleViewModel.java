package com.wxh.shareiot.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.wxh.basiclib.http.HttpManager;
import com.wxh.basiclib.http.IHttpResult;
import com.wxh.basiclib.lifecycle.BaseViewModel;
import com.wxh.shareiot.api.ApiHolder;
import com.wxh.shareiot.api.CreatePayOrderBean;
import com.wxh.shareiot.api.CycleBean;
import com.wxh.shareiot.api.EbikeDetailResponse;
import com.wxh.shareiot.api.EbikeGisResponse;
import com.wxh.shareiot.api.EmptyBean;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.api.Km5sSettingData;
import com.wxh.shareiot.api.LastCycleResponse;
import com.wxh.shareiot.api.LoginBean;
import com.wxh.shareiot.api.NotifyUnLockResponse;
import com.wxh.shareiot.api.RegionFenceResponse;
import com.wxh.shareiot.utils.UserUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class CycleViewModel extends BaseViewModel {
    protected MutableLiveData<LoginBean> loginResult = new MutableLiveData<>();
    protected MutableLiveData<CreatePayOrderBean> orderResult = new MutableLiveData<>();
    protected MutableLiveData<EmptyBean> btApplyResult = new MutableLiveData<>();
    protected MutableLiveData<CycleBean> cycleResult = new MutableLiveData<>();
    protected MutableLiveData<EbikeGisResponse> gisResult = new MutableLiveData<>();
    protected MutableLiveData<Km5sSettingData> agreeMentResult = new MutableLiveData<>();
    protected MutableLiveData<EbikeDetailResponse> ebikeDetailResult = new MutableLiveData<>();
    protected MutableLiveData<EmptyBean> iotUnlockResult = new MutableLiveData<>();
    protected MutableLiveData<NotifyUnLockResponse> notifyUnlockResult = new MutableLiveData<>();
    protected MutableLiveData<CreatePayOrderBean> notifylockResult = new MutableLiveData<>();
    protected MutableLiveData<LastCycleResponse> lastCycleResult = new MutableLiveData<>();
    protected MutableLiveData<RegionFenceResponse> regionFenceResult = new MutableLiveData<>();

    public MutableLiveData<RegionFenceResponse> getRegionFenceResult() {
        return regionFenceResult;
    }

    public MutableLiveData<LastCycleResponse> getLastCycleResult() {
        return lastCycleResult;
    }

    public MutableLiveData<NotifyUnLockResponse> getNotifyUnlockResult() {
        return notifyUnlockResult;
    }

    public MutableLiveData<CreatePayOrderBean> getNotifylockResult() {
        return notifylockResult;
    }

    public MutableLiveData<EmptyBean> getBtApplyResult() {
        return btApplyResult;
    }

    public MutableLiveData<EmptyBean> getIotUnlockResult() {
        return iotUnlockResult;
    }

    public MutableLiveData<Km5sSettingData> getAgreeMentResult() {
        return agreeMentResult;
    }

    public MutableLiveData<EbikeDetailResponse> getEbikeDetailResult() {
        return ebikeDetailResult;
    }

    public MutableLiveData<LoginBean> getLoginData() {
        return loginResult;
    }

    public MutableLiveData<CreatePayOrderBean> getPayOrderData() {
        return orderResult;
    }

    public MutableLiveData<CycleBean> getCycleData() {
        return cycleResult;
    }

    public MutableLiveData<EbikeGisResponse> getGisResult() {
        return gisResult;
    }

    public void tokenLogin(String token) throws Exception {
        showDialog.setValue(false);
        JSONObject object = new JSONObject();
        object.put("token", token);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().tokenLogin(body),
                new IHttpResult<LoginBean>() {
                    @Override
                    public void success(LoginBean myResultResponseResult) {
                        showDialog.setValue(false);
                        loginResult.setValue(myResultResponseResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }

                });
        addDisposable(disposable);
    }

    public void saveConfig(HashMap<String, String> config) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(config));
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().saveConfig(UserUtil.token, body),
                new IHttpResult<EmptyBean>() {
                    @Override
                    public void success(EmptyBean myResultResponseResult) {

                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }
                });
        addDisposable(disposable);
    }

    public void findEbikeGis() {
        JSONObject object = new JSONObject();
        try {
            object.put("status", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().findEbikeGis(UserUtil.token, body),
                new IHttpResult<EbikeGisResponse>() {
                    @Override
                    public void success(EbikeGisResponse myResultResponseResult) {
                        gisResult.setValue(myResultResponseResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }
                });
        addDisposable(disposable);
    }

//    public void startCycle() throws Exception {
//        showDialog.setValue(false);
//        JSONObject object = new JSONObject();
//        object.put("ebikeId", "ebike002");
//        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
//
//        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().startCycle(UserUtil.token, body),
//                new IHttpResult<ResponseResult<MyResult<CycleBean>>>() {
//                    @Override
//                    public void success(ResponseResult<MyResult<CycleBean>> myResultResponseResult) {
//                        showDialog.setValue(false);
//                        cycleResult.setValue(myResultResponseResult.getResult());
//                    }
//
//                    @Override
//                    public void error(Throwable throwable) {
//                        showDialog.setValue(false);
//                        error.setValue("发生错误了");
//                        LogUtils.e(throwable.getMessage());
//                    }
//                });
//        addDisposable(disposable);
//    }
//
//    public void createPayOrder() throws Exception {
//        showDialog.setValue(true,"计算中...");
//        JSONObject object = new JSONObject();
//        object.put("cycleId", UserUtil.cycleId);
//        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
//
//        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().createPayOrder(UserUtil.token, body),
//                new IHttpResult<ResponseResult<MyResult<CreatePayOrderBean>>>() {
//                    @Override
//                    public void success(ResponseResult<MyResult<CreatePayOrderBean>> myResultResponseResult) {
//                        showDialog.setValue(false);
//                        orderResult.setValue(myResultResponseResult.getResult());
//                    }
//
//                    @Override
//                    public void error(Throwable throwable) {
//                        showDialog.setValue(false);
//                        error.setValue("发生错误了");
//                        LogUtils.e(throwable.getMessage());
//                    }
//                });
//        addDisposable(disposable);
//    }

    public void getEbikeDetail(String ebikeId) {
        try {
            showDialog.setValue(true);
            JSONObject object = new JSONObject();
            object.put("ebikeId", ebikeId);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

            Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().getEbikeDetail(UserUtil.token, body),
                    new IHttpResult<EbikeDetailResponse>() {
                        @Override
                        public void success(EbikeDetailResponse myResultResponseResult) {
                            showDialog.setValue(false);
                            ebikeDetailResult.setValue(myResultResponseResult);
                        }

                        @Override
                        public void error(int i, String s) {
                            showDialog.setValue(false);
                            error.setValue(new Error(i, s));
                        }
                    });
            addDisposable(disposable);
        } catch (Exception e) {
            showDialog.setValue(false);
        }
    }

    public void getEbikeAgreement(String ebikeId) throws Exception {
        JSONObject object = new JSONObject();
        object.put("ebikeId", ebikeId);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().find5sController(UserUtil.token, body),
                new IHttpResult<Km5sSettingData>() {
                    @Override
                    public void success(Km5sSettingData myResultResponseResult) {
                        agreeMentResult.setValue(myResultResponseResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }
                });
        addDisposable(disposable);
    }

    public void btUnlockApply(String ebikeId) {
        showDialog.setValue(true);
        JSONObject object = new JSONObject();
        try {
            object.put("ebikeId", ebikeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().BtUnlockApply(UserUtil.token, body),
                new IHttpResult<EmptyBean>() {
                    @Override
                    public void success(EmptyBean myResult) {
                        showDialog.setValue(false);
                        btApplyResult.setValue(myResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }
                });
        addDisposable(disposable);
    }

    public void iot4GUnlock(String ebikeId) {
        showDialog.setValue(false);
        JSONObject object = new JSONObject();
        try {
            object.put("ebikeId", ebikeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().iot4GUnlock(UserUtil.token, body),
                new IHttpResult<EmptyBean>() {
                    @Override
                    public void success(EmptyBean myResult) {
                        showDialog.setValue(false);
                        iotUnlockResult.setValue(myResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }
                });
        addDisposable(disposable);
    }

    public void notifyUnlock(String ebikeId) {
        JSONObject object = new JSONObject();
        try {
            object.put("ebikeId", ebikeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().dealunLockSuccess(UserUtil.token, body),
                new IHttpResult<NotifyUnLockResponse>() {
                    @Override
                    public void success(NotifyUnLockResponse myResult) {
                        notifyUnlockResult.setValue(myResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }
                });
        addDisposable(disposable);
    }

    public void notifyLock(String ebikeId) {
        JSONObject object = new JSONObject();
        try {
            object.put("ebikeId", ebikeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().dealLockSuccess(UserUtil.token, body),
                new IHttpResult<CreatePayOrderBean>() {
                    @Override
                    public void success(CreatePayOrderBean myResult) {
                        notifylockResult.setValue(myResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }
                });
        addDisposable(disposable);
    }

    public void ioT4GLock(String ebikeId) {
        showDialog.setValue(false);
        JSONObject object = new JSONObject();
        try {
            object.put("ebikeId", ebikeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().iot4Glock(UserUtil.token, body),
                new IHttpResult<EmptyBean>() {
                    @Override
                    public void success(EmptyBean myResult) {
                        showDialog.setValue(false);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }
                });
        addDisposable(disposable);
    }

    public void findLastCycle(String ebikeId) {
        JSONObject object = new JSONObject();
        try {
            object.put("ebikeId", ebikeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().getLastCycle(UserUtil.token, body),
                new IHttpResult<LastCycleResponse>() {
                    @Override
                    public void success(LastCycleResponse myResult) {
                        lastCycleResult.setValue(myResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }
                });
        addDisposable(disposable);
    }

    /**
     * 查询区域围栏
     */
    public void findRegionFencePoints() {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(new JSONObject()));
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().findRegionFencePoints(UserUtil.token, body),
                new IHttpResult<RegionFenceResponse>() {
                    @Override
                    public void success(RegionFenceResponse myResult) {
                        regionFenceResult.setValue(myResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i, s));
                    }
                });
        addDisposable(disposable);
    }
}

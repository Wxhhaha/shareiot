package com.wxh.shareiot.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.wxh.basiclib.http.HttpManager;
import com.wxh.basiclib.http.IHttpResult;
import com.wxh.basiclib.lifecycle.BaseViewModel;
import com.wxh.shareiot.api.ApiHolder;
import com.wxh.shareiot.api.EbikeDetailResponse;
import com.wxh.shareiot.api.EmptyBean;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.api.Km5sSettingData;
import com.wxh.shareiot.utils.UserUtil;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LockViewModel extends BaseViewModel {
    protected MutableLiveData<EbikeDetailResponse> ebikeDetailResult = new MutableLiveData<>();
    protected MutableLiveData<EmptyBean> btApplyResult = new MutableLiveData<>();
    protected MutableLiveData<EmptyBean> iotUnlockResult = new MutableLiveData<>();
    protected MutableLiveData<Km5sSettingData> agreeMentResult = new MutableLiveData<>();

    public MutableLiveData<Km5sSettingData> getAgreeMentResult() {
        return agreeMentResult;
    }

    public MutableLiveData<EbikeDetailResponse> getEbikeDetailResult() {
        return ebikeDetailResult;
    }

    public MutableLiveData<EmptyBean> getBtApplyResult() {
        return btApplyResult;
    }

    public MutableLiveData<EmptyBean> getIotUnlockResult() {
        return iotUnlockResult;
    }

    public void getEbikeDetail(String ebikeId) throws Exception {
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
                        error.setValue(new Error(i,s));
                    }
                });
        addDisposable(disposable);
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
                        error.setValue(new Error(i,s));
                    }
                });
        addDisposable(disposable);
    }

    public void btUnlockApply(String ebikeId) {
        showDialog.setValue(false);
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
                        btApplyResult.setValue(myResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i,s));
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
                        iotUnlockResult.setValue(myResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i,s));
                    }
                });
        addDisposable(disposable);
    }
}

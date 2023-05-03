package com.wxh.shareiot.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.wxh.basiclib.http.HttpManager;
import com.wxh.basiclib.http.IHttpResult;
import com.wxh.basiclib.lifecycle.BaseViewModel;
import com.wxh.shareiot.api.ApiHolder;
import com.wxh.shareiot.api.BalancePayRequest;
import com.wxh.shareiot.api.CreateRechargeOrderRequest;
import com.wxh.shareiot.api.CreateRechargeOrderResponse;
import com.wxh.shareiot.api.EmptyBean;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.api.UserBalanceResponse;
import com.wxh.shareiot.api.WechatResponse;
import com.wxh.shareiot.api.ZfbPayRequest;
import com.wxh.shareiot.api.ZfbResponse;
import com.wxh.shareiot.utils.UserUtil;

import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PayViewModel extends BaseViewModel {
    protected MutableLiveData<ZfbResponse> zfbResult = new MutableLiveData<>();

    public MutableLiveData<ZfbResponse> getZfbResult() {
        return zfbResult;
    }

    protected MutableLiveData<WechatResponse> wechatResult = new MutableLiveData<>();

    public MutableLiveData<WechatResponse> getWechatResult() {
        return wechatResult;
    }

    protected MutableLiveData<EmptyBean> balancePayResult = new MutableLiveData<>();

    public MutableLiveData<EmptyBean> getBalancePayResult() {
        return balancePayResult;
    }

    protected MutableLiveData<CreateRechargeOrderResponse> rechargeOrderResult = new MutableLiveData<>();

    public MutableLiveData<CreateRechargeOrderResponse> getRechargeOrderResult() {
        return rechargeOrderResult;
    }

    protected MutableLiveData<UserBalanceResponse> balanceResult = new MutableLiveData<>();

    public MutableLiveData<UserBalanceResponse> getBalanceResult() {
        return balanceResult;
    }

    public void zfbPay(ZfbPayRequest request) {
        showDialog.setValue(false);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(request));
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().zfbPay(UserUtil.token, body),
                new IHttpResult<ZfbResponse>() {
                    @Override
                    public void success(ZfbResponse myResultResponseResult) {
                        showDialog.setValue(false);
                        zfbResult.setValue(myResultResponseResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i,s));
                    }
                });
        addDisposable(disposable);
    }

    public void wechatPay(ZfbPayRequest request) {
        showDialog.setValue(false);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(request));
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().wechatPay(UserUtil.token, body),
                new IHttpResult<WechatResponse>() {
                    @Override
                    public void success(WechatResponse myResultResponseResult) {
                        showDialog.setValue(false);
                        wechatResult.setValue(myResultResponseResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i,s));
                    }
                });
        addDisposable(disposable);
    }

    public void balancePay(BalancePayRequest request) {
        showDialog.setValue(false);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(request));
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().balancePay(UserUtil.token, body),
                new IHttpResult<EmptyBean>() {
                    @Override
                    public void success(EmptyBean myResultResponseResult) {
                        showDialog.setValue(false);
                        balancePayResult.setValue(myResultResponseResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i,s));
                    }
                });
        addDisposable(disposable);
    }

    public void createRechargeOrder(CreateRechargeOrderRequest request) {
        showDialog.setValue(false);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(request));
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().createRechargeOrder(UserUtil.token, body),
                new IHttpResult<CreateRechargeOrderResponse>() {
                    @Override
                    public void success(CreateRechargeOrderResponse myResultResponseResult) {
                        showDialog.setValue(false);
                        rechargeOrderResult.setValue(myResultResponseResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i,s));
                    }
                });
        addDisposable(disposable);
    }

    public void getUserBalance() {
        showDialog.setValue(false);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new JSONObject().toString());
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().getUserBalance(UserUtil.token, body),
                new IHttpResult<UserBalanceResponse>() {
                    @Override
                    public void success(UserBalanceResponse myResultResponseResult) {
                        showDialog.setValue(false);
                        balanceResult.setValue(myResultResponseResult);
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

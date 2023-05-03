package com.wxh.shareiot.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.wxh.basiclib.http.HttpManager;
import com.wxh.basiclib.http.IHttpResult;
import com.wxh.basiclib.lifecycle.BaseViewModel;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.ApiHolder;
import com.wxh.shareiot.api.EmptyBean;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.api.LoginBean;
import com.wxh.shareiot.api.SendMsgBean;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 创建时间:2022/1/24 13:44
 * 作者:wxh
 */
public class LoginViewModel extends BaseViewModel {
    protected MutableLiveData<LoginBean> loginResult = new MutableLiveData<>();
    protected MutableLiveData<EmptyBean> bindResult = new MutableLiveData<>();
    protected MutableLiveData<EmptyBean> unbindResult = new MutableLiveData<>();
    protected MutableLiveData<SendMsgBean> sendResult = new MutableLiveData<>();
    protected MutableLiveData<EmptyBean> registerResult = new MutableLiveData<>();

    public MutableLiveData<EmptyBean> getRegisterResult() {
        return registerResult;
    }

    public MutableLiveData<LoginBean> getLoginData() {
        return loginResult;
    }

    public MutableLiveData<EmptyBean> getBindResult() {
        return bindResult;
    }

    public MutableLiveData<EmptyBean> getUnbindResult() {
        return unbindResult;
    }

    public MutableLiveData<SendMsgBean> getSendResult() {
        return sendResult;
    }

    /**
     * 验证码登录
     * @param body
     * @param showLoading
     */
    public void codeLogin(RequestBody body, boolean showLoading) {
        showDialog.setValue(showLoading, "Loading...");

        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().login(body),
                new IHttpResult<LoginBean>() {
            @Override
            public void success(LoginBean myResultResponseResult) {
                showDialog.setValue(false);
                loginResult.setValue(myResultResponseResult);
            }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i,s));
                    }
        });
        addDisposable(disposable);
    }

    /**
     * 手机号密码登录
     * @param body
     * @param showLoading
     */
    public void accountLogin(RequestBody body, boolean showLoading) {
        showDialog.setValue(showLoading, "Loading...");

        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().login(body),
                new IHttpResult<LoginBean>() {
                    @Override
                    public void success(LoginBean myResultResponseResult) {
                        showDialog.setValue(false);
                        loginResult.setValue(myResultResponseResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i,s));
                    }
                });
        addDisposable(disposable);
    }

    /**
     * 获取短信验证码
     * @param phoneNumber
     */
    public void getSmsVerifyCode(String phoneNumber) {
        JSONObject object = new JSONObject();
        try {
            object.put("phoneNo",phoneNumber);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        showDialog.setValue(false);
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().getSmsCode(body),
                new IHttpResult<EmptyBean>() {
                    @Override
                    public void success(EmptyBean myResultResponseResult) {
                        error.setValue(R.string.code_sended);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i,s));
                    }
                });
        addDisposable(disposable);
    }

    public void registerPhoneUser(RequestBody body) {

        showDialog.setValue(false);
        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().registerPhoneUser(body),
                new IHttpResult<EmptyBean>() {
                    @Override
                    public void success(EmptyBean myResultResponseResult) {
                        registerResult.setValue(myResultResponseResult);
                    }

                    @Override
                    public void error(int i, String s) {
                        showDialog.setValue(false);
                        error.setValue(new Error(i,s));
                    }
                });
        addDisposable(disposable);
    }

//    public void sendMsg(RequestBody body) {
//        showDialog.setValue(true, "Loading...");
//
//        Disposable disposable = HttpManager.toSubscribe(ApiHolder.getApiInstance().sendMsg(UserUtil.token,body),
//                new IHttpResult<SendMsgBean>() {
//                    @Override
//                    public void success(SendMsgBean> myResultResponseResult) {
//                        showDialog.setValue(false);
//                        sendResult.postValue(myResultResponseResult.getResult());
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
}

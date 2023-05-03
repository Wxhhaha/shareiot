package com.wxh.shareiot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.animation.AnimationUtils;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.wxh.basiclib.base.BaseActivity;
import com.wxh.basiclib.utils.LogUtil;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.AccountLoginRequest;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.api.LoginBean;
import com.wxh.shareiot.databinding.ActivityLoginBinding;
import com.wxh.shareiot.socket.JWebSocketClientService;
import com.wxh.shareiot.utils.CountDownTimerUtils;
import com.wxh.shareiot.utils.TextUtils;
import com.wxh.shareiot.utils.UserUtil;
import com.wxh.shareiot.viewmodel.LoginViewModel;

import okhttp3.MediaType;
import okhttp3.RequestBody;


public class LoginActivity extends BaseActivity<LoginViewModel, ActivityLoginBinding> {

    private boolean isChecked = false;
    private boolean isShowPwd = false;

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView(Bundle bundle) {

        dataBinding.tvLogin.setOnClickListener(view -> {
            String phoneNo = dataBinding.etUser.getText().toString();
            String smsCode = dataBinding.etPwd.getText().toString();
            if (!RegexUtils.isMobileSimple(phoneNo)) {
                ToastUtils.showShort(R.string.please_input_phone_num);
                return;
            }
            if (StringUtils.isEmpty(smsCode)) {
                ToastUtils.showShort(R.string.please_input_code);
                return;
            }
            if (!isChecked) {
                dataBinding.tvAgreement.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_checkbox_shake));
                return;
            }
//            SmsLoginRequest request = new SmsLoginRequest();
//            request.setPhoneNo(phoneNo);
//            request.setSmsCode(smsCode);
//            RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(request));
//            viewModel.login(body, true);

            AccountLoginRequest request1 = new AccountLoginRequest();
            request1.setLoginName(dataBinding.etUser.getText().toString());
            request1.setLoginPwd(dataBinding.etPwd.getText().toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(request1));
            viewModel.accountLogin(body, true);
        });

        dataBinding.btGetCode.setOnClickListener(view -> {
            try {
                String phoneNumber = dataBinding.etUser.getText().toString();
                if (!RegexUtils.isMobileSimple(phoneNumber)) {
                    ToastUtils.showShort(R.string.please_input_phone_num);
                    return;
                }
                viewModel.getSmsVerifyCode(phoneNumber);
                CountDownTimerUtils countDownTimerUtils = new CountDownTimerUtils(dataBinding.btGetCode, 60000, 1000);
                countDownTimerUtils.start();
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
        });

        dataBinding.tvRegister.setOnClickListener(view -> {
            ActivityUtils.startActivityForResult(this,RegisterActivity.class,0);
        });

        dataBinding.ivHide.setOnClickListener(view -> {
            if (isShowPwd) {
                dataBinding.ivHide.setImageResource(R.mipmap.ic_pwd_hide);
                dataBinding.etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                dataBinding.etPwd.setSelection(dataBinding.etPwd.length());
                isShowPwd = false;
            } else {
                dataBinding.ivHide.setImageResource(R.mipmap.ic_pwd_show);
                dataBinding.etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                dataBinding.etPwd.setSelection(dataBinding.etPwd.length());
                isShowPwd = true;
            }
        });

        TextUtils.getBuilder().click(getString(R.string.login_protocol), getResources().getColor(R.color.black),
                new TextUtils.OnClickListener() {
                    @Override
                    public void onClick(int position) {
                        switch (position) {
                            case 0://用户协议


                                break;
                            case 1://隐私权限

                                break;
                        }

                    }
                }, getString(R.string.user_agreement2), getString(R.string.privacy_policy2)).checkBox(this, dataBinding.tvAgreement, new TextUtils.OnImageClickListener() {
            @Override
            public void onChecked() {
                isChecked = true;
            }

            @Override
            public void onUnChecked() {
                isChecked = false;
            }
        }).clickInto(dataBinding.tvAgreement);


    }

    @Override
    protected void initData() {
        viewModel.getLoginData().observe(this, new Observer<LoginBean>() {
            @Override
            public void onChanged(LoginBean loginBeanMyResult) {
                    UserUtil.token = loginBeanMyResult.getToken();
                    UserUtil.isLogin = true;
                    SPUtils.getInstance().put("token", UserUtil.token);
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                    finish();
            }
        });
    }

    @Override
    protected LoginViewModel initViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Override
    protected void showError(Object o) {
        if(o instanceof Error){
            Error error = (Error)o;
            switch (error.getCode()) {
                case 401:
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

    @Override
    public void onBackPressed() {
        AppUtils.exitApp();
        super.onBackPressed();
    }
}
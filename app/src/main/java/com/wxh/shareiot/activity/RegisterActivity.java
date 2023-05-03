package com.wxh.shareiot.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wxh.basiclib.base.BaseActivity;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.databinding.ActivityRegisterBinding;
import com.wxh.shareiot.socket.JWebSocketClientService;
import com.wxh.shareiot.utils.CountDownTimerUtils;
import com.wxh.shareiot.viewmodel.LoginViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RegisterActivity extends BaseActivity<LoginViewModel, ActivityRegisterBinding> {

    @Override
    protected int getLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView(Bundle bundle) {
        dataBinding.btGetCode.setOnClickListener(view -> {
            String phone = dataBinding.etUser.getText().toString();
            if (!phone.isEmpty()) {
                viewModel.getSmsVerifyCode(phone);
                new CountDownTimerUtils(dataBinding.btGetCode, 60 * 1000, 1000).start();
            }
        });

        dataBinding.tvRegister.setOnClickListener(view -> {
            if (dataBinding.etUser.getText().toString().isEmpty()) {
                return;
            }
            if (dataBinding.etCode.getText().toString().isEmpty()) {
                return;
            }
            if (dataBinding.etPwd.getText().toString().isEmpty()) {
                return;
            }
            JSONObject object = new JSONObject();
            try {
                object.put("phoneNo", dataBinding.etUser.getText().toString());
                object.put("verCode", dataBinding.etCode.getText().toString());
                object.put("loginPwd", dataBinding.etPwd.getText().toString());
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
            viewModel.registerPhoneUser(body);
        });
    }

    @Override
    protected void initData() {
        viewModel.getRegisterResult().observe(this, emptyBeanMyResult -> {
            setResult(2);
            finish();
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
}
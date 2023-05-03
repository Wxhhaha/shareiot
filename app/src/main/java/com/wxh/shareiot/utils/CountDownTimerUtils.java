package com.wxh.shareiot.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.wxh.shareiot.R;


public class CountDownTimerUtils extends CountDownTimer {
    private TextView mTextView;

    public CountDownTimerUtils(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setEnabled(false); //设置不可点击
        mTextView.setText(millisUntilFinished / 1000 + "s");  //设置倒计时时间
    }

    @Override
    public void onFinish() {
        mTextView.setText(R.string.get_verify_code);
        mTextView.setEnabled(true);//重新获得点击
    }
}

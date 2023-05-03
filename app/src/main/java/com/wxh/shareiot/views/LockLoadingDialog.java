package com.wxh.shareiot.views;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.wxh.basiclib.utils.Dp2pxUtils;
import com.wxh.shareiot.R;
import com.wxh.shareiot.databinding.LockDialogLoadingBinding;


public class LockLoadingDialog extends Dialog {

    private LockDialogLoadingBinding binding;
    private Context context;

    public LockLoadingDialog(@NonNull Context context) {
        super(context, R.style.BaseDialog);
        this.context = context;
        setCanceledOnTouchOutside(false);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.lock_dialog_loading, null, false);
        setContentView(binding.getRoot());
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = Dp2pxUtils.dip2px(context, 300);
        lp.height = Dp2pxUtils.dip2px(context, 150);
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
    }

    /**
     * 设置等待提示信息
     */
    public void setLoadingMsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        binding.tvMsg.setText(msg);
    }

    public void setLoadingMsg(String msg,int resId) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        binding.tvMsg.setCompoundDrawables(context.getDrawable(R.mipmap.ic_unlocking),null,null,null);
        binding.tvMsg.setText(msg);
    }
}

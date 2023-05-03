package com.wxh.shareiot.views;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ScreenUtils;
import com.wxh.basiclib.dialog.BaseDialog;
import com.wxh.shareiot.R;

public class UnfinishedDialog extends BaseDialog {
    private DialogCallback dialogCallback;
    private int type;

    public UnfinishedDialog(@NonNull Context context, int type) {
        super(context, R.style.LoadDialog);
        this.type = type;
    }

    @Override
    public int bindLayout() {
        return R.layout.unfinish_dialog_layout;
    }

    @Override
    public void initView(BaseDialog baseDialog, View view) {
        TextView contentTextView = view.findViewById(R.id.tv);
        TextView okTextView = view.findViewById(R.id.tv_ok);
        TextView endTextView = view.findViewById(R.id.tv_end);
        if(type==0){
            contentTextView.setText(R.string.not_pay_order);
            endTextView.setVisibility(View.GONE);
        }else{
            contentTextView.setText(R.string.has_order);
            okTextView.setText(R.string.continue_ride);
            endTextView.setVisibility(View.VISIBLE);
        }

        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                dialogCallback.ok();
            }
        });
        endTextView.setOnClickListener(view1 -> {
            dismiss();
            dialogCallback.end();
        });
    }

    @Override
    public void setWindowStyle(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (ScreenUtils.getScreenWidth() * 0.7);
        window.setAttributes(params);
    }

    public interface DialogCallback {
        void ok();
        void end();
    }

    public DialogCallback getDialogCallback() {
        return dialogCallback;
    }

    public void setDialogCallback(DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }
}

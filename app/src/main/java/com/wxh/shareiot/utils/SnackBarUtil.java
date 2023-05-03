package com.wxh.shareiot.utils;

import android.content.Context;
import android.view.View;

import com.blankj.utilcode.util.SnackbarUtils;
import com.wxh.shareiot.R;
import com.wxh.shareiot.interfaces.SnackbarInterface;

/**
 * 创建时间:2021/12/14 11:22
 * 作者:wxh
 */
public class SnackBarUtil {

    public static void showSnackbar(View view, boolean isSuccess, String msg) {
        if (isSuccess) {
            SnackbarUtils.with(view).setDuration(SnackbarUtils.LENGTH_LONG).setMessage(msg).showSuccess();
        } else {
            SnackbarUtils.with(view).setDuration(SnackbarUtils.LENGTH_LONG).setMessage(msg).showError();
        }
    }

    public static void showSnackbarConfirm(Context context, View view, String msg, SnackbarInterface snackbarInterface) {
        SnackbarUtils.with(view).setDuration(SnackbarUtils.LENGTH_INDEFINITE).setMessage(msg).setAction(context.getString(R.string.ok), v -> {
            snackbarInterface.ok();
        }).show();
    }

    public static void showSnackbarAction(Context context, View view, String msg, String actionMsg, SnackbarInterface snackbarInterface) {
        SnackbarUtils.with(view).setDuration(SnackbarUtils.LENGTH_INDEFINITE).setMessage(msg).setAction(actionMsg, v -> {
            snackbarInterface.ok();
        }).show();
    }
}

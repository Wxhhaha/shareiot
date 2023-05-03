package com.wxh.shareiot.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.wxh.basiclib.ble.UartSettingData;
import com.wxh.shareiot.R;

public class RideDialog extends AppCompatDialog {
    private OnClickInterface onClickInterface;
    private Window mWindow;
    private TextView speedTextView;
    private TextView odoTextView;
    private TextView socTextView;
    private TextView tpTextView;
    private TextView powerTextView;
    private TextView pasTextView;
    private ImageView walk;
    private ImageView light;
    private ImageView closeImageView;
    private long clickTime;

    public OnClickInterface getOnClickInterface() {
        return onClickInterface;
    }

    public void setOnClickInterface(OnClickInterface onClickInterface) {
        this.onClickInterface = onClickInterface;
    }

    public RideDialog(Context context) {
        super(context, R.style.BaseDialog);
    }

    public RideDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_controller_layout);
        initView();
    }

    private void initView() {
        speedTextView = findViewById(R.id.tv_speed);
        odoTextView = findViewById(R.id.tv_odo);
        socTextView = findViewById(R.id.tv_soc);
        tpTextView = findViewById(R.id.tv_tapin);
        powerTextView = findViewById(R.id.tv_power);
        walk = findViewById(R.id.iv_walk);
        light = findViewById(R.id.iv_light);
        pasTextView = findViewById(R.id.tv_pas);
        closeImageView = findViewById(R.id.iv_close);
        closeImageView.setOnClickListener(view -> {
            dismiss();
        });
        if (walk != null) {
            walk.setOnLongClickListener(v -> {
                clickTime = System.currentTimeMillis();
                walk.setImageResource(R.mipmap.ic_walk_on);
                onClickInterface.onWalkStart();
                return true;
            });

            walk.setOnClickListener(v -> {
                clickTime = System.currentTimeMillis();
                walk.setImageResource(R.mipmap.ic_walk_off);
                onClickInterface.onWalkStop();
            });
        }

        if (light != null) {
            light.setOnClickListener(v -> {
                clickTime = System.currentTimeMillis();
                if (light.getTag().equals("0")) {
                    //关灯状态点击开灯
                    light.setImageResource(R.mipmap.ic_bt_light_on);
                    light.setTag("1");
                    onClickInterface.onLightOn();
                } else {
                    //开灯状态点击关灯
                    light.setImageResource(R.mipmap.ic_bt_light_off);
                    light.setTag("0");
                    onClickInterface.onLightOff();
                }
            });
        }
    }

    public void resfreshView(String speed, int pas, int lamp, String odo, String soc, String error,String cadence,String power) {
        speedTextView.setText(speed);
        pasTextView.setText(pas + "");
        if (pas == 10) {
            pasTextView.setText("-");
        }
        if (checkTimeLong()) {
            if (lamp == 0) {
                light.setImageResource(R.mipmap.ic_bt_light_off);
                light.setTag("0");
                UartSettingData.getInstance().setLightStatus(0);
            } else {
                light.setImageResource(R.mipmap.ic_bt_light_on);
                light.setTag("1");
                UartSettingData.getInstance().setLightStatus(1);
            }

            if (pas != 10) {
                UartSettingData.getInstance().setWalkMode(0);
                walk.setImageResource(R.mipmap.ic_walk_off);
            } else {
                UartSettingData.getInstance().setWalkMode(1);
                walk.setImageResource(R.mipmap.ic_walk_on);
            }
        }

        odoTextView.setText(odo);
        socTextView.setText(soc);
        tpTextView.setText(cadence);
        powerTextView.setText(power);
    }

    private boolean checkTimeLong() {
        if (System.currentTimeMillis() - clickTime > 1000) {
            return true;
        }
        return false;
    }
//
//    public void refreshViewB(String odo, String soc, String voltage, String current) {
//        odoTextView.setText(odo);
//        socTextView.setText(soc);
//        volTextView.setText(voltage);
//        currentTextView.setText(current);
//    }

    @Override
    public void show() {
        super.show();
        mWindow = getWindow();
        WindowManager.LayoutParams params = mWindow.getAttributes();
        params.x = 0;
        params.y = 0;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.alpha = 1.0f;
        params.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(params);
    }

    public interface OnClickInterface {
        void onWalkStart();

        void onWalkStop();

        void onLightOff();

        void onLightOn();

        void onPas(int pas);
    }
}

package com.wxh.shareiot.activity;

import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.KeyboardUtils;
import com.simple.vclayout.VerCodeLayout;
import com.wxh.basiclib.base.BaseNoModelActivity;
import com.wxh.basiclib.utils.LogUtil;
import com.wxh.shareiot.R;
import com.wxh.shareiot.databinding.ActivityEnterCodeBinding;

public class EnterCodeActivity extends BaseNoModelActivity<ActivityEnterCodeBinding> {
    private boolean isFlashOn;
    private CameraManager mCameraManager;
    private boolean mTorchAvailable;
    private String mCameraId;
    private boolean mFlashlightEnable;

    @Override
    protected int getLayout() {
        return R.layout.activity_enter_code;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        dataBinding.etCode.setOnCompleteListener(new VerCodeLayout.OnCompleteListener() {
            @Override
            public void onComplete(Editable editable, String code) {
                dataBinding.etCode.clearFocus();
                KeyboardUtils.hideSoftInput(EnterCodeActivity.this);

                //远程开锁，同扫描完相同
                Intent intent = new Intent();
                intent.putExtra("code", "N" + code);
                setResult(1, intent);
                finish();
            }
        });

        dataBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dataBinding.ivFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    setFlashLight(false);
                    dataBinding.ivFlash.setImageResource(R.mipmap.ic_flash_off);
                    isFlashOn = false;
                } else {
                    setFlashLight(true);
                    dataBinding.ivFlash.setImageResource(R.mipmap.ic_flash_on);
                    isFlashOn = true;
                }
            }
        });
    }

    @Override
    protected void initData() {
        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            mCameraId = getCameraId();
        } catch (Exception e) {
            LogUtil.e("Could't initialize.");
        }
        if (mCameraId != null) {
            mCameraManager.registerTorchCallback(mTorchCallback, null);
        }
    }

    private String getCameraId() throws CameraAccessException {
        String[] ids = mCameraManager.getCameraIdList();
        for (String id : ids) {
            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
            Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
            if (flashAvailable != null && flashAvailable && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                return id;
            }
        }
        return null;
    }

    private CameraManager.TorchCallback mTorchCallback = new CameraManager.TorchCallback() {
        @Override
        public void onTorchModeUnavailable(@NonNull String cameraId) {
            if (cameraId.equals(mCameraId)) {
                setCameraAvailable(false);
            }
            ;
        }

        @Override
        public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
            if (cameraId.equals(mCameraId)) {
                setCameraAvailable(true);
                mFlashlightEnable = enabled;
            }
            ;
        }

        private void setCameraAvailable(boolean available) {
            mTorchAvailable = available;
        }
    };

    private void setFlashLight(boolean enabled) {
        synchronized (this) {
            if (mCameraId == null || !mTorchAvailable) {
                return;
            }
            if (mFlashlightEnable != enabled) {
                mFlashlightEnable = enabled;
            }
            try {
                mCameraManager.setTorchMode(mCameraId, enabled);
            } catch (CameraAccessException e) {
                LogUtil.e("Couldn't set torch mode");
            }
        }
    }
}
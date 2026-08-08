/*
 * CaptureActivity.java
 * 二维码扫描活动 - 匹配原版 com.clement.livetools.qrcode.ui.CaptureActivity
 * SOLID: Single Responsibility
 * KISS: 保持简单
 */

package com.clement.livetools.qrcode.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import androidx.annotation.Nullable;

public class CaptureActivity extends Activity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(getResId("activity_capture"));

        surfaceView = findViewById(getResId("surface_view"));
        if (surfaceView != null) {
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
        }
    }

    private int getResId(String name) {
        return getResources().getIdentifier(name, "id", getPackageName());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

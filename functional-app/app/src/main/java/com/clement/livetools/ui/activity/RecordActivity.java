package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.clement.livetools.R;
import com.clement.livetools.service.ScreenRecordService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordActivity extends Activity {

    private static final int REQUEST_SCREEN_CAPTURE = 1001;
    private Button btnRecord;
    private TextView tvStatus, tvTimer;
    private boolean isRecording = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private long startTime;
    private ScreenRecordService recordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        btnRecord = findViewById(R.id.btn_record);
        tvStatus = findViewById(R.id.tv_status);
        tvTimer = findViewById(R.id.tv_timer);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_settings).setOnClickListener(v ->
                startActivity(new Intent(this, RecordSetVideoActivity.class)));

        btnRecord.setOnClickListener(v -> {
            if (!isRecording) {
                requestScreenCapture();
            } else {
                stopRecording();
            }
        });
    }

    private void requestScreenCapture() {
        MediaProjectionManager mpm = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if (mpm != null) {
            startActivityForResult(mpm.createScreenCaptureIntent(), REQUEST_SCREEN_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCREEN_CAPTURE && resultCode == RESULT_OK && data != null) {
            startRecording(resultCode, data);
        }
    }

    private void startRecording(int resultCode, Intent data) {
        Intent serviceIntent = new Intent(this, ScreenRecordService.class);
        serviceIntent.putExtra("result_code", resultCode);
        serviceIntent.putExtra("data", data);
        startForegroundService(serviceIntent);

        isRecording = true;
        startTime = System.currentTimeMillis();
        btnRecord.setText("停止录制");
        tvStatus.setText("录制中...");
        tvStatus.setTextColor(0xFFFF0000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    int secs = (int) (elapsed / 1000) % 60;
                    int mins = (int) (elapsed / 60000);
                    tvTimer.setText(String.format("%02d:%02d", mins, secs));
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    private void stopRecording() {
        stopService(new Intent(this, ScreenRecordService.class));
        isRecording = false;
        btnRecord.setText("开始录制");
        tvStatus.setText("录制已保存");
        tvStatus.setTextColor(0xFF666666);
        tvTimer.setText("00:00");
    }
}
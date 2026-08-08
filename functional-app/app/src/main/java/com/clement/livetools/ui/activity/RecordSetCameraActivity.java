package com.clement.livetools.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clement.livetools.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordSetCameraActivity extends Activity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button btnRecord;
    private TextView tvStatus, tvTimer;
    private Spinner spResolution, spCamera;
    private MediaRecorder mediaRecorder;
    private android.hardware.camera2.CameraCaptureSession captureSession;
    private android.hardware.camera2.CameraDevice cameraDevice;
    private CameraManager cameraManager;
    private boolean isRecording = false;
    private long startTime;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String currentCameraId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_set_camera);

        surfaceView = findViewById(R.id.surface_view);
        btnRecord = findViewById(R.id.btn_record);
        tvStatus = findViewById(R.id.tv_status);
        tvTimer = findViewById(R.id.tv_timer);
        spResolution = findViewById(R.id.sp_resolution);
        spCamera = findViewById(R.id.sp_camera);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        // 分辨率选项
        String[] resolutions = {"720p (1280x720)", "1080p (1920x1080)", "480p (854x480)"};
        spResolution.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, resolutions));

        // 摄像头选项
        List<String> cameras = new ArrayList<>();
        try {
            for (String id : cameraManager.getCameraIdList()) {
                CameraCharacteristics cc = cameraManager.getCameraCharacteristics(id);
                Integer facing = cc.get(CameraCharacteristics.LENS_FACING);
                cameras.add(facing != null && facing == CameraMetadata.LENS_FACING_FRONT ? "前置" : "后置");
            }
        } catch (CameraAccessException e) {
            cameras.add("后置");
        }
        spCamera.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cameras));
        spCamera.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                try {
                    currentCameraId = cameraManager.getCameraIdList()[position];
                } catch (Exception e) {}
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnRecord.setOnClickListener(v -> {
            if (!isRecording) startCameraRecording();
            else stopCameraRecording();
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void startCameraRecording() {
        try {
            cameraManager.openCamera(currentCameraId, new android.hardware.camera2.CameraDevice.StateCallback() {
                @Override
                public void onOpened(android.hardware.camera2.CameraDevice camera) {
                    cameraDevice = camera;
                    startRecord();
                }
                @Override
                public void onDisconnected(android.hardware.camera2.CameraDevice camera) {}
                @Override
                public void onError(android.hardware.camera2.CameraDevice camera, int error) {}
            }, handler);
        } catch (Exception e) {
            Toast.makeText(this, "无法打开摄像头: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecord() {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String path = getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/camera_" + timestamp + ".mp4";

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setOutputFile(path);
            mediaRecorder.setVideoEncodingBitRate(5000000);
            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setVideoSize(1280, 720);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.prepare();

            Surface surface = mediaRecorder.getSurface();
            cameraDevice.createCaptureSession(
                    java.util.Collections.singletonList(surface),
                    new android.hardware.camera2.CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(android.hardware.camera2.CameraCaptureSession session) {
                            captureSession = session;
                            try {
                                android.hardware.camera2.CaptureRequest.Builder builder =
                                        cameraDevice.createCaptureRequest(android.hardware.camera2.CameraDevice.TEMPLATE_RECORD);
                                builder.addTarget(surface);
                                builder.set(android.hardware.camera2.CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                                session.setRepeatingRequest(builder.build(), null, handler);
                                mediaRecorder.start();

                                isRecording = true;
                                startTime = System.currentTimeMillis();
                                btnRecord.setText("停止");
                                tvStatus.setText("录制中...");

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isRecording) {
                                            long e = System.currentTimeMillis() - startTime;
                                            tvTimer.setText(String.format("%02d:%02d", e / 60000, (e / 1000) % 60));
                                            handler.postDelayed(this, 1000);
                                        }
                                    }
                                }, 1000);
                            } catch (Exception e) {
                                Toast.makeText(RecordSetCameraActivity.this, "录制失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onConfigureFailed(android.hardware.camera2.CameraCaptureSession session) {}
                    }, handler);
        } catch (Exception e) {
            Toast.makeText(this, "初始化失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopCameraRecording() {
        if (mediaRecorder != null) {
            try { mediaRecorder.stop(); } catch (Exception e) {}
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (captureSession != null) {
            try { captureSession.close(); } catch (Exception e) {}
            captureSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        isRecording = false;
        btnRecord.setText("开始录制");
        tvStatus.setText("录制已保存");
        tvTimer.setText("00:00");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {}
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) stopCameraRecording();
    }
}
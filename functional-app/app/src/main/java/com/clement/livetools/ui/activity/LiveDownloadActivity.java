package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.clement.livetools.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LiveDownloadActivity extends Activity {

    private EditText etUrl;
    private Button btnParse, btnRecord, btnDownload;
    private TextView tvStatus;
    private VideoView videoView;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private String currentUrl;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_download);

        etUrl = findViewById(R.id.et_url);
        btnParse = findViewById(R.id.btn_parse);
        btnRecord = findViewById(R.id.btn_record);
        btnDownload = findViewById(R.id.btn_download);
        tvStatus = findViewById(R.id.tv_status);
        videoView = findViewById(R.id.video_view);

        // 粘贴剪贴板
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData clip = clipboard.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                etUrl.setText(clip.getItemAt(0).getText());
            }
        }

        btnParse.setOnClickListener(v -> parseUrl());
        btnRecord.setOnClickListener(v -> toggleRecord());
        btnDownload.setOnClickListener(v -> downloadStream());
    }

    private void parseUrl() {
        currentUrl = etUrl.getText().toString().trim();
        if (currentUrl.isEmpty()) {
            Toast.makeText(this, "请输入直播地址", Toast.LENGTH_SHORT).show();
            return;
        }

        tvStatus.setText("正在解析...");
        videoView.setVideoURI(Uri.parse(currentUrl));
        videoView.setOnPreparedListener(mp -> {
            tvStatus.setText("解析成功 - " + mp.getVideoWidth() + "x" + mp.getVideoHeight());
            videoView.start();
            btnRecord.setEnabled(true);
            btnDownload.setEnabled(true);
        });
        videoView.setOnErrorListener((mp, what, extra) -> {
            tvStatus.setText("解析失败: " + what);
            return true;
        });
        videoView.start();
    }

    private void toggleRecord() {
        if (!isRecording) {
            startRecord();
        } else {
            stopRecord();
        }
    }

    private void startRecord() {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String filePath = getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/rec_" + timestamp + ".mp4";

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setOutputFile(filePath);
            mediaRecorder.setVideoEncodingBitRate(5000000);
            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setVideoSize(1280, 720);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording = true;
            btnRecord.setText("停止录制");
            tvStatus.setText("录制中... " + filePath);
        } catch (Exception e) {
            tvStatus.setText("录制失败: " + e.getMessage());
        }
    }

    private void stopRecord() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (Exception e) {
                tvStatus.setText("停止录制失败: " + e.getMessage());
            }
            mediaRecorder = null;
            isRecording = false;
            btnRecord.setText("开始录制");
            tvStatus.setText("录制已保存");
        }
    }

    private void downloadStream() {
        if (currentUrl == null || currentUrl.isEmpty()) return;

        tvStatus.setText("正在下载...");
        new Thread(() -> {
            try {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                File outputFile = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "live_" + timestamp + ".mp4");

                URL url = new URL(currentUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                InputStream in = conn.getInputStream();
                FileOutputStream out = new FileOutputStream(outputFile);
                byte[] buffer = new byte[8192];
                int len;
                long total = 0;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    total += len;
                }
                out.close();
                in.close();
                conn.disconnect();

                handler.post(() -> tvStatus.setText("下载完成: " + outputFile.getAbsolutePath() + " (" + total / 1024 + "KB)"));
            } catch (Exception e) {
                handler.post(() -> tvStatus.setText("下载失败: " + e.getMessage()));
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) stopRecord();
        if (videoView != null) videoView.stopPlayback();
    }
}
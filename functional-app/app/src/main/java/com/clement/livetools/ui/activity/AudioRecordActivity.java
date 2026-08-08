package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.clement.livetools.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AudioRecordActivity extends Activity {

    private Button btnRecord;
    private TextView tvStatus, tvTimer;
    private MediaRecorder recorder;
    private boolean isRecording = false;
    private String outputPath;
    private long startTime;
    private Timer timer;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);

        btnRecord = findViewById(R.id.btn_record);
        tvStatus = findViewById(R.id.tv_status);
        tvTimer = findViewById(R.id.tv_timer);

        btnRecord.setOnClickListener(v -> toggleRecord());

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void toggleRecord() {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            outputPath = new File(dir, "rec_" + timestamp + ".mp3").getAbsolutePath();

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(128000);
            recorder.setAudioSamplingRate(44100);
            recorder.setOutputFile(outputPath);
            recorder.prepare();
            recorder.start();

            isRecording = true;
            startTime = System.currentTimeMillis();
            btnRecord.setText("停止");
            tvStatus.setText("录音中...");
            tvStatus.setTextColor(0xFFFF0000);

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    long elapsed = System.currentTimeMillis() - startTime;
                    int secs = (int) (elapsed / 1000) % 60;
                    int mins = (int) (elapsed / 60000);
                    handler.post(() -> tvTimer.setText(String.format("%02d:%02d", mins, secs)));
                }
            }, 0, 1000);
        } catch (Exception e) {
            Toast.makeText(this, "录音失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
            } catch (Exception e) {
                // ignore
            }
            recorder = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        isRecording = false;
        btnRecord.setText("开始录音");
        tvStatus.setText("已保存: " + outputPath);
        tvStatus.setTextColor(0xFF666666);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) stopRecording();
    }
}
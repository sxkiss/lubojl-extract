package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.clement.livetools.R;
import com.clement.livetools.qrcode.ui.CaptureActivity;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 直播录制
        findViewById(R.id.item_live_download).setOnClickListener(v ->
                startActivity(new Intent(this, LiveDownloadActivity.class)));

        // 屏幕录制
        findViewById(R.id.item_screen_record).setOnClickListener(v ->
                startActivity(new Intent(this, RecordActivity.class)));

        // AI虚拟主播
        findViewById(R.id.item_ai_anchor).setOnClickListener(v ->
                startActivity(new Intent(this, RecordSetAiManActivity.class)));

        // 音频录制
        findViewById(R.id.item_audio_record).setOnClickListener(v ->
                startActivity(new Intent(this, AudioRecordActivity.class)));

        // 语音合成 (TTS)
        findViewById(R.id.item_tts).setOnClickListener(v ->
                startActivity(new Intent(this, AudioSynthesisActivity.class)));

        // 音乐播放
        findViewById(R.id.item_music).setOnClickListener(v ->
                startActivity(new Intent(this, AudioLibraryActivity.class)));

        // 二维码扫描
        findViewById(R.id.item_qrcode).setOnClickListener(v ->
                startActivity(new Intent(this, CaptureActivity.class)));

        // Socks5代理
        findViewById(R.id.item_proxy).setOnClickListener(v ->
                startActivity(new Intent(this, PushInfoSetActivity.class)));

        // 摄像头录制
        findViewById(R.id.item_camera).setOnClickListener(v ->
                startActivity(new Intent(this, RecordSetCameraActivity.class)));

        // 设置按钮
        findViewById(R.id.btn_settings).setOnClickListener(v ->
                startActivity(new Intent(this, PushInfoSetActivity.class)));
    }
}
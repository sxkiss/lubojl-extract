package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clement.livetools.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AudioSynthesisActivity extends Activity {

    private EditText etText;
    private Spinner spVoice;
    private Button btnSpeak, btnStop;
    private TextView tvStatus;
    private TextToSpeech tts;
    private boolean ttsReady = false;

    // 原版支持的22种声音（使用系统TTS替代）
    private String[] voices = {
            "默认女声", "默认男声", "中文女声", "中文男声",
            "英语女声", "英语男声", "日语女声", "日语男声"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_synthesis);

        etText = findViewById(R.id.et_text);
        spVoice = findViewById(R.id.sp_voice);
        btnSpeak = findViewById(R.id.btn_speak);
        btnStop = findViewById(R.id.btn_stop);
        tvStatus = findViewById(R.id.tv_status);

        spVoice.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, voices));

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true;
                tts.setLanguage(Locale.CHINESE);
                tvStatus.setText("TTS 引擎就绪");
            } else {
                tvStatus.setText("TTS 初始化失败");
            }
        });

        btnSpeak.setOnClickListener(v -> {
            if (!ttsReady) {
                Toast.makeText(this, "TTS 引擎未就绪", Toast.LENGTH_SHORT).show();
                return;
            }
            String text = etText.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "请输入文本", Toast.LENGTH_SHORT).show();
                return;
            }
            // 根据选择的声音切换语言
            int voiceIndex = spVoice.getSelectedItemPosition();
            switch (voiceIndex) {
                case 0: case 2: tts.setLanguage(Locale.CHINESE); break;
                case 1: case 3: tts.setLanguage(Locale.CHINESE); break;
                case 4: case 5: tts.setLanguage(Locale.ENGLISH); break;
                case 6: case 7: tts.setLanguage(Locale.JAPANESE); break;
            }
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ttsutterance");
            tvStatus.setText("正在合成...");
        });

        btnStop.setOnClickListener(v -> {
            if (tts != null && tts.isSpeaking()) {
                tts.stop();
                tvStatus.setText("已停止");
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
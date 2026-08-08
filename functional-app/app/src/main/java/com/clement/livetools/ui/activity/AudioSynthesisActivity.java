package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clement.livetools.R;

import java.util.Locale;

/**
 * 语音合成 TTS - 对标原版 AudioSynthesisActivity
 * 支持：声音选择、语速调节、音调调节、音量调节
 */
public class AudioSynthesisActivity extends Activity {

    private EditText etText;
    private Spinner spVoice;
    private Button btnSpeak, btnStop, btnSave;
    private TextView tvStatus, tvSpeedValue, tvToneValue, tvVolumeValue;
    private TextToSpeech tts;
    private boolean ttsReady = false;

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
        btnSave = findViewById(R.id.btn_save_synth);
        tvStatus = findViewById(R.id.tv_status);
        tvSpeedValue = findViewById(R.id.tv_speed_value);
        tvToneValue = findViewById(R.id.tv_tone_value);
        tvVolumeValue = findViewById(R.id.tv_volume_value);

        SeekBar sbSpeed = findViewById(R.id.sb_speed);
        SeekBar sbTone = findViewById(R.id.sb_tone);
        SeekBar sbVolume = findViewById(R.id.sb_volume);

        spVoice.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, voices));

        // 加载保存的设置
        SharedPreferences sp = getSharedPreferences("tts_settings", MODE_PRIVATE);
        int savedSpeed = sp.getInt("speed", 50);
        int savedTone = sp.getInt("tone", 50);
        int savedVolume = sp.getInt("volume", 80);
        sbSpeed.setProgress(savedSpeed);
        sbTone.setProgress(savedTone);
        sbVolume.setProgress(savedVolume);
        tvSpeedValue.setText(formatSpeed(savedSpeed));
        tvToneValue.setText(formatTone(savedTone));
        tvVolumeValue.setText(savedVolume + "%");
        etText.setText(sp.getString("last_text", ""));

        // 初始化 TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true;
                applyVoiceSettings(sbSpeed.getProgress(), sbTone.getProgress(), sbVolume.getProgress());
                tvStatus.setText("TTS 引擎就绪");
            } else {
                tvStatus.setText("TTS 初始化失败");
            }
        });

        // 语速滑块
        sbSpeed.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSpeedValue.setText(formatSpeed(progress));
                if (ttsReady) applySpeed(progress);
            }
        });

        // 音调滑块
        sbTone.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvToneValue.setText(formatTone(progress));
                if (ttsReady) applyTone(progress);
            }
        });

        // 音量滑块
        sbVolume.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvVolumeValue.setText(progress + "%");
                if (ttsReady) applyVolume(progress);
            }
        });

        // 合成播放
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
            int voiceIndex = spVoice.getSelectedItemPosition();
            switch (voiceIndex) {
                case 0: case 2: tts.setLanguage(Locale.CHINESE); break;
                case 1: case 3: tts.setLanguage(Locale.CHINESE); break;
                case 4: case 5: tts.setLanguage(Locale.ENGLISH); break;
                case 6: case 7: tts.setLanguage(Locale.JAPANESE); break;
            }
            applyVoiceSettings(sbSpeed.getProgress(), sbTone.getProgress(), sbVolume.getProgress());
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ttsutterance");
            tvStatus.setText("正在合成播放...");
        });

        btnStop.setOnClickListener(v -> {
            if (tts != null && tts.isSpeaking()) {
                tts.stop();
                tvStatus.setText("已停止");
            }
        });

        // 保存设置
        btnSave.setOnClickListener(v -> {
            sp.edit()
                    .putInt("speed", sbSpeed.getProgress())
                    .putInt("tone", sbTone.getProgress())
                    .putInt("volume", sbVolume.getProgress())
                    .putString("last_text", etText.getText().toString())
                    .putInt("voice", spVoice.getSelectedItemPosition())
                    .apply();
            Toast.makeText(this, "TTS设置已保存", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void applyVoiceSettings(int speed, int tone, int volume) {
        applySpeed(speed);
        applyTone(tone);
        applyVolume(volume);
    }

    private void applySpeed(int progress) {
        // 0.5x ~ 2.0x
        float speed = 0.5f + (progress / 100f) * 1.5f;
        tts.setSpeechRate(speed);
    }

    private void applyTone(int progress) {
        // 0.5x ~ 2.0x
        float tone = 0.5f + (progress / 100f) * 1.5f;
        tts.setPitch(tone);
    }

    private void applyVolume(int progress) {
        // 通过 StreamMusic 控制
        android.media.AudioManager am = (android.media.AudioManager) getSystemService(AUDIO_SERVICE);
        if (am != null) {
            int maxVol = am.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC);
            int vol = (int) (maxVol * progress / 100f);
            am.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, vol, 0);
        }
    }

    private String formatSpeed(int progress) {
        float speed = 0.5f + (progress / 100f) * 1.5f;
        return String.format("%.1fx", speed);
    }

    private String formatTone(int progress) {
        float tone = 0.5f + (progress / 100f) * 1.5f;
        return String.format("%.1fx", tone);
    }

    abstract static class SimpleSeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override public void onStopTrackingTouch(SeekBar seekBar) {}
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
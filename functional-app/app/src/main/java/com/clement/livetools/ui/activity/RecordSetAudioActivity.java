package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.clement.livetools.R;

public class RecordSetAudioActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_set_audio);

        Spinner spSampleRate = findViewById(R.id.sp_sample_rate);
        Spinner spChannel = findViewById(R.id.sp_channel);
        Spinner spBitrate = findViewById(R.id.sp_bitrate);

        String[] sampleRates = {"44100Hz", "48000Hz", "22050Hz", "16000Hz"};
        String[] channels = {"立体声", "单声道"};
        String[] bitrates = {"320kbps", "256kbps", "192kbps", "128kbps", "64kbps"};

        spSampleRate.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sampleRates));
        spChannel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, channels));
        spBitrate.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bitrates));

        SharedPreferences sp = getSharedPreferences("record_settings", MODE_PRIVATE);
        spSampleRate.setSelection(sp.getInt("sample_rate", 0));
        spChannel.setSelection(sp.getInt("channel", 0));
        spBitrate.setSelection(sp.getInt("audio_bitrate", 3));

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("record_settings", MODE_PRIVATE).edit();
            editor.putInt("sample_rate", spSampleRate.getSelectedItemPosition());
            editor.putInt("channel", spChannel.getSelectedItemPosition());
            editor.putInt("audio_bitrate", spBitrate.getSelectedItemPosition());
            editor.apply();
            Toast.makeText(this, "录音设置已保存", Toast.LENGTH_SHORT).show();
            finish();
        });
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}
package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.clement.livetools.R;

public class RecordSetVideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_set_video);

        Spinner spResolution = findViewById(R.id.sp_resolution);
        Spinner spFps = findViewById(R.id.sp_fps);
        Spinner spBitrate = findViewById(R.id.sp_bitrate);

        String[] resolutions = {"1920x1080", "1280x720", "854x480", "640x360"};
        String[] fps = {"60fps", "30fps", "24fps", "15fps"};
        String[] bitrates = {"10Mbps", "8Mbps", "5Mbps", "3Mbps", "1Mbps"};

        spResolution.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, resolutions));
        spFps.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fps));
        spBitrate.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bitrates));

        SharedPreferences sp = getSharedPreferences("record_settings", MODE_PRIVATE);
        spResolution.setSelection(sp.getInt("resolution", 1));
        spFps.setSelection(sp.getInt("fps", 1));
        spBitrate.setSelection(sp.getInt("bitrate", 2));

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("record_settings", MODE_PRIVATE).edit();
            editor.putInt("resolution", spResolution.getSelectedItemPosition());
            editor.putInt("fps", spFps.getSelectedItemPosition());
            editor.putInt("bitrate", spBitrate.getSelectedItemPosition());
            editor.apply();
            Toast.makeText(this, "录影设置已保存", Toast.LENGTH_SHORT).show();
            finish();
        });
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}
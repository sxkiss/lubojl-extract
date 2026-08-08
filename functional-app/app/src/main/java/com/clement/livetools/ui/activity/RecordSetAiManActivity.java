package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clement.livetools.R;

public class RecordSetAiManActivity extends Activity {

    private TextView tvAlphaValue, tvBgValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_set_ai_man);

        Spinner spCharacter = findViewById(R.id.sp_character);
        Spinner spModel = findViewById(R.id.sp_model);
        SeekBar sbAlpha = findViewById(R.id.sb_alpha);
        SeekBar sbBg = findViewById(R.id.sb_bg_blur);
        tvAlphaValue = findViewById(R.id.tv_alpha_value);
        tvBgValue = findViewById(R.id.tv_bg_blur_value);

        // 原版虚拟人物列表
        String[] characters = {"小晨", "小涵", "小嘉", "小曼", "小沫", "小秋", "小睿", "小霜", "小晓", "小雪", "小言", "小悠", "小雨", "小真", "云龙", "云希", "云阳", "云叶", "云哲"};
        String[] models = {"RVM MobileNet FP16", "RVM MobileNet FP32"};

        spCharacter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, characters));
        spModel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, models));

        SharedPreferences sp = getSharedPreferences("ai_settings", MODE_PRIVATE);
        spCharacter.setSelection(sp.getInt("character", 0));
        spModel.setSelection(sp.getInt("model", 0));
        int alpha = sp.getInt("alpha", 80);
        int bgBlur = sp.getInt("bg_blur", 50);
        tvAlphaValue.setText(alpha + "%");
        tvBgValue.setText(bgBlur + "%");

        sbAlpha.setProgress(alpha);
        sbBg.setProgress(bgBlur);

        sbAlpha.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvAlphaValue.setText(progress + "%");
            }
        });

        sbBg.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvBgValue.setText(progress + "%");
            }
        });

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("ai_settings", MODE_PRIVATE).edit();
            editor.putInt("character", spCharacter.getSelectedItemPosition());
            editor.putInt("model", spModel.getSelectedItemPosition());
            editor.putInt("alpha", sbAlpha.getProgress());
            editor.putInt("bg_blur", sbBg.getProgress());
            editor.apply();
            Toast.makeText(this, "AI设置已保存", Toast.LENGTH_SHORT).show();
            finish();
        });
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    abstract static class SimpleSeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override public void onStopTrackingTouch(SeekBar seekBar) {}
    }
}
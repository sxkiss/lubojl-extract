package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clement.livetools.R;

/**
 * 推流主頁 - 对标原版 activity_push_main.xml
 * 功能：悬浮窗权限、视频文件选择、视频地址、推流地址、开始直播
 */
public class PushMainActivity extends Activity {

    private EditText etVideoUrl, etPushAddress, etPushKey;
    private RadioGroup rgPushType;
    private TextView tvVersion, tvFloatStatus;
    private Button btnParse, btnStart, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_main);

        etVideoUrl = findViewById(R.id.et_video_url);
        etPushAddress = findViewById(R.id.et_push_address);
        etPushKey = findViewById(R.id.et_push_key);
        rgPushType = findViewById(R.id.rg_push_type);
        tvVersion = findViewById(R.id.tv_version);
        tvFloatStatus = findViewById(R.id.tv_float_status);
        btnParse = findViewById(R.id.btn_parse);
        btnStart = findViewById(R.id.btn_start);
        btnSave = findViewById(R.id.btn_save_push);

        tvVersion.setText("软件版本: V1.0.0");

        // 加载保存的设置
        SharedPreferences sp = getSharedPreferences("push_settings", MODE_PRIVATE);
        etVideoUrl.setText(sp.getString("video_url", ""));
        etPushAddress.setText(sp.getString("push_address", ""));
        etPushKey.setText(sp.getString("push_key", ""));

        // 视频解析
        btnParse.setOnClickListener(v -> {
            String url = etVideoUrl.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, "请输入视频地址", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "正在解析...", Toast.LENGTH_SHORT).show();
            // TODO: 解析视频地址
        });

        // 开始直播
        btnStart.setOnClickListener(v -> {
            String pushAddress = etPushAddress.getText().toString().trim();
            String pushKey = etPushKey.getText().toString().trim();
            if (pushAddress.isEmpty()) {
                Toast.makeText(this, "请输入推流地址", Toast.LENGTH_SHORT).show();
                return;
            }

            // 保存设置
            sp.edit()
                    .putString("video_url", etVideoUrl.getText().toString())
                    .putString("push_address", pushAddress)
                    .putString("push_key", pushKey)
                    .putInt("push_type", rgPushType.getCheckedRadioButtonId())
                    .apply();

            // 启动推流
            Intent intent = new Intent(this, RecordActivity.class);
            intent.putExtra("push_url", pushAddress);
            intent.putExtra("push_key", pushKey);
            intent.putExtra("video_url", etVideoUrl.getText().toString());
            startActivity(intent);
        });

        // 保存推流设置
        btnSave.setOnClickListener(v -> {
            sp.edit()
                    .putString("push_address", etPushAddress.getText().toString())
                    .putString("push_key", etPushKey.getText().toString())
                    .apply();
            Toast.makeText(this, "推流设置已保存", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}
package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.clement.livetools.R;
import com.clement.livetools.base.ui.BaseWebViewActivity;

/**
 * 直播网页设置 - 对标原版 activity_record_set_web.xml
 * 配置直播推流的Web界面参数
 */
public class RecordSetWebActivity extends Activity {

    private EditText etWebUrl, etRefreshInterval;
    private Switch swAutoRefresh, swDesktopMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_set_web);

        etWebUrl = findViewById(R.id.et_web_url);
        etRefreshInterval = findViewById(R.id.et_refresh_interval);
        swAutoRefresh = findViewById(R.id.sw_auto_refresh);
        swDesktopMode = findViewById(R.id.sw_desktop_mode);

        SharedPreferences sp = getSharedPreferences("web_settings", MODE_PRIVATE);
        etWebUrl.setText(sp.getString("web_url", "https://"));
        etRefreshInterval.setText(sp.getString("refresh_interval", "30"));
        swAutoRefresh.setChecked(sp.getBoolean("auto_refresh", false));
        swDesktopMode.setChecked(sp.getBoolean("desktop_mode", true));

        findViewById(R.id.btn_open_web).setOnClickListener(v -> {
            String url = etWebUrl.getText().toString().trim();
            if (!url.startsWith("http")) url = "http://" + url;
            Intent intent = new Intent(this, BaseWebViewActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        });

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("web_url", etWebUrl.getText().toString());
            editor.putString("refresh_interval", etRefreshInterval.getText().toString());
            editor.putBoolean("auto_refresh", swAutoRefresh.isChecked());
            editor.putBoolean("desktop_mode", swDesktopMode.isChecked());
            editor.apply();
            Toast.makeText(this, "网页设置已保存", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}
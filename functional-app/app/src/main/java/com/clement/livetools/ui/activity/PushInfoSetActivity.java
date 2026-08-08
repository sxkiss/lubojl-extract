package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.clement.livetools.R;

public class PushInfoSetActivity extends Activity {

    private EditText etProxyHost, etProxyPort, etProxyUser, etProxyPass;
    private EditText etPushUrl, etStreamKey;
    private Switch swAutoReply;
    private EditText etAutoReplyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_info_set);

        etProxyHost = findViewById(R.id.et_proxy_host);
        etProxyPort = findViewById(R.id.et_proxy_port);
        etProxyUser = findViewById(R.id.et_proxy_user);
        etProxyPass = findViewById(R.id.et_proxy_pass);
        etPushUrl = findViewById(R.id.et_push_url);
        etStreamKey = findViewById(R.id.et_stream_key);
        swAutoReply = findViewById(R.id.sw_auto_reply);
        etAutoReplyText = findViewById(R.id.et_auto_reply_text);

        loadSettings();

        findViewById(R.id.btn_save).setOnClickListener(v -> saveSettings());
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        SharedPreferences sp = getSharedPreferences("app_settings", MODE_PRIVATE);
        etProxyHost.setText(sp.getString("proxy_host", ""));
        etProxyPort.setText(sp.getString("proxy_port", "1080"));
        etProxyUser.setText(sp.getString("proxy_user", ""));
        etProxyPass.setText(sp.getString("proxy_pass", ""));
        etPushUrl.setText(sp.getString("push_url", ""));
        etStreamKey.setText(sp.getString("stream_key", ""));
        swAutoReply.setChecked(sp.getBoolean("auto_reply", false));
        etAutoReplyText.setText(sp.getString("auto_reply_text", "你好，主播正在直播中，请稍等"));
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences("app_settings", MODE_PRIVATE).edit();
        editor.putString("proxy_host", etProxyHost.getText().toString());
        editor.putString("proxy_port", etProxyPort.getText().toString());
        editor.putString("proxy_user", etProxyUser.getText().toString());
        editor.putString("proxy_pass", etProxyPass.getText().toString());
        editor.putString("push_url", etPushUrl.getText().toString());
        editor.putString("stream_key", etStreamKey.getText().toString());
        editor.putBoolean("auto_reply", swAutoReply.isChecked());
        editor.putString("auto_reply_text", etAutoReplyText.getText().toString());
        editor.apply();
        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
    }
}
package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.clement.livetools.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PushInfoSetActivity extends Activity {

    private EditText etProxyHost, etProxyPort, etProxyUser, etProxyPass;
    private EditText etPushUrl, etStreamKey;
    private Switch swAutoReply;
    private LinearLayout llAutoReplySettings;
    private EditText etAutoReplyKeywords, etAutoReplyText;
    private LinearLayout llVoiceScript;
    private TextView tvVoiceCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_info_set);

        // 代理设置
        etProxyHost = findViewById(R.id.et_proxy_host);
        etProxyPort = findViewById(R.id.et_proxy_port);
        etProxyUser = findViewById(R.id.et_proxy_user);
        etProxyPass = findViewById(R.id.et_proxy_pass);

        // 推流设置
        etPushUrl = findViewById(R.id.et_push_url);
        etStreamKey = findViewById(R.id.et_stream_key);

        // 智能回复
        swAutoReply = findViewById(R.id.sw_auto_reply);
        llAutoReplySettings = findViewById(R.id.ll_auto_reply_settings);
        etAutoReplyKeywords = findViewById(R.id.et_auto_reply_keywords);
        etAutoReplyText = findViewById(R.id.et_auto_reply_text);

        // 语音脚本
        llVoiceScript = findViewById(R.id.ll_voice_script);
        tvVoiceCount = findViewById(R.id.tv_voice_count);

        loadSettings();

        // 智能回复开关
        swAutoReply.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llAutoReplySettings.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        llAutoReplySettings.setVisibility(swAutoReply.isChecked() ? View.VISIBLE : View.GONE);

        // 语音脚本管理
        findViewById(R.id.btn_add_voice).setOnClickListener(v -> showAddVoiceDialog());
        findViewById(R.id.btn_manage_voice).setOnClickListener(v -> showVoiceList());

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
        etAutoReplyKeywords.setText(sp.getString("auto_reply_keywords", "你好,在吗,主播"));
        etAutoReplyText.setText(sp.getString("auto_reply_text", "你好，主播正在直播中，请稍等"));

        Set<String> voices = sp.getStringSet("voice_scripts", new HashSet<>());
        tvVoiceCount.setText("已添加 " + voices.size() + " 条语音脚本");
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
        editor.putString("auto_reply_keywords", etAutoReplyKeywords.getText().toString());
        editor.putString("auto_reply_text", etAutoReplyText.getText().toString());
        editor.apply();
        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
    }

    private void showAddVoiceDialog() {
        View dialogView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
        EditText etName = new EditText(this);
        etName.setHint("语音脚本名称");
        etName.setPadding(48, 32, 48, 32);

        EditText etContent = new EditText(this);
        etContent.setHint("语音内容文本（TTS合成）");
        etContent.setPadding(48, 32, 48, 32);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(etName);
        layout.addView(etContent);

        new AlertDialog.Builder(this)
                .setTitle("添加语音脚本")
                .setView(layout)
                .setPositiveButton("添加", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String content = etContent.getText().toString().trim();
                    if (!name.isEmpty() && !content.isEmpty()) {
                        addVoiceScript(name, content);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void addVoiceScript(String name, String content) {
        SharedPreferences sp = getSharedPreferences("app_settings", MODE_PRIVATE);
        Set<String> voices = new HashSet<>(sp.getStringSet("voice_scripts", new HashSet<>()));
        voices.add(name + "|||" + content);
        sp.edit().putStringSet("voice_scripts", voices).apply();
        tvVoiceCount.setText("已添加 " + voices.size() + " 条语音脚本");
        Toast.makeText(this, "已添加: " + name, Toast.LENGTH_SHORT).show();
    }

    private void showVoiceList() {
        SharedPreferences sp = getSharedPreferences("app_settings", MODE_PRIVATE);
        Set<String> voices = sp.getStringSet("voice_scripts", new HashSet<>());
        ArrayList<String> voiceList = new ArrayList<>(voices);

        if (voiceList.isEmpty()) {
            Toast.makeText(this, "暂无语音脚本", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] items = voiceList.toArray(new String[0]);
        new AlertDialog.Builder(this)
                .setTitle("语音脚本列表")
                .setItems(items, (dialog, which) -> {
                    // 点击播放语音脚本
                    String[] parts = items[which].split("\\|\\|\\|");
                    if (parts.length == 2) {
                        Toast.makeText(this, "播放: " + parts[0], Toast.LENGTH_SHORT).show();
                        // TODO: 调用TTS播放
                    }
                })
                .setNeutralButton("删除", (dialog, which) -> {
                    // 删除最后一条
                    if (!voiceList.isEmpty()) {
                        voices.remove(voiceList.get(voiceList.size() - 1));
                        sp.edit().putStringSet("voice_scripts", voices).apply();
                        tvVoiceCount.setText("已添加 " + voices.size() + " 条语音脚本");
                    }
                })
                .setNegativeButton("关闭", null)
                .show();
    }
}
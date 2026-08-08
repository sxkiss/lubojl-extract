package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clement.livetools.R;
import com.clement.livetools.auth.AuthManager;

import org.json.JSONObject;

public class LoginActivity extends Activity {

    private EditText etCardKey;
    private Button btnLogin;
    private TextView tvStatus, tvNotice;
    private AuthManager authManager;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("app_config", MODE_PRIVATE);
        String savedKey = sp.getString("card_key", "");
        if (!savedKey.isEmpty()) {
            // 已有卡密，验证是否有效
            checkExistingLogin(savedKey);
            return;
        }

        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews() {
        etCardKey = findViewById(R.id.et_card_key);
        btnLogin = findViewById(R.id.btn_login);
        tvStatus = findViewById(R.id.tv_status);
        tvNotice = findViewById(R.id.tv_notice);

        authManager = new AuthManager(this);

        // 加载公告
        loadNotice();

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void loadNotice() {
        new Thread(() -> {
            try {
                JSONObject resp = authManager.getNotice();
                if (resp.optInt("code") == 1) {
                    String notice = resp.optJSONObject("data") != null ?
                            resp.optJSONObject("data").optString("notice", "") : "";
                    if (!notice.isEmpty()) {
                        handler.post(() -> tvNotice.setText(notice));
                    }
                }
            } catch (Exception e) {}
        }).start();
    }

    private void checkExistingLogin(String cardKey) {
        setContentView(R.layout.activity_loading);
        authManager = new AuthManager(this);

        new Thread(() -> {
            try {
                JSONObject resp = authManager.checkLoginStatus();
                int code = resp.optInt("code", -1);
                if (code == 1) {
                    // 已登录，检查是否过期
                    JSONObject data = resp.optJSONObject("data");
                    if (data != null) {
                        long expireTs = data.optLong("expire_timestamp", 0);
                        if (expireTs > 0 && expireTs < System.currentTimeMillis() / 1000) {
                            // 已过期
                            handler.post(() -> {
                                setContentView(R.layout.activity_login);
                                initViews();
                                etCardKey.setText(cardKey);
                                Toast.makeText(this, "卡密已过期，请重新激活", Toast.LENGTH_LONG).show();
                            });
                            return;
                        }
                    }
                    // 有效，直接进入主页
                    handler.post(this::goToHome);
                } else {
                    // 未登录或验证失败
                    handler.post(() -> {
                        setContentView(R.layout.activity_login);
                        initViews();
                        etCardKey.setText(cardKey);
                    });
                }
            } catch (Exception e) {
                handler.post(() -> {
                    setContentView(R.layout.activity_login);
                    initViews();
                    etCardKey.setText(cardKey);
                    Toast.makeText(this, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void doLogin() {
        String cardKey = etCardKey.getText().toString().trim();
        if (cardKey.isEmpty()) {
            Toast.makeText(this, "请输入卡密", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        tvStatus.setText("正在验证...");

        new Thread(() -> {
            try {
                JSONObject resp = authManager.activate(cardKey);
                int code = resp.optInt("code", -1);
                String msg = resp.optString("msg", "未知错误");

                handler.post(() -> {
                    if (code == 1) {
                        // 激活成功，保存卡密
                        getSharedPreferences("app_config", MODE_PRIVATE).edit()
                                .putString("card_key", cardKey)
                                .putBoolean("logged_in", true)
                                .apply();
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        goToHome();
                    } else {
                        tvStatus.setText(msg);
                        btnLogin.setEnabled(true);
                    }
                });
            } catch (Exception e) {
                handler.post(() -> {
                    tvStatus.setText("网络连接失败");
                    btnLogin.setEnabled(true);
                });
            }
        }).start();
    }

    private void goToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
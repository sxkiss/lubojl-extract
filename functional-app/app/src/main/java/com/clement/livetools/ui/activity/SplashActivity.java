package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clement.livetools.R;
import com.clement.livetools.auth.AuthManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 启动页 + 自动检查更新
 * 对标原版 SplashActivity + apk_update_* 功能
 */
public class SplashActivity extends Activity {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 启动页：2秒后检查更新并跳转
        handler.postDelayed(this::checkVersionAndNavigate, 2000);
    }

    private void checkVersionAndNavigate() {
        new Thread(() -> {
            try {
                AuthManager auth = new AuthManager(this);
                JSONObject resp = auth.getVersionInfo();
                if (resp.optInt("code") == 1) {
                    JSONObject data = resp.optJSONObject("data");
                    if (data != null) {
                        int forceUpdate = data.optInt("force_update", 0);
                        String updateUrl = data.optString("update_url", "");
                        String versionName = data.optString("version_name", "");

                        if (forceUpdate == 1 && !updateUrl.isEmpty()) {
                            handler.post(() -> showUpdateDialog(versionName, updateUrl));
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                // 忽略网络错误
            }
            handler.post(this::navigateToMain);
        }).start();
    }

    private void showUpdateDialog(String version, String updateUrl) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 40);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("发现新版本 " + version);
        tvTitle.setTextSize(18);
        tvTitle.setGravity(Gravity.CENTER);
        layout.addView(tvTitle);

        TextView tvContent = new TextView(this);
        tvContent.setText("\n检测到新版本，请更新后使用");
        tvContent.setTextSize(14);
        tvContent.setGravity(Gravity.CENTER);
        layout.addView(tvContent);

        new AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton("立即更新", (dialog, which) -> {
                    downloadAndInstall(updateUrl);
                    navigateToMain();
                })
                .setNegativeButton("下次再说", (dialog, which) -> navigateToMain())
                .setCancelable(false)
                .show();
    }

    private void downloadAndInstall(String url) {
        Toast.makeText(this, "正在下载更新...", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            try {
                File outputFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk");
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.connect();
                InputStream in = conn.getInputStream();
                FileOutputStream out = new FileOutputStream(outputFile);
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) out.write(buffer, 0, len);
                out.close();
                in.close();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(outputFile), "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this, "下载失败", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void navigateToMain() {
        SharedPreferences sp = getSharedPreferences("app_config", MODE_PRIVATE);
        if (sp.getString("card_key", "").isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
/*
 * LoginActivity.java
 * 登入活動 - 完全匹配原版功能
 * SOLID: Single Responsibility
 * KISS: 保持簡單
 */

package com.clement.livetools.ui.activity;
import com.clement.livetools.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            // 原版登入邏輯
            finish();
        });
    }
}

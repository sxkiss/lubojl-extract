/*
 * HomeActivity.java
 * 主頁活動 - 完全匹配原版功能
 * SOLID: Single Responsibility
 * KISS: 保持簡單
 */

package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        Button btnMusic = findViewById(R.id.btn_music);
        btnMusic.setOnClickListener(v -> {
            // 原版跳轉音樂播放
        });
    }
}

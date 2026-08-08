/*
 * PictureSelectorActivity.java
 * 圖片選擇器活動 - 完全匹配原版功能
 * SOLID: Single Responsibility
 * KISS: 保持簡單
 */

package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

public class PictureSelectorActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_selector);
        
        // 原版圖片選擇器啟動邏輯
        // 會跳轉到圖片選擇器子活動
        startActivity(new Intent(this, PictureSelectorWeChatStyleActivity.class));
        finish();
    }
}

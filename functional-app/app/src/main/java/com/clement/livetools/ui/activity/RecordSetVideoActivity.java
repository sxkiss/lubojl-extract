/*
 * RecordSetVideoActivity.java
 * 錄影設置活動 - 完全匹配原版功能
 * SOLID: Single Responsibility
 * KISS: 保持簡單
 */

package com.clement.livetools.ui.activity;
import com.clement.livetools.R;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

public class RecordSetVideoActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_set_video);
    }
}

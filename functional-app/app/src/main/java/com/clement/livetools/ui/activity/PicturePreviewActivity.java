/*
 * PicturePreviewActivity.java
 * 圖片預覽活動 - 完全匹配原版功能
 */

package com.clement.livetools.ui.activity;
import com.clement.livetools.R;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

public class PicturePreviewActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
    }
}

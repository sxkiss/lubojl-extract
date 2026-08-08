package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import com.clement.livetools.R;

public class PictureSelectorPreviewWeChatStyleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_selector_preview_wechat_style);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}

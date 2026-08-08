package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.clement.livetools.R;

public class PicturePreviewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);

        ImageView ivPreview = findViewById(R.id.iv_preview);
        TextView tvInfo = findViewById(R.id.tv_info);

        String path = getIntent().getStringExtra("path");
        if (path != null) {
            ivPreview.setImageBitmap(BitmapFactory.decodeFile(path));
            tvInfo.setText(path.substring(path.lastIndexOf("/") + 1));
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            setResult(RESULT_OK, getIntent());
            finish();
        });
    }
}
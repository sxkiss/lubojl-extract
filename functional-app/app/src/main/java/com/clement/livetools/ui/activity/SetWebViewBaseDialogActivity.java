package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.clement.livetools.R;
import com.clement.livetools.base.ui.BaseWebViewActivity;

public class SetWebViewBaseDialogActivity extends Activity {

    private EditText etUrl;
    private Switch swJs, swZoom, swStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_webview_base_dialog);

        etUrl = findViewById(R.id.et_webview_url);
        swJs = findViewById(R.id.sw_javascript);
        swZoom = findViewById(R.id.sw_zoom);
        swStorage = findViewById(R.id.sw_storage);

        SharedPreferences sp = getSharedPreferences("webview_settings", MODE_PRIVATE);
        etUrl.setText(sp.getString("url", "https://"));
        swJs.setChecked(sp.getBoolean("javascript", true));
        swZoom.setChecked(sp.getBoolean("zoom", true));
        swStorage.setChecked(sp.getBoolean("storage", true));

        findViewById(R.id.btn_open).setOnClickListener(v -> {
            String url = etUrl.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, "请输入URL", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            sp.edit()
                    .putString("url", url)
                    .putBoolean("javascript", swJs.isChecked())
                    .putBoolean("zoom", swZoom.isChecked())
                    .putBoolean("storage", swStorage.isChecked())
                    .apply();

            Intent intent = new Intent(this, BaseWebViewActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}
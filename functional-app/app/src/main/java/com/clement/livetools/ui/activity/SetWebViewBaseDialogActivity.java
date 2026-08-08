package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import com.clement.livetools.R;

public class SetWebViewBaseDialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_webview_base_dialog);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}

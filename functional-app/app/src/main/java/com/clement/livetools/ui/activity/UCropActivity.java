package com.clement.livetools.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import com.clement.livetools.R;

public class UCropActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ucrop);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}

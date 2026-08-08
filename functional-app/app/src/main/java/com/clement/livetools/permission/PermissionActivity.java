/*
 * PermissionActivity.java
 * 权限请求活动 - 匹配原版 com.clement.livetools.permission.PermissionActivity
 * SOLID: Single Responsibility
 * KISS: 保持简单
 */

package com.clement.livetools.permission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PermissionActivity extends Activity {

    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final int REQUEST_CODE_OVERLAY = 1002;
    private static final int REQUEST_CODE_MANAGE_STORAGE = 1003;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] needed = {
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_PHONE_STATE
            };
            for (String perm : needed) {
                if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(perm);
                }
            }
        }

        if (!permissions.isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
        } else {
            onAllPermissionsGranted();
        }
    }

    private void onAllPermissionsGranted() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                onAllPermissionsGranted();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
}

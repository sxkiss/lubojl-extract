package com.clement.livetools.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.clement.livetools.R;

import java.util.ArrayList;
import java.util.List;

public class PictureSelectorActivity extends Activity {

    private List<String> imagePaths = new ArrayList<>();
    private List<String> selectedPaths = new ArrayList<>();
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_selector);

        gridView = findViewById(R.id.grid_images);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            if (selectedPaths.isEmpty()) {
                Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent data = new Intent();
            data.putStringArrayListExtra("selected", new ArrayList<>(selectedPaths));
            setResult(RESULT_OK, data);
            finish();
        });

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            loadImages();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) loadImages();
    }

    private void loadImages() {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.DATA}, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) imagePaths.add(cursor.getString(0));
            cursor.close();
        }
        gridView.setAdapter(new BaseAdapter() {
            @Override public int getCount() { return imagePaths.size(); }
            @Override public Object getItem(int p) { return imagePaths.get(p); }
            @Override public long getItemId(int p) { return p; }
            @Override
            public View getView(int position, View cv, ViewGroup parent) {
                if (cv == null) cv = LayoutInflater.from(PictureSelectorActivity.this).inflate(R.layout.item_image_grid, parent, false);
                ImageView iv = cv.findViewById(R.id.iv_image);
                CheckBox cb = cv.findViewById(R.id.cb_select);
                String path = imagePaths.get(position);
                iv.setImageBitmap(BitmapFactory.decodeFile(path));
                cb.setChecked(selectedPaths.contains(path));
                cv.setOnClickListener(v -> {
                    if (selectedPaths.contains(path)) { selectedPaths.remove(path); cb.setChecked(false); }
                    else { selectedPaths.add(path); cb.setChecked(true); }
                });
                return cv;
            }
        });
    }
}
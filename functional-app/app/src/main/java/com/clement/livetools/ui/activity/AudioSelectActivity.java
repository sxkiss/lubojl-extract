package com.clement.livetools.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.clement.livetools.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioSelectActivity extends Activity {

    private List<Map<String, String>> audioList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_select);

        listView = findViewById(R.id.list_audio);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            loadAudioFiles();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadAudioFiles();
        }
    }

    private void loadAudioFiles() {
        audioList.clear();
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION},
                MediaStore.Audio.Media.IS_MUSIC + "=1", null,
                MediaStore.Audio.Media.TITLE + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, String> song = new HashMap<>();
                song.put("title", cursor.getString(1));
                song.put("path", cursor.getString(2));
                song.put("duration", cursor.getString(3));
                audioList.add(song);
            }
            cursor.close();
        }

        List<String> titles = new ArrayList<>();
        for (Map<String, String> song : audioList) {
            titles.add(song.get("title"));
        }

        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("path", audioList.get(position).get("path"));
            resultIntent.putExtra("title", audioList.get(position).get("title"));
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
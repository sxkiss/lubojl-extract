package com.clement.livetools.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.clement.livetools.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioLibraryActivity extends Activity {

    private List<Map<String, String>> musicList = new ArrayList<>();
    private ListView listView;
    private TextView tvEmpty;
    private MediaPlayer mediaPlayer;
    private int currentPlaying = -1;
    private MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_library);

        listView = findViewById(R.id.list_music);
        tvEmpty = findViewById(R.id.tv_empty);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            loadMusic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadMusic();
        }
    }

    private void loadMusic() {
        musicList.clear();
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media.IS_MUSIC + "=1", null,
                MediaStore.Audio.Media.TITLE + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, String> song = new HashMap<>();
                song.put("id", cursor.getString(0));
                song.put("title", cursor.getString(1));
                song.put("artist", cursor.getString(2));
                song.put("duration", cursor.getString(3));
                song.put("path", cursor.getString(4));
                musicList.add(song);
            }
            cursor.close();
        }

        if (musicList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            adapter = new MusicAdapter();
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> playMusic(position));
        }
    }

    private void playMusic(int position) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(musicList.get(position).get("path"));
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentPlaying = position;
            if (adapter != null) adapter.notifyDataSetChanged();
            mediaPlayer.setOnCompletionListener(mp -> {
                currentPlaying = -1;
                if (adapter != null) adapter.notifyDataSetChanged();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MusicAdapter extends BaseAdapter {
        @Override
        public int getCount() { return musicList.size(); }
        @Override
        public Object getItem(int position) { return musicList.get(position); }
        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(AudioLibraryActivity.this)
                        .inflate(android.R.layout.simple_list_item_2, parent, false);
            }
            Map<String, String> song = musicList.get(position);
            TextView tv1 = convertView.findViewById(android.R.id.text1);
            TextView tv2 = convertView.findViewById(android.R.id.text2);
            tv1.setText(song.get("title"));
            String duration = song.get("duration");
            long ms = Long.parseLong(duration != null ? duration : "0");
            tv2.setText(song.get("artist") + " | " + (ms / 60000) + ":" + String.format("%02d", (ms / 1000) % 60));
            if (position == currentPlaying) {
                tv1.setTextColor(0xFFFF6200);
            } else {
                tv1.setTextColor(0xFF000000);
            }
            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
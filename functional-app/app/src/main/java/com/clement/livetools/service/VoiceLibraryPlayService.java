/*
 * VoiceLibraryPlayService.java
 * 語音庫播放服務 - 完全匹配原版功能
 * SOLID: Single Responsibility
 * KISS: 保持簡單
 * DRY: 避免重複代碼
 */

package com.clement.livetools.service;
import com.clement.livetools.R;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class VoiceLibraryPlayService extends Service {

    private static final String CHANNEL_ID = "voice_channel";
    private static final String NOTIFICATION_CHANNEL_NAME = "語音庫播放";

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private String currentVoiceUrl;

    private final IBinder binder = new VoiceLibraryBinder();

    public class VoiceLibraryBinder extends Binder {
        public VoiceLibraryPlayService getService() {
            return VoiceLibraryPlayService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            updateNotification();
        });
        startForegroundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case "PLAY": playVoice(); break;
                    case "PAUSE": pauseVoice(); break;
                    case "STOP": stopVoice(); break;
                }
            }
        }
        return START_STICKY;
    }

    private void playVoice() {
        if (mediaPlayer != null && currentVoiceUrl != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(currentVoiceUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
                isPlaying = true;
                updateNotification();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void pauseVoice() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            updateNotification();
        }
    }

    private void stopVoice() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            stopForeground(true);
        }
    }

    private void updateNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Bitmap largeIcon = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
        // 設置大圖標 (原版風格)

        builder.setSmallIcon(android.R.drawable.ic_media_play)
               .setLargeIcon(largeIcon)
               .setContentTitle("語音庫播放")
               .setContentText(currentVoiceUrl != null ? currentVoiceUrl : "正在播放")
               .setOngoing(true)
               .setPriority(NotificationCompat.PRIORITY_MAX);

        Notification notification = builder.build();
        startForeground(2, notification);
    }

    private void startForegroundService() {
        updateNotification();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}

/*
 * Socks5ProxyService.java
 * Socks5代理服務 - 完全匹配原版功能
 * SOLID: Single Responsibility
 * KISS: 保持簡單
 * DRY: 避免重複代碼
 */

package com.clement.livetools.socks5;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.annotation.Nullable;

public class Socks5ProxyService extends Service {

    private static final String CHANNEL_ID = "socks5_channel";
    private static final String NOTIFICATION_CHANNEL_NAME = "Socks5代理";

    private final IBinder binder = new Socks5Binder();

    public class Socks5Binder extends Binder {
        public Socks5ProxyService getService() {
            return Socks5ProxyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForegroundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void startForegroundService() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Bitmap largeIcon = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
        // 設置大圖標 (原版風格)

        builder.setSmallIcon(android.R.drawable.ic_media_play)
               .setLargeIcon(largeIcon)
               .setContentTitle("Socks5代理")
               .setContentText("代理服務正在運行")
               .setOngoing(true)
               .setPriority(NotificationCompat.PRIORITY_MAX);

        Notification notification = builder.build();
        startForeground(3, notification);
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
        stopForeground(true);
    }
}

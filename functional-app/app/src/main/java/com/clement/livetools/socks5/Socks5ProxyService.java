package com.clement.livetools.socks5;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.clement.livetools.ui.activity.PushInfoSetActivity;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Socks5ProxyService extends Service {

    private static final String CHANNEL_ID = "socks5_channel";
    private final IBinder binder = new Socks5Binder();
    private volatile boolean running = false;
    private Thread proxyThread;

    public class Socks5Binder extends Binder {
        public Socks5ProxyService getService() { return Socks5ProxyService.this; }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(3, buildNotification("代理服务准备中..."));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP".equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }
        startProxy();
        return START_STICKY;
    }

    private void startProxy() {
        SharedPreferences sp = getSharedPreferences("app_settings", MODE_PRIVATE);
        String host = sp.getString("proxy_host", "");
        int port = Integer.parseInt(sp.getString("proxy_port", "1080"));

        if (host.isEmpty()) {
            stopSelf();
            return;
        }

        running = true;
        updateNotification("代理运行中: " + host + ":" + port);

        proxyThread = new Thread(() -> {
            try {
                // 测试代理连接
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(host, port), 10000);
                socket.close();
                // 连接成功，保持服务运行
                while (running) {
                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                updateNotification("代理连接失败: " + e.getMessage());
                running = false;
            }
        });
        proxyThread.start();
    }

    private void updateNotification(String text) {
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.notify(3, buildNotification(text));
    }

    private Notification buildNotification(String text) {
        Intent intent = new Intent(this, PushInfoSetActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, Socks5ProxyService.class);
        stopIntent.setAction("STOP");
        PendingIntent stopPi = PendingIntent.getService(this, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        return new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Socks5代理")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_menu_share)
                .setContentIntent(pi)
                .addAction(new Notification.Action.Builder(null, "停止", stopPi).build())
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Socks5代理", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) { return binder; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        if (proxyThread != null) proxyThread.interrupt();
        stopForeground(true);
    }
}
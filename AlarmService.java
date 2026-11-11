package com.wakeup.pro;

import android.app.*;
import android.content.*;
import android.os.*;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import java.util.*;

public class AlarmService extends Service {
    private static final int NOTIFICATION_ID = 900;
    private static final String CHANNEL_ID = "wake_alarm_channel";
    private TextToSpeech tts;
    private boolean active = true;
    private int messageIndex = 0;
    private final String[] messages = {
        "Wake up! Your day has started. Get out of bed now.",
        "No more delay. Stand up, get ready, and begin your work.",
        "You made a plan. Now follow it. Wake up and get moving.",
        "Time to get up. Your goals need your effort today."
    };

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
        if (Build.VERSION.SDK_INT >= 26) {
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (v != null && v.hasVibrator()) {
                v.vibrate(VibrationEffect.createWaveform(new long[]{0,700,300,700,500}, 0));
            }
        }
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale locale = Locale.US;
                int result = tts.setLanguage(locale);
                tts.setPitch(0.78f);
                tts.setSpeechRate(0.90f);
                speakNext();
            }
        });
        if (tts != null) {
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override public void onStart(String id) {}
                @Override public void onDone(String id) { new Handler(Looper.getMainLooper()).postDelayed(() -> speakNext(), 1400); }
                @Override public void onError(String id) { new Handler(Looper.getMainLooper()).postDelayed(() -> speakNext(), 2500); }
            });
        }
    }

    private void speakNext() {
        if (!active || tts == null) return;
        String msg = messages[messageIndex++ % messages.length];
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, "wake-" + messageIndex);
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "Wake Up Pro Alarm", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Active wake-up alarm");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Intent open = new Intent(this, AlarmActivity.class);
        PendingIntent full = PendingIntent.getActivity(this, 901, open,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Intent stop = new Intent(this, AlarmDismissReceiver.class);
        PendingIntent dismiss = PendingIntent.getBroadcast(this, 902, stop,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b = Build.VERSION.SDK_INT >= 26
            ? new Notification.Builder(this, CHANNEL_ID) : new Notification.Builder(this);
        return b.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("WAKE UP PRO — TIME TO GET UP")
            .setContentText("The alarm is active. Tap dismiss when you are awake.")
            .setCategory(Notification.CATEGORY_ALARM)
            .setPriority(Notification.PRIORITY_MAX)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true).setFullScreenIntent(full, true)
            .addAction(android.R.drawable.ic_media_pause, "DISMISS", dismiss)
            .build();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }

    @Override public void onDestroy() {
        active = false;
        if (tts != null) { tts.stop(); tts.shutdown(); }
        try {
            Vibrator v = (Vibrator)getSystemService(VIBRATOR_SERVICE);
            if (v != null) v.cancel();
        } catch (Exception ignored) {}
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (nm != null) nm.cancel(NOTIFICATION_ID);
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}

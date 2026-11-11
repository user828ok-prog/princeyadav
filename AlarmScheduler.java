package com.wakeup.pro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;

public class AlarmScheduler {
    public static final String PREFS = "wake_up_pro";
    public static final String ENABLED = "enabled";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";

    public static void schedule(Context context, int hour, int minute) {
        Calendar when = Calendar.getInstance();
        when.set(Calendar.HOUR_OF_DAY, hour);
        when.set(Calendar.MINUTE, minute);
        when.set(Calendar.SECOND, 0);
        when.set(Calendar.MILLISECOND, 0);
        if (!when.after(Calendar.getInstance())) when.add(Calendar.DAY_OF_YEAR, 1);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pending = alarmPendingIntent(context);
        if (manager == null) return;
        if (Build.VERSION.SDK_INT >= 23) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pending);
        } else {
            manager.setExact(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pending);
        }
    }

    public static void cancel(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) manager.cancel(alarmPendingIntent(context));
    }

    private static PendingIntent alarmPendingIntent(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(context, 1001, intent, flags);
    }

    public static void scheduleSaved(Context context) {
        android.content.SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (p.getBoolean(ENABLED, false)) {
            schedule(context, p.getInt(HOUR, 7), p.getInt(MINUTE, 0));
        }
    }
}

package com.wakeup.pro;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private int hour = 7, minute = 0;
    private TextView timeText, statusText, clockText;
    private SharedPreferences prefs;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences(AlarmScheduler.PREFS, MODE_PRIVATE);
        hour = prefs.getInt(AlarmScheduler.HOUR, 7);
        minute = prefs.getInt(AlarmScheduler.MINUTE, 0);
        buildScreen();
        requestNotificationPermission();
        updateStatus();
    }

    private void buildScreen() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(24), dp(34), dp(24), dp(24));
        page.setBackgroundColor(Color.rgb(10, 17, 35));

        TextView brand = text("WAKE UP PRO", 25, Color.WHITE, true);
        page.addView(brand);
        TextView sub = text("DISCIPLINE STARTS HERE", 11, Color.rgb(151, 173, 211), true);
        sub.setPadding(0, dp(5), 0, dp(25));
        page.addView(sub);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(20), dp(28), dp(20), dp(28));
        card.setBackgroundColor(Color.rgb(24, 36, 63));
        page.addView(card, new LinearLayout.LayoutParams(-1, -2));

        clockText = text("", 17, Color.rgb(170, 190, 224), false);
        card.addView(clockText);
        timeText = text(formatTime(), 54, Color.WHITE, true);
        timeText.setPadding(0, dp(12), 0, dp(18));
        card.addView(timeText);

        Button choose = button("CHOOSE ALARM TIME");
        card.addView(choose, matchWrap());
        choose.setOnClickListener(v -> new TimePickerDialog(this, (view, h, m) -> {
            hour = h; minute = m;
            timeText.setText(formatTime());
            prefs.edit().putInt(AlarmScheduler.HOUR, hour).putInt(AlarmScheduler.MINUTE, minute).apply();
            updateStatus();
        }, hour, minute, false).show());

        Button save = button("SAVE & ENABLE ALARM");
        LinearLayout.LayoutParams sp = matchWrap();
        sp.topMargin = dp(14);
        card.addView(save, sp);
        save.setOnClickListener(v -> enableAlarm());

        Button disable = button("TURN ALARM OFF");
        LinearLayout.LayoutParams dp = matchWrap();
        dp.topMargin = dp(10);
        card.addView(disable, dp);
        disable.setOnClickListener(v -> {
            AlarmScheduler.cancel(this);
            prefs.edit().putBoolean(AlarmScheduler.ENABLED, false).apply();
            updateStatus();
            Toast.makeText(this, "Alarm turned off", Toast.LENGTH_SHORT).show();
        });

        statusText = text("", 14, Color.rgb(185, 204, 235), false);
        statusText.setPadding(0, dp(22), 0, dp(10));
        page.addView(statusText);

        TextView note = text("VOICE ALARM\nA deep male-sounding voice is selected from the voices available on your phone. The voice repeats until you dismiss the alarm. Actual voice quality depends on your phone's Text-to-Speech engine.", 13, Color.rgb(156, 174, 205), false);
        note.setPadding(0, dp(20), 0, dp(10));
        page.addView(note);

        TextView footer = text("Keep a second alarm as backup while testing.", 12, Color.rgb(120, 140, 174), false);
        footer.setPadding(0, dp(18), 0, 0);
        page.addView(footer);

        setContentView(page);
        clockText.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(new Date()));
    }

    private void enableAlarm() {
        if (Build.VERSION.SDK_INT >= 31) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (am != null && !am.canScheduleExactAlarms()) {
                Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:" + getPackageName()));
                startActivity(i);
                Toast.makeText(this, "Allow alarms, then tap Save again.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        prefs.edit().putBoolean(AlarmScheduler.ENABLED, true)
            .putInt(AlarmScheduler.HOUR, hour).putInt(AlarmScheduler.MINUTE, minute).apply();
        AlarmScheduler.schedule(this, hour, minute);
        updateStatus();
        Toast.makeText(this, "Alarm saved", Toast.LENGTH_SHORT).show();
    }

    private void updateStatus() {
        if (statusText == null) return;
        boolean on = prefs.getBoolean(AlarmScheduler.ENABLED, false);
        statusText.setText(on ? "● ALARM ENABLED  ·  " + formatTime() : "○ ALARM IS OFF");
        statusText.setTextColor(on ? Color.rgb(93, 225, 174) : Color.LTGRAY);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 22);
        }
    }

    private String formatTime() {
        return String.format(Locale.getDefault(), "%02d:%02d %s",
            (hour % 12 == 0 ? 12 : hour % 12), minute, hour < 12 ? "AM" : "PM");
    }

    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label); b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(57, 105, 220)));
        return b;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(-1, -2);
    }
    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density); }
}

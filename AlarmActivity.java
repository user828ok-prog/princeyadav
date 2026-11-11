package com.wakeup.pro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

public class AlarmActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags( android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            | android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            | android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(30, 30, 30, 30);
        root.setBackgroundColor(Color.rgb(10, 17, 35));

        TextView icon = new TextView(this);
        icon.setText("⏰"); icon.setTextSize(64); icon.setGravity(Gravity.CENTER);
        root.addView(icon);

        TextView title = new TextView(this);
        title.setText("WAKE UP. NO EXCUSES.");
        title.setTextSize(27); title.setTextColor(Color.WHITE);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 24, 0, 14);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Your day has started. Stand up and get moving.");
        subtitle.setTextSize(17); subtitle.setTextColor(Color.LTGRAY);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle);

        Button dismiss = new Button(this);
        dismiss.setText("I AM AWAKE · DISMISS ALARM");
        dismiss.setTextSize(16);
        dismiss.setTextColor(Color.WHITE);
        dismiss.setAllCaps(false);
        dismiss.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(220, 65, 85)));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = 50;
        root.addView(dismiss, p);
        dismiss.setOnClickListener(v -> stopAlarm());

        setContentView(root);
    }

    private void stopAlarm() {
        stopService(new Intent(this, AlarmService.class));
        finish();
    }
}

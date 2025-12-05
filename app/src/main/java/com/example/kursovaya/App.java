package com.example.kursovaya;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CH_GENERAL = "reminders";
    public static final String CH_DEADLINE = "deadline";

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = getSystemService(NotificationManager.class);

            NotificationChannel general = new NotificationChannel(
                    CH_GENERAL,
                    "Напоминания",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            nm.createNotificationChannel(general);

            NotificationChannel deadline = new NotificationChannel(
                    CH_DEADLINE,
                    "Сроки",
                    NotificationManager.IMPORTANCE_HIGH
            );
            nm.createNotificationChannel(deadline);
        }
    }
}

package com.example.kursovaya.notif;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.kursovaya.App;
import com.example.kursovaya.MainActivity;
import com.example.kursovaya.R;

public class NotificationUtil {
    public static void show(Context ctx, String channelId, int id, String title, String text){
        Intent i = new Intent(ctx, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                ctx,
                0,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(R.mipmap.ic_start_logo)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setPriority(channelId.equals(App.CH_DEADLINE)
                        ? NotificationCompat.PRIORITY_HIGH
                        : NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(ctx).notify(id, b.build());
    }
}

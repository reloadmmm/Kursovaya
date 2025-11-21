package com.example.kursovaya.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.kursovaya.MainActivity;
import com.example.kursovaya.R;
import com.example.kursovaya.data.AppDatabase;
import com.example.kursovaya.data.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAll(Context context) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        int[] ids = mgr.getAppWidgetIds(new ComponentName(context, GoalWidgetProvider.class));
        for (int id : ids) {
            updateWidget(context, mgr, id);
        }
    }

    private static void updateWidget(Context context,
                                     AppWidgetManager mgr,
                                     int id) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_goals);

        new Thread(() -> {
            List<Task> tasks = AppDatabase.get(context)
                    .taskDao()
                    .getNearestPendingSync();

            String txt = "Нет активных целей";
            if (tasks != null && !tasks.isEmpty()) {
                Task t = tasks.get(0);
                SimpleDateFormat fmt = new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault());
                txt = t.title + " — до " + fmt.format(new Date(t.dueAtMillis));
            }

            views.setTextViewText(R.id.tvWidgetTask, txt);

            Intent openIntent = new Intent(context, MainActivity.class);
            PendingIntent pOpen = PendingIntent.getActivity(
                    context, 0, openIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );
            views.setOnClickPendingIntent(R.id.btnWidgetOpen, pOpen);
            views.setOnClickPendingIntent(R.id.tvWidgetTask, pOpen);

            PendingIntent pAdd = PendingIntent.getActivity(
                    context, 1, openIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );
            views.setOnClickPendingIntent(R.id.btnWidgetAdd, pAdd);

            mgr.updateAppWidget(id, views);
        }).start();
    }
}

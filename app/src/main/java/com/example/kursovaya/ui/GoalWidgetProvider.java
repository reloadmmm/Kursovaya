package com.example.kursovaya.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
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

    @Override
    public void onAppWidgetOptionsChanged(Context context,
                                          AppWidgetManager appWidgetManager,
                                          int appWidgetId,
                                          Bundle newOptions) {
        updateWidget(context, appWidgetManager, appWidgetId);
    }

    public static void updateAll(Context context) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        int[] ids = mgr.getAppWidgetIds(
                new ComponentName(context, GoalWidgetProvider.class)
        );
        for (int id : ids) {
            updateWidget(context, mgr, id);
        }
    }

    private static void updateWidget(Context context,
                                     AppWidgetManager mgr,
                                     int appWidgetId) {

        RemoteViews views = new RemoteViews(
                context.getPackageName(),
                R.layout.widget_goals
        );

        boolean isNight = (context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;

        views.setInt(R.id.widgetRoot, "setBackgroundResource",
                isNight ? R.drawable.widget_bg_dark_rounded
                        : R.drawable.widget_bg_white_rounded);

        views.setTextViewText(R.id.tvWidgetTitle, "Ближайшая цель");
        views.setViewVisibility(R.id.pbWidgetLoading, View.VISIBLE);
        views.setViewVisibility(R.id.pbWidgetProgress, View.GONE);
        views.setTextViewText(R.id.tvWidgetTask1, "Загрузка...");
        views.setViewVisibility(R.id.tvWidgetTask2, View.GONE);
        views.setViewVisibility(R.id.tvWidgetTask3, View.GONE);

        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent pOpen = PendingIntent.getActivity(
                context,
                0,
                openIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent addIntent = new Intent(context, QuickAddActivity.class);
        PendingIntent pAdd = PendingIntent.getActivity(
                context,
                1,
                addIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        views.setOnClickPendingIntent(R.id.tvWidgetTask1, pOpen);
        views.setOnClickPendingIntent(R.id.tvWidgetTask2, pOpen);
        views.setOnClickPendingIntent(R.id.tvWidgetTask3, pOpen);
        views.setOnClickPendingIntent(R.id.btnWidgetOpen, pOpen);
        views.setOnClickPendingIntent(R.id.btnWidgetAdd, pAdd);

        mgr.updateAppWidget(appWidgetId, views);

        new Thread(() -> {
            List<Task> tasks = AppDatabase.get(context)
                    .taskDao()
                    .getNearestPendingSync();

            Bundle opts = mgr.getAppWidgetOptions(appWidgetId);
            int minHeight = opts != null
                    ? opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
                    : 0;
            boolean compact = minHeight < 110;

            views.setViewVisibility(R.id.pbWidgetLoading, View.GONE);

            if (tasks == null || tasks.isEmpty()) {
                views.setTextViewText(R.id.tvWidgetTask1, "Нет активных целей");
                views.setViewVisibility(R.id.tvWidgetTask2, View.GONE);
                views.setViewVisibility(R.id.tvWidgetTask3, View.GONE);
                views.setViewVisibility(R.id.pbWidgetProgress, View.GONE);
            } else {
                SimpleDateFormat fmt =
                        new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault());

                Task first = tasks.get(0);
                String line1 = first.title + " — до " +
                        fmt.format(new Date(first.dueAtMillis));
                views.setTextViewText(R.id.tvWidgetTask1, line1);

                long now = System.currentTimeMillis();
                long total = first.dueAtMillis - first.createdAt;
                long done = now - first.createdAt;
                int progress = 0;
                if (total > 0) {
                    long val = done * 100 / total;
                    if (val < 0) val = 0;
                    if (val > 100) val = 100;
                    progress = (int) val;
                }
                views.setViewVisibility(R.id.pbWidgetProgress, View.VISIBLE);
                views.setProgressBar(R.id.pbWidgetProgress, 100, progress, false);

                if (!compact && tasks.size() > 1) {
                    Task t2 = tasks.get(1);
                    views.setViewVisibility(R.id.tvWidgetTask2, View.VISIBLE);
                    views.setTextViewText(R.id.tvWidgetTask2,
                            "• " + t2.title);
                } else {
                    views.setViewVisibility(R.id.tvWidgetTask2, View.GONE);
                }

                if (!compact && tasks.size() > 2) {
                    Task t3 = tasks.get(2);
                    views.setViewVisibility(R.id.tvWidgetTask3, View.VISIBLE);
                    views.setTextViewText(R.id.tvWidgetTask3,
                            "• " + t3.title);
                } else {
                    views.setViewVisibility(R.id.tvWidgetTask3, View.GONE);
                }
            }

            mgr.updateAppWidget(appWidgetId, views);
        }).start();
    }
}

package com.example.kursovaya.notif;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.kursovaya.data.Task;

import java.util.concurrent.TimeUnit;

public class NotifScheduler {
    private static final String TAG_PREFIX = "task_";

    public static void scheduleForTask(Context ctx, Task t) {
        WorkManager wm = WorkManager.getInstance(ctx);
        String tag = TAG_PREFIX + t.createdAt;

        if (t.notifyEnabled && t.notifyEveryMin > 0) {
            int mins = Math.max(1, t.notifyEveryMin);
            long delayMs = TimeUnit.MINUTES.toMillis(mins);

            Data data = new Data.Builder()
                    .putString("title", t.title)
                    .putLong("createdAt", t.createdAt)
                    .build();

            OneTimeWorkRequest reminderOnce =
                    new OneTimeWorkRequest.Builder(ReminderWorker.class)
                            .setInitialDelay(Math.max(0, delayMs), TimeUnit.MILLISECONDS)
                            .setInputData(data)
                            .addTag(tag)
                            .build();

            wm.enqueueUniqueWork(
                    tag + "_reminder_once",
                    ExistingWorkPolicy.REPLACE,
                    reminderOnce
            );
        }

        long now = System.currentTimeMillis();
        long untilDue = Math.max(0, t.dueAtMillis - now);

        int[] minsOffsets = new int[]{20, 15, 10, 5};
        for (int m : minsOffsets) {
            long delay = t.dueAtMillis - now - TimeUnit.MINUTES.toMillis(m);
            if (delay > 0) {
                Data d = new Data.Builder()
                        .putString("title", t.title)
                        .putLong("createdAt", t.createdAt)
                        .putInt("offsetMin", m)
                        .build();

                OneTimeWorkRequest req =
                        new OneTimeWorkRequest.Builder(DeadlinePingWorker.class)
                                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                .setInputData(d)
                                .addTag(tag)
                                .build();

                wm.enqueue(req);
            }
        }

        long delayRelapse = untilDue + TimeUnit.HOURS.toMillis(12);

        Data d = new Data.Builder()
                .putLong("createdAt", t.createdAt)
                .putString("title", t.title)
                .build();

        OneTimeWorkRequest autoRelapse =
                new OneTimeWorkRequest.Builder(AutoRelapseWorker.class)
                        .setInitialDelay(Math.max(0, delayRelapse), TimeUnit.MILLISECONDS)
                        .setInputData(d)
                        .addTag(tag)
                        .build();

        wm.enqueue(autoRelapse);
    }

    public static void cancelForTask(Context ctx, long createdAt) {
        WorkManager wm = WorkManager.getInstance(ctx);
        String tag = TAG_PREFIX + createdAt;

        wm.cancelAllWorkByTag(tag);
        wm.cancelUniqueWork(tag + "_reminder_once");
    }
}

package com.example.kursovaya.notif;

import android.content.Context;
import androidx.work.*;
import com.example.kursovaya.data.Task;

import java.util.concurrent.TimeUnit;

public class NotifScheduler {
    private static final String TAG_PREFIX = "task_"; // + createdAt

    public static void scheduleForTask(Context ctx, Task t){
        WorkManager wm = WorkManager.getInstance(ctx);
        String tag = TAG_PREFIX + t.createdAt;

        if (t.notifyEnabled && t.notifyEveryMin > 0){
            Data data = new Data.Builder()
                    .putString("title", t.title)
                    .putLong("createdAt", t.createdAt)
                    .build();
            PeriodicWorkRequest periodic = new PeriodicWorkRequest.Builder(
                    ReminderWorker.class, t.notifyEveryMin, TimeUnit.MINUTES)
                    .setInputData(data)
                    .addTag(tag)
                    .build();
            wm.enqueueUniquePeriodicWork(tag + "_periodic",
                    ExistingPeriodicWorkPolicy.UPDATE, periodic);
        }

        long now = System.currentTimeMillis();
        long untilDue = Math.max(0, t.dueAtMillis - now);

        int[] mins = new int[]{20,15,10,5};
        for (int m : mins){
            long delay = t.dueAtMillis - now - TimeUnit.MINUTES.toMillis(m);
            if (delay > 0){
                Data d = new Data.Builder()
                        .putString("title", t.title)
                        .putLong("createdAt", t.createdAt)
                        .putInt("offsetMin", m)
                        .build();
                OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(DeadlinePingWorker.class)
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
        OneTimeWorkRequest autoRelapse = new OneTimeWorkRequest.Builder(AutoRelapseWorker.class)
                .setInitialDelay(Math.max(0, delayRelapse), TimeUnit.MILLISECONDS)
                .setInputData(d)
                .addTag(tag)
                .build();
        wm.enqueue(autoRelapse);
    }

    public static void cancelForTask(Context ctx, long createdAt){
        WorkManager.getInstance(ctx).cancelAllWorkByTag(TAG_PREFIX + createdAt);
        WorkManager.getInstance(ctx).cancelUniqueWork(TAG_PREFIX + createdAt + "_periodic");
    }
}

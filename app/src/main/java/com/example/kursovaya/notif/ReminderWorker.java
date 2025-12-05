package com.example.kursovaya.notif;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.kursovaya.App;
import com.example.kursovaya.data.AppDatabase;
import com.example.kursovaya.data.Task;

public class ReminderWorker extends Worker {

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        long createdAt = getInputData().getLong("createdAt", -1);

        Task t = AppDatabase
                .get(getApplicationContext())
                .taskDao()
                .findByCreatedAt(createdAt);

        if (t == null || !"pending".equals(t.status)) {
            return Result.success();
        }

        NotificationUtil.show(
                getApplicationContext(),
                App.CH_GENERAL,
                (int) (createdAt % Integer.MAX_VALUE),
                "Напоминание о цели",
                t.title
        );

        return Result.success();
    }
}

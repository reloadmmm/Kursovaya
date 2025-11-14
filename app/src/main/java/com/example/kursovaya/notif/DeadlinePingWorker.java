package com.example.kursovaya.notif;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.kursovaya.App;
import com.example.kursovaya.data.AppDatabase;
import com.example.kursovaya.data.Task;

public class DeadlinePingWorker extends Worker {
    public DeadlinePingWorker(@NonNull Context context, @NonNull WorkerParameters params){ super(context, params); }

    @NonNull @Override
    public Result doWork(){
        long createdAt = getInputData().getLong("createdAt", -1);
        int offset = getInputData().getInt("offsetMin", 0);
        Task t = AppDatabase.get(getApplicationContext()).taskDao().findByCreatedAt(createdAt);
        if (t == null || !"pending".equals(t.status)) return Result.success();
        NotificationUtil.show(getApplicationContext(), App.CH_DEADLINE,
                (int)((createdAt + offset) % Integer.MAX_VALUE),
                "Уточните статус цели", t.title + " — до срока " + offset + " мин");
        return Result.success();
    }
}

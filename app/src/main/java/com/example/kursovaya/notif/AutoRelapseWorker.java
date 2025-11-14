package com.example.kursovaya.notif;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.kursovaya.App;
import com.example.kursovaya.data.AppDatabase;
import com.example.kursovaya.data.Task;

public class AutoRelapseWorker extends Worker {
    public AutoRelapseWorker(@NonNull Context context, @NonNull WorkerParameters params){ super(context, params); }

    @NonNull @Override
    public Result doWork(){
        long createdAt = getInputData().getLong("createdAt", -1);
        Task t = AppDatabase.get(getApplicationContext()).taskDao().findByCreatedAt(createdAt);
        if (t == null) return Result.success();

        if ("pending".equals(t.status)) {
            AppDatabase.get(getApplicationContext()).taskDao()
                    .updateStatusByCreatedAt(createdAt, "relapse");
            NotificationUtil.show(getApplicationContext(), App.CH_DEADLINE,
                    (int)((createdAt + 999) % Integer.MAX_VALUE),
                    "Цель перенесена в «Рецидив»", t.title);
        }
        NotifScheduler.cancelForTask(getApplicationContext(), createdAt);
        return Result.success();
    }
}

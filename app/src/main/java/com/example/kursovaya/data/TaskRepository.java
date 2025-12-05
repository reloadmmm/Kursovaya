package com.example.kursovaya.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.kursovaya.ui.GoalWidgetProvider;

import java.util.List;
import java.util.concurrent.Executors;

public class TaskRepository {
    private final TaskDao dao;
    private final Context appCtx;

    public TaskRepository(Context ctx) {
        this.appCtx = ctx.getApplicationContext();
        this.dao = AppDatabase.get(appCtx).taskDao();
    }

    public LiveData<List<Task>> pending() { return dao.observePending(); }
    public LiveData<List<Task>> done()    { return dao.observeDone(); }
    public LiveData<List<Task>> relapse() { return dao.observeRelapse(); }

    public void insert(Task t){
        Executors.newSingleThreadExecutor().execute(() -> {
            dao.insert(t);
            GoalWidgetProvider.updateAll(appCtx);
        });
    }

    public void update(Task t){
        Executors.newSingleThreadExecutor().execute(() -> {
            dao.update(t);
            GoalWidgetProvider.updateAll(appCtx);
        });
    }
}

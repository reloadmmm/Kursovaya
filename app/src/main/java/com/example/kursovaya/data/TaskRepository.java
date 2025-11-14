package com.example.kursovaya.data;

import android.content.Context;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.Executors;

public class TaskRepository {
    private final TaskDao dao;

    public TaskRepository(Context ctx) {
        dao = AppDatabase.get(ctx).taskDao();
    }

    public LiveData<List<Task>> pending() { return dao.observePending(); }
    public LiveData<List<Task>> done() { return dao.observeDone(); }
    public LiveData<List<Task>> relapse() { return dao.observeRelapse(); }

    public void insert(Task t){ Executors.newSingleThreadExecutor().execute(() -> dao.insert(t)); }
    public void update(Task t){ Executors.newSingleThreadExecutor().execute(() -> dao.update(t)); }
}

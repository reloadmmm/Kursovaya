package com.example.kursovaya.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.example.kursovaya.data.*;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository repo;
    public final LiveData<List<Task>> pending, done, relapse;
    public final LiveData<Integer> total, doneCount;

    public TaskViewModel(@NonNull Application app) {
        super(app);
        repo = new TaskRepository(app);
        pending = repo.pending();
        done = repo.done();
        relapse = repo.relapse();
        total = AppDatabase.get(app).taskDao().observeTotal();
        doneCount = AppDatabase.get(app).taskDao().observeDoneCount();
    }

    public void add(Task t){ repo.insert(t); }
    public void markDone(Task t){ t.status = "done"; repo.update(t); }
    public void markRelapse(Task t){ t.status = "relapse"; repo.update(t); }
}

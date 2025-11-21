package com.example.kursovaya.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.kursovaya.data.AppDatabase;
import com.example.kursovaya.data.DiaryDao;
import com.example.kursovaya.data.DiaryEntry;

import java.util.List;

public class DiaryViewModel extends AndroidViewModel {

    private final DiaryDao dao;
    private final LiveData<List<DiaryEntry>> entries;

    public DiaryViewModel(@NonNull Application application) {
        super(application);
        dao = AppDatabase.get(application).diaryDao();
        entries = dao.observeAll();
    }

    public LiveData<List<DiaryEntry>> getEntries() {
        return entries;
    }

    public void add(String text, String audioPath) {
        long now = System.currentTimeMillis();
        DiaryEntry e = new DiaryEntry(text, now, audioPath);
        AppDatabase.databaseWriteExecutor.execute(() -> dao.insert(e));
    }

    public void add(String text) {
        add(text, null);
    }

    public void delete(DiaryEntry e) {
        AppDatabase.databaseWriteExecutor.execute(() -> dao.delete(e));
    }
}

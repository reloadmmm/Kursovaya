package com.example.kursovaya.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.kursovaya.data.AppDatabase;
import com.example.kursovaya.data.DiaryDao;
import com.example.kursovaya.data.DiaryEntry;

import java.util.List;
import java.util.concurrent.Executors;

public class DiaryViewModel extends AndroidViewModel {

    private final DiaryDao dao;
    public final LiveData<List<DiaryEntry>> entries;

    public DiaryViewModel(@NonNull Application app) {
        super(app);
        dao = AppDatabase.get(app).diaryDao();
        entries = dao.observeAll();
    }

    public void add(String text) {
        Executors.newSingleThreadExecutor().execute(
                () -> dao.insert(new DiaryEntry(System.currentTimeMillis(), text))
        );
    }

    public void delete(DiaryEntry e) {
        Executors.newSingleThreadExecutor().execute(
                () -> dao.delete(e)
        );
    }
}

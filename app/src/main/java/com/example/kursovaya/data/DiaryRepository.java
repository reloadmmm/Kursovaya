package com.example.kursovaya.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executors;

public class DiaryRepository {

    private final DiaryDao dao;

    public DiaryRepository(Context ctx) {
        dao = AppDatabase.get(ctx).diaryDao();
    }

    public LiveData<List<DiaryEntry>> getAll() {
        return dao.getAll();
    }

    public void add(String text) {
        Executors.newSingleThreadExecutor().execute(() ->
                dao.insert(new DiaryEntry(text, System.currentTimeMillis()))
        );
    }

    public void delete(DiaryEntry entry) {
        Executors.newSingleThreadExecutor().execute(() ->
                dao.delete(entry)
        );
    }
}

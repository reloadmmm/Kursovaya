package com.example.kursovaya.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class DiaryRepository {

    private final DiaryDao dao;

    public DiaryRepository(Context context) {
        dao = AppDatabase.get(context).diaryDao();
    }

    public LiveData<List<DiaryEntry>> getAll() {
        return dao.observeAll();
    }

    public void addText(String text) {
        long now = System.currentTimeMillis();
        DiaryEntry e = new DiaryEntry(text, now, null);
        AppDatabase.databaseWriteExecutor.execute(() -> dao.insert(e));
    }

    public void addWithAudio(String text, String audioPath) {
        long now = System.currentTimeMillis();
        DiaryEntry e = new DiaryEntry(text, now, audioPath);
        AppDatabase.databaseWriteExecutor.execute(() -> dao.insert(e));
    }

    public void delete(DiaryEntry entry) {
        AppDatabase.databaseWriteExecutor.execute(() -> dao.delete(entry));
    }
}

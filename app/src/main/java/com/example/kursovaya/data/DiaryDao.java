package com.example.kursovaya.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface DiaryDao {

    @Query("SELECT * FROM diary ORDER BY createdAt DESC")
    LiveData<List<DiaryEntry>> getAll();

    @Insert
    void insert(DiaryEntry entry);

    @Delete
    void delete(DiaryEntry entry);
}

package com.example.kursovaya.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DiaryDao {

    @Query("SELECT * FROM DiaryEntry ORDER BY createdAtMillis DESC")
    LiveData<List<DiaryEntry>> observeAll();

    @Insert
    long insert(DiaryEntry e);

    @Delete
    void delete(DiaryEntry e);
}

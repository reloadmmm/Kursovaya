package com.example.kursovaya.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface HabitDao {

    @Insert
    long insert(Habit habit);

    @Update
    void update(Habit habit);

    @Delete
    void delete(Habit habit);

    @Query("SELECT * FROM habits ORDER BY id DESC")
    LiveData<List<Habit>> observe();
}

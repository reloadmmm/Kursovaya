package com.example.kursovaya.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface GoalEventDao {

    @Insert
    long insert(GoalEvent event);

    @Delete
    void delete(GoalEvent event);

    @Query("SELECT * FROM goal_events WHERE date BETWEEN :start AND :end")
    LiveData<List<GoalEvent>> observeRange(long start, long end);
}

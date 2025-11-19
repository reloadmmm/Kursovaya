package com.example.kursovaya.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "goal_events")
public class GoalEvent {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public long date;
    public String title;

    public GoalEvent(long date, String title) {
        this.date = date;
        this.title = title;
    }
}

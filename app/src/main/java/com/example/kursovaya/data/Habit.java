package com.example.kursovaya.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habits")
public class Habit {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public int streak;
    public long lastUpdate;
    public boolean active;

    public Habit(String title, int streak, long lastUpdate, boolean active) {
        this.title = title;
        this.streak = streak;
        this.lastUpdate = lastUpdate;
        this.active = active;
    }
}

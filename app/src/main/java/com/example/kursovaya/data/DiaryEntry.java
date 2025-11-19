package com.example.kursovaya.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diary")
public class DiaryEntry {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String text;
    public long createdAt;

    public DiaryEntry(String text, long createdAt) {
        this.text = text;
        this.createdAt = createdAt;
    }
}

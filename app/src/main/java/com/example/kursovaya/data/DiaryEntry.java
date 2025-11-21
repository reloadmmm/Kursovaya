package com.example.kursovaya.data;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DiaryEntry {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long createdAtMillis;

    public String text;

    @Nullable
    public String audioPath;

    public DiaryEntry(String text, long createdAtMillis, @Nullable String audioPath) {
        this.text = text;
        this.createdAtMillis = createdAtMillis;
        this.audioPath = audioPath;
    }
}

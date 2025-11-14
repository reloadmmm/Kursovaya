package com.example.kursovaya.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true) public long id;
    public String title;
    public long dueAtMillis;
    public boolean rewardEnabled;
    public String rewardText;
    public boolean notifyEnabled;
    public int notifyEveryMin;
    public String status;
    public long createdAt;

    public Task(String title, long dueAtMillis, boolean rewardEnabled, String rewardText,
                boolean notifyEnabled, int notifyEveryMin, String status, long createdAt) {
        this.title = title;
        this.dueAtMillis = dueAtMillis;
        this.rewardEnabled = rewardEnabled;
        this.rewardText = rewardText;
        this.notifyEnabled = notifyEnabled;
        this.notifyEveryMin = notifyEveryMin;
        this.status = status;
        this.createdAt = createdAt;
    }
}

package com.example.kursovaya.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insert(Task t);

    @Update
    void update(Task t);

    @Delete
    void delete(Task t);

    @Query("SELECT * FROM tasks WHERE status='pending' ORDER BY dueAtMillis ASC")
    LiveData<List<Task>> observePending();

    @Query("SELECT * FROM tasks WHERE status='done' ORDER BY dueAtMillis DESC")
    LiveData<List<Task>> observeDone();

    @Query("SELECT * FROM tasks WHERE status='relapse' ORDER BY dueAtMillis DESC")
    LiveData<List<Task>> observeRelapse();

    @Query("SELECT COUNT(*) FROM tasks")
    LiveData<Integer> observeTotal();

    @Query("SELECT COUNT(*) FROM tasks WHERE status='done'")
    LiveData<Integer> observeDoneCount();

    @Query("SELECT * FROM tasks WHERE createdAt = :createdAt LIMIT 1")
    Task findByCreatedAt(long createdAt);

    @Query("UPDATE tasks SET status = :newStatus WHERE createdAt = :createdAt")
    void updateStatusByCreatedAt(long createdAt, String newStatus);

    @Query("SELECT * FROM tasks WHERE dueAtMillis BETWEEN :from AND :to ORDER BY dueAtMillis")
    LiveData<List<Task>> tasksForDay(long from, long to);

    @Query("SELECT * FROM tasks WHERE status = 'pending' ORDER BY dueAtMillis LIMIT 3")
    List<Task> getNearestPendingSync();


}

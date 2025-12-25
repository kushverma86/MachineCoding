package com.todoapp.repository;

import com.todoapp.model.ActivityLogEntry;
import com.todoapp.model.Task;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    // Task Operations
    Task saveTask(Task task);
    Optional<Task> findTaskByIdAndUserId(Long taskId, Long userId);
    void deleteTask(Long taskId, Long userId);
    List<Task> findAllTasksByUserId(Long userId);

    Optional<Task> findTaskByTaskId(Long taskId);

    // Activity Log
    void logActivity(ActivityLogEntry entry);
    List<ActivityLogEntry> getLogEntriesByUserId(Long userId);

    // Utility for ID generation (Counter, UUID etc)
    Long generateTaskId();
}
package com.todoapp.service;

import com.todoapp.model.ActivityLogEntry;
import com.todoapp.model.Task;
import com.todoapp.model.TaskStatistics;
import com.todoapp.util.TaskFilter;
import com.todoapp.util.TaskSortCriteria;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoService {
    // CRUD
    Task addTask(Task task, Long userId);
    Task getTask(Long taskId, Long userId);
    Task getTask(Long taskId);
    Task modifyTask(Task task, Long userId);
    void removeTask(Long taskId, Long userId);
    void completeTask(Long taskId, Long userId);

    List<Task> listTasks(Long userId, TaskFilter filter, TaskSortCriteria sortCriteria);

    TaskStatistics getStatistics(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    List<ActivityLogEntry> getActivityLog(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
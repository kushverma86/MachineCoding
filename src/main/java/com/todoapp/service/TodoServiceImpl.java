package com.todoapp.service;

import com.todoapp.exception.TaskNotFoundException;
import com.todoapp.model.*;
import com.todoapp.repository.TodoRepository;
import com.todoapp.util.FilterStrategy;
import com.todoapp.util.SortStrategy;
import com.todoapp.util.TaskFilter;
import com.todoapp.util.TaskSortCriteria;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository repository;

    @Override
    public Task addTask(Task task, Long userId) {
        if (task.getId() == null) {
            task.setId(repository.generateTaskId());
        }
        task.setUserId(userId);
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        Task savedTask = repository.saveTask(task);

        // Log activity
        repository.logActivity(ActivityLogEntry.builder()
                .userId(userId)
                .taskId(savedTask.getId())
                .type(ActivityType.ADDITION)
                .timestamp(LocalDateTime.now())
                .description("Task added: " + savedTask.getTitle())
                .build());

        return savedTask;
    }

    @Override
    public Task getTask(Long taskId, Long userId) {
        return repository.findTaskByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));
    }

    @Override
    public Task getTask(@NonNull Long taskId){
        return repository.findTaskByTaskId(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));
    }

    @Override
    public Task modifyTask(Task task, Long userId) {
        Task existingTask = getTask(task.getId(), userId);
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDeadline(task.getDeadline());
        existingTask.setPlannedStartDate(task.getPlannedStartDate());
        existingTask.setTags(task.getTags());
        existingTask.setUpdatedAt(LocalDateTime.now());

        Task modifiedTask = repository.saveTask(existingTask);

        // Log activity
        repository.logActivity(ActivityLogEntry.builder()
                .userId(userId)
                .taskId(modifiedTask.getId())
                .type(ActivityType.MODIFICATION)
                .timestamp(LocalDateTime.now())
                .description("Task modified: " + modifiedTask.getTitle())
                .build());

        return modifiedTask;
    }

    @Override
    public void removeTask(Long taskId, Long userId) {
        Task taskToRemove = getTask(taskId, userId);
        repository.deleteTask(taskId, userId);
        repository.logActivity(ActivityLogEntry.builder()
                .userId(userId)
                .taskId(taskId)
                .type(ActivityType.REMOVAL)
                .timestamp(LocalDateTime.now())
                .description("Task removed: " + taskToRemove.getTitle())
                .build());
    }

    @Override
    public void completeTask(Long taskId, Long userId) {
        Task taskToComplete = getTask(taskId, userId);
        taskToComplete.setStatus(TaskStatus.COMPLETED);

        // Completed task is removed and logged
        repository.deleteTask(taskId, userId);

        repository.logActivity(ActivityLogEntry.builder()
                .userId(userId)
                .taskId(taskId)
                .type(ActivityType.COMPLETION)
                .timestamp(LocalDateTime.now())
                .description("Task completed and removed: " + taskToComplete.getTitle())
                .build());
    }

    @Override
    public List<Task> listTasks(Long userId, TaskFilter filter, TaskSortCriteria sortCriteria) {
        List<Task> allTasks = repository.findAllTasksByUserId(userId);


        // Marking SpilledOver Tasks
        allTasks.forEach(task -> {
            if (task.getStatus() == TaskStatus.PENDING &&
                    task.getDeadline() != null &&
                    task.getDeadline().isBefore(LocalDate.now())) {

                task.setStatus(TaskStatus.SPILLOVER);
                repository.saveTask(task); // Persist status change

                repository.logActivity(ActivityLogEntry.builder()
                        .userId(task.getUserId())
                        .taskId(task.getId())
                        .type(ActivityType.SPILLOVER)
                        .timestamp(LocalDateTime.now())
                        .description("Task has been SPILLED OVER : " + task.getTitle())
                        .build());
                // Note: No activity log for automated spillover for simplicity
            }
        });


        FilterStrategy filterStrategy = filter.getStrategy();
        List<Task> filteredTasks = allTasks.stream()
                .filter(filterStrategy::test)
                .collect(Collectors.toList());

        SortStrategy sortStrategy = sortCriteria.getStrategy();
        filteredTasks.sort(sortStrategy.getComparator());

        return filteredTasks;
    }

    @Override
    public TaskStatistics getStatistics(Long userId, LocalDateTime startTime, LocalDateTime endTime) {

        List<ActivityLogEntry> relevantLogs = getActivityLog(userId, startTime, endTime);

        long added = relevantLogs.stream()
                .filter(log -> log.getType() == ActivityType.ADDITION)
                .count();

        long completed = relevantLogs.stream()
                .filter(log -> log.getType() == ActivityType.COMPLETION)
                .count();

        long spillover = relevantLogs.stream()
                .filter(log -> log.getType().equals(ActivityType.SPILLOVER))
                .count();

        // Spillover is calculated by checking the status of tasks currently in the system
        // that were modified/created in the time period, OR by tracking a specific SPILLOVER_LOG type.
        // For simplicity and based on available log types (ADDITION, MODIFICATION, COMPLETION, REMOVAL):
        // We'll approximate spillover by counting tasks that were *modified* to SPILLOVER status
        // and were in the list during the time period.

        // A more robust way (which we'll use here) is to check all tasks for spillover status
        // and see if their latest update/creation time falls within the period.
        long spilledOver = repository.findAllTasksByUserId(userId).stream()
                .filter(task -> task.getStatus() == TaskStatus.SPILLOVER)
                .filter(task -> !task.getCreatedAt().isBefore(startTime) && !task.getCreatedAt().isAfter(endTime))
                .count();


        return TaskStatistics.builder()
                .startTime(startTime)
                .endTime(endTime)
                .totalAdded(added)
                .totalCompleted(completed)
                .totalSpilledOver(spilledOver)
                .build();
    }

    @Override
    public List<ActivityLogEntry> getActivityLog(Long userId, LocalDateTime startTime, LocalDateTime endTime) {

        List<ActivityLogEntry> allLogs = repository.getLogEntriesByUserId(userId);

        final boolean isTimePeriodSupplied = startTime != null && endTime != null;

        if (!isTimePeriodSupplied) {
            return allLogs;
        }
        // Filter based on the provided time period [OPTIONAL]
        return allLogs.stream()
                .filter(log -> !log.getTimestamp().isBefore(startTime) && !log.getTimestamp().isAfter(endTime))
                .collect(Collectors.toList());
    }
}

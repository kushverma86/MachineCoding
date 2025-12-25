package com.todoapp.repository;

import com.todoapp.model.ActivityLogEntry;
import com.todoapp.model.Task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTodoRepository implements TodoRepository {

    private final Map<Long, Map<Long, Task>> taskStore = new ConcurrentHashMap<>();
    private final Map<Long, List<ActivityLogEntry>> activityLogStore = new ConcurrentHashMap<>();
    private final AtomicLong taskIdCounter = new AtomicLong(0);

    private static final InMemoryTodoRepository INSTANCE = new InMemoryTodoRepository();

    private InMemoryTodoRepository() {}

    public static InMemoryTodoRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Long generateTaskId() {
        return taskIdCounter.incrementAndGet();
    }

    @Override
    public Task saveTask(Task task) {
        taskStore.computeIfAbsent(task.getUserId(), k -> new ConcurrentHashMap<>())
                .put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findTaskByIdAndUserId(Long taskId, Long userId) {
        return Optional.ofNullable(taskStore.getOrDefault(userId, Collections.emptyMap()).get(taskId));
    }

    @Override
    public Optional<Task> findTaskByTaskId(Long taskId){
        return taskStore.values().stream()
                .map(tasks -> tasks.get(taskId))
                .filter(task -> Objects.nonNull(task.getId()))
                .findFirst();

    }


    @Override
    public void deleteTask(Long taskId, Long userId) {
        if (taskStore.containsKey(userId)) {
            taskStore.get(userId).remove(taskId);
        }
    }

    @Override
    public List<Task> findAllTasksByUserId(Long userId) {
        return new ArrayList<>(taskStore.getOrDefault(userId, Collections.emptyMap()).values());
    }

    @Override
    public void logActivity(ActivityLogEntry entry) {
        activityLogStore.computeIfAbsent(entry.getUserId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(entry);
        // Sort by timestamp (newest first)
        activityLogStore.get(entry.getUserId()).sort(Comparator.comparing(ActivityLogEntry::getTimestamp).reversed());
    }

    @Override
    public List<ActivityLogEntry> getLogEntriesByUserId(Long userId) {
        return activityLogStore.getOrDefault(userId, Collections.emptyList());
    }
}

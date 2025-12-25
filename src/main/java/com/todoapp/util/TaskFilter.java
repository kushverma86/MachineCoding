package com.todoapp.util;

import com.todoapp.model.Task;
import com.todoapp.model.TaskStatus;

import java.time.LocalDate;
import java.util.function.Predicate;

public enum TaskFilter {
    ALL(task -> true),
    PENDING(task -> task.getStatus() == TaskStatus.PENDING),
    SPILLOVER(task -> task.getStatus() == TaskStatus.SPILLOVER),
    FUTURE(task -> task.getPlannedStartDate() != null && task.getPlannedStartDate().isAfter(LocalDate.now())),
    HAS_DEADLINE(task -> task.getDeadline() != null);

    private final Predicate<Task> predicate;

    TaskFilter(Predicate<Task> predicate) {
        this.predicate = predicate;
    }

    public FilterStrategy getStrategy() {
        return new FilterStrategy() {
            @Override
            public Predicate<Task> getPredicate() {
                return predicate;
            }
        };
    }
}

package com.todoapp.util;

import com.todoapp.model.Task;

import java.util.Comparator;

public enum TaskSortCriteria {
    BY_CREATED_DATE_ASC(Comparator.comparing(Task::getCreatedAt)),
    BY_DEADLINE_ASC(Comparator.comparing(Task::getDeadline, Comparator.nullsLast(Comparator.naturalOrder()))),
    BY_TITLE_ASC(Comparator.comparing(Task::getTitle)),
    BY_TAGS_COUNT_DESC(Comparator.comparing((Task task) -> task.getTags().size()).reversed());

    private final Comparator<Task> comparator;

    TaskSortCriteria(Comparator<Task> comparator) {
        this.comparator = comparator;
    }

    public SortStrategy getStrategy() {
        return new SortStrategy() {
            @Override
            public Comparator<Task> getComparator() {
                return comparator;
            }
        };
    }
}

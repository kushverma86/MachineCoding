package com.todoapp.util;

import com.todoapp.model.Task;

import java.util.function.Predicate;

/**
 * Strategy interface for defining how a list of Tasks should be filtered.
 */
@FunctionalInterface
public interface FilterStrategy {

    Predicate<Task> getPredicate();

    default boolean test(Task task) {
        return getPredicate().test(task);
    }
}

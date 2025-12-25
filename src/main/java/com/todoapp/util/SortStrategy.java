package com.todoapp.util;

import com.todoapp.model.Task;

import java.util.Comparator;

public interface SortStrategy {

    Comparator<Task> getComparator();

}
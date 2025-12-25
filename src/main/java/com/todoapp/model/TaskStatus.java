package com.todoapp.model;

public enum TaskStatus {
    PENDING,
    COMPLETED,
    SPILLOVER // Custom status for tasks past deadline and not completed
}
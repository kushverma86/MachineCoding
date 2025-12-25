package com.todoapp.exception;

/**
 * Custom exception thrown when a requested Task ID does not exist
 * or does not belong to the requesting user.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String message) {
        super(message);
    }
}
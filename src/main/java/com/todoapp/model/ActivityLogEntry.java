package com.todoapp.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Users should be able to see an activity log that describes additions, modifications,
 * completions and removals of tasks from the TODO list during a particular time period.
 */
@Data
@Builder
public class ActivityLogEntry {
    private LocalDateTime timestamp;
    private Long taskId;
    private Long userId;
    private ActivityType type;
    private String description;
}

package com.todoapp.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Users should also be able to see statistics around how many tasks were added, completed,
 * and spilled over the deadline during a particular time period.
 */
@Data
@Builder
public class TaskStatistics {
    // Defines the time window for which statistics were calculated
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Statistics metrics
    private long totalAdded;
    private long totalCompleted;
    private long totalSpilledOver; // Tasks past their deadline and not completed

    // Additional metrics could be added, e.g., total pending, total modified, etc.
}

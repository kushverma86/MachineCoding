package com.todoapp.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Task {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate deadline; // Optional deadline
    private LocalDate plannedStartDate; // For future tasks
    private Set<String> tags = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
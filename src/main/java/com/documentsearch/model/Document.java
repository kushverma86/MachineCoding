package com.documentsearch.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Document {
    private final String id;
    private final String content;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Document(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public void updateContent(String newContent) {
        // In a real system, you might need a lock here
        this.updatedAt = LocalDateTime.now();
        // Assuming we're not actually changing the content for this quick implementation
    }
}
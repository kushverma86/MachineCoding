package com.documentsearch.strategy;

import com.documentsearch.model.Document;

import java.util.Comparator;

public class UpdatedOrderingStrategy implements OrderingStrategy {
    // Sorts by last update date, most recent first (descending).
    @Override
    public Comparator<Document> getComparator() {
        return Comparator.comparing(Document::getUpdatedAt).reversed();
    }
}

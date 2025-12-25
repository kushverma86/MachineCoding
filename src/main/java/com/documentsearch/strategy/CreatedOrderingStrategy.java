package com.documentsearch.strategy;

import com.documentsearch.model.Document;

import java.util.Comparator;

public class CreatedOrderingStrategy implements OrderingStrategy {
    @Override
    public Comparator<Document> getComparator() {
        return Comparator.comparing(Document::getCreatedAt).reversed();
    }
}
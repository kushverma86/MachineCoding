package com.documentsearch.strategy;

import com.documentsearch.model.Document;

import java.util.Comparator;

public interface OrderingStrategy {
    /**
     * Provides a comparator for sorting Documents.
     * @return A Comparator<Document>.
     */
    Comparator<Document> getComparator();
}

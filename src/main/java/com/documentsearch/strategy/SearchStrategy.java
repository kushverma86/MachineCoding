package com.documentsearch.strategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SearchStrategy {
    /**
     * Executes the search and returns a set of document IDs matching the query.
     * @param query The search pattern.
     * @param index The inverted index for the category.
     * @return A set of matching document IDs.
     */
    Set<String> search(String query, Map<String, Map<String, Integer>> index);
}

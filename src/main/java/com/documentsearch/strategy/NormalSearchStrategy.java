package com.documentsearch.strategy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NormalSearchStrategy implements SearchStrategy {
    // Simple AND search: document must contain ALL terms.
    @Override
    public Set<String> search(String query, Map<String, Map<String, Integer>> index) {
        if (query == null || query.trim().isEmpty()) {
            return new HashSet<>();
        }

        Set<String> resultIds = null;
        String[] terms = query.toLowerCase().split("\\s+");

        for (String term : terms) {
            Map<String, Integer> docs = index.get(term);
            if (docs == null || docs.isEmpty()) {
                // If any term is not found, the AND search yields no results
                return new HashSet<>();
            }

            if (resultIds == null) {
                resultIds = new HashSet<>(docs.keySet());
            } else {
                resultIds.retainAll(docs.keySet()); // Intersection (AND logic)
            }
        }

        return resultIds != null ? resultIds : new HashSet<>();
    }
}
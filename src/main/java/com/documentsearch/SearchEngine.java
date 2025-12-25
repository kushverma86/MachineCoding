package com.documentsearch;

import com.documentsearch.exception.CategoryNotFoundException;
import com.documentsearch.model.Document;
import com.documentsearch.strategy.OrderingStrategy;
import com.documentsearch.strategy.SearchStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SearchEngine {

    // Category Name -> Inverted Index Map
    // Inverted Index: Term -> Document ID -> Frequency
    private final Map<String, Map<String, Map<String, Integer>>> categoryIndexes = new ConcurrentHashMap<>();

    // Category Name -> Document ID -> Document Object
    private final Map<String, Map<String, Document>> categoryDocuments = new ConcurrentHashMap<>();

    // --- Core Operations ---

    public void createCategory(String categoryName) {
        categoryIndexes.putIfAbsent(categoryName, new ConcurrentHashMap<>());
        categoryDocuments.putIfAbsent(categoryName, new ConcurrentHashMap<>());
    }

    public void insertDocument(String categoryName, Document document) {
        if (!categoryIndexes.containsKey(categoryName)) {
            throw new CategoryNotFoundException(categoryName);
        }

        // 1. Store the document
        categoryDocuments.get(categoryName).put(document.getId(), document);

        // 2. Index the document
        Map<String, Map<String, Integer>> index = categoryIndexes.get(categoryName);
        Map<String, Integer> termFrequencies = tokenize(document.getContent());

        for (Map.Entry<String, Integer> entry : termFrequencies.entrySet()) {
            String term = entry.getKey();
            Integer freq = entry.getValue();

            index.computeIfAbsent(term, k -> new ConcurrentHashMap<>())
                    .put(document.getId(), freq);
        }
    }

    public void deleteDocument(String categoryName, String documentId) {
        if (!categoryIndexes.containsKey(categoryName)) {
            throw new CategoryNotFoundException(categoryName);
        }

        // 1. Remove from document store
        categoryDocuments.get(categoryName).remove(documentId);

        // 2. Remove from index
        Map<String, Map<String, Integer>> index = categoryIndexes.get(categoryName);
        for (Map<String, Integer> docMap : index.values()) {
            docMap.remove(documentId);
        }

        // Optional: clean up empty term entries in index to save memory
        index.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    // --- Search Operation using Strategies ---

    public List<Document> search(
            String categoryName,
            String query,
            SearchStrategy searchStrategy,
            OrderingStrategy orderingStrategy
    ) {
        if (!categoryIndexes.containsKey(categoryName)) {
            throw new CategoryNotFoundException(categoryName);
        }

        Map<String, Map<String, Integer>> index = categoryIndexes.get(categoryName);
        Map<String, Document> docStore = categoryDocuments.get(categoryName);

        // 1. Execute Search Strategy: Get matching document IDs
        // Note: The set returned by FrequencyBasedSearchStrategy is ordered (LinkedHashSet)
        Set<String> matchingIds = searchStrategy.search(query, index);

        // 2. Retrieve Document Objects
        List<Document> results = matchingIds.stream()
                .map(docStore::get)
                .filter(Objects::nonNull) // Should not happen, but safe check
                .collect(Collectors.toList());


        // 3. Apply Ordering Strategy
        results.sort(orderingStrategy.getComparator());

        // Note: If FrequencyBasedSearchStrategy was used, the order is based on frequency,
        // and the OrderingStrategy (e.g., Created) acts as a secondary sort.

        return results;
    }

    // --- Utility Methods (The Tokenizer) ---

    private Map<String, Integer> tokenize(String text) {
        Map<String, Integer> frequencies = new HashMap<>();
        if (text == null || text.isEmpty()) {
            return frequencies;
        }

        // Simple tokenization: to lowercase, split by non-word characters
        String[] terms = text.toLowerCase().split("[^a-z0-9]+");

        for (String term : terms) {
            if (!term.isEmpty()) {
                frequencies.put(term, frequencies.getOrDefault(term, 0) + 1);
            }
        }
        return frequencies;
    }
}

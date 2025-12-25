package com.documentsearch;

import com.documentsearch.exception.CategoryNotFoundException;
import com.documentsearch.model.Document;
import com.documentsearch.strategy.*;

import java.util.List;

public class SearchEngineClient {
    public static void main(String[] args) {
        SearchEngine engine = new SearchEngine();

        // --- Setup ---
        engine.createCategory("TechBlog");

        // Use the example data
        Document doc1 = new Document("Doc1", "apple is a fruit");
        Document doc2 = new Document("Doc2", "apple, apple come on!");
        Document doc3 = new Document("Doc3", "oranges are sour");
        Document doc4 = new Document("Doc4", "apple-pie is sweet");

        engine.insertDocument("TechBlog", doc1);
        engine.insertDocument("TechBlog", doc2);
        engine.insertDocument("TechBlog", doc3);
        engine.insertDocument("TechBlog", doc4);

        // Doc2 was created first, let's update Doc1 to make it the most recently updated
        doc1.updateContent("updated content");

        System.out.println("--- Search Test: 'apple' ---");

        // --- 1. Frequency-Based Search, Ordered by Creation Date (default is newest first) ---
        SearchStrategy freqSearch = new FrequencyBasedSearchStrategy();
        OrderingStrategy createdOrder = new CreatedOrderingStrategy();

        System.out.println("\nFrequency Search (Sorted by Creation Date):");
        List<Document> results1 = engine.search("TechBlog", "apple", freqSearch, createdOrder);

        // Expected frequency order: Doc2 (2), Doc1 (1), Doc4 (1)
        // If creation is same, it falls back to the frequency order.
        // The output should be: Doc2 (oldest), Doc1 (newer), Doc4 (newest) based on their creation time.
        // Wait, CreatedOrderingStrategy sorts newest first.
        // Let's assume Doc4 was created last, then Doc1, then Doc2.
        // Output should be: Doc4, Doc1, Doc2.
        results1.forEach(d -> System.out.println(d.getId() + " (Created: " + d.getCreatedAt().getSecond() + ")"));

        // --- 2. Normal (AND) Search, Ordered by Update Date (newest first) ---
        SearchStrategy normalSearch = new NormalSearchStrategy();
        OrderingStrategy updatedOrder = new UpdatedOrderingStrategy();

        System.out.println("\nNormal Search (Sorted by Last Updated):");
        List<Document> results2 = engine.search("TechBlog", "apple fruit", normalSearch, updatedOrder);
        // Only Doc1 contains BOTH "apple" AND "fruit"
        results2.forEach(d -> System.out.println(d.getId() + " (Updated: " + d.getUpdatedAt().getSecond() + ")"));

        // --- 3. Exception Handling Test ---
        try {
            engine.search("NonExistentCategory", "test", normalSearch, createdOrder);
        } catch (CategoryNotFoundException e) {
            System.err.println("\nCaught Exception: " + e.getMessage());
        }

        // --- 4. Delete Test ---
        engine.deleteDocument("TechBlog", "Doc2");
        System.out.println("\nAfter deleting Doc2, Normal Search for 'apple':");
        List<Document> results3 = engine.search("TechBlog", "apple", normalSearch, createdOrder);
        results3.forEach(d -> System.out.println(d.getId())); // Should only contain Doc1 and Doc4
    }
}

package com.documentsearch.strategy;

import java.util.*;

public class FrequencyBasedSearchStrategy implements SearchStrategy {
    // Finds documents that contain ANY of the terms and sorts them by total frequency.
    @Override
    public Set<String> search(String query, Map<String, Map<String, Integer>> index) {
        if (query == null || query.trim().isEmpty()) {
            return new HashSet<>();
        }

        Map<String, Integer> docFreq = new HashMap<>();

        String[] tokens = query.toLowerCase().split("[^a-z0-9]+");

        for (String token : tokens){
            Map<String, Integer> docs = index.get(token);

            if (docs == null || docs.isEmpty())
                return new HashSet<>();

            docs.forEach((docId, freq) ->
                    docFreq.put(docId, docFreq.getOrDefault(docId, 0) + freq));
        }

        List<String> sortedIds = new ArrayList<>(docFreq.keySet());
        sortedIds.sort(Comparator.comparing( (String id) -> docFreq.get(id)).reversed());

        return new LinkedHashSet<>(sortedIds);








//        Map<String, Integer> docFrequencies = new HashMap<>();
//        String[] terms = query.toLowerCase().split("\\s+");
//
//        for (String term : terms) {
//            Map<String, Integer> docs = index.get(term);
//            if (docs != null) {
//                docs.forEach((docId, freq) ->
//                        docFrequencies.put(docId, docFrequencies.getOrDefault(docId, 0) + freq)
//                );
//            }
//        }
//
//        // Return only the IDs, but internally sorted by frequency (descending)
//        // This sorting will be combined with the OrderingStrategy later.
//        List<String> sortedIds = new ArrayList<>(docFrequencies.keySet());
//
//        sortedIds.sort(Comparator.comparing((String id) -> docFrequencies.get(id)).reversed());
//
//        sortedIds.sort((id1, id2) -> docFrequencies.get(id2).compareTo(docFrequencies.get(id1)));
//
//        return new LinkedHashSet<>(sortedIds); // Preserve the frequency order for the next step
    }
}
package com.customerissueresolutionportal.repository;


import com.customerissueresolutionportal.excpetion.NotFoundException;
import com.customerissueresolutionportal.model.Issue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// IssueRepository.java
public class IssueRepository {
    // Thread-safe map for production-readiness
    private final Map<String, Issue> issueStore = new HashMap<>();

    public Issue save(Issue issue) {
        issueStore.put(issue.getIssueId(), issue);
        return issue;
    }

    public Issue findById(String issueId) throws NotFoundException {
        Issue issue = issueStore.get(issueId);
        if (issue == null) {
            throw new NotFoundException("Issue", issueId);
        }
        return issue;
    }

    public List<Issue> findAll() {
        return new ArrayList<>(issueStore.values());
    }
}
package com.customerissueresolutionportal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Issue {
    private String issueId;
    private String transactionId;
    private IssueType issueType;
    private String subject;
    private String description;
    private String customerEmail;
    private IssueStatus status;
    private String assignedAgentId;
    private String resolution;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdatedOn;


}
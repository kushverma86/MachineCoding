package com.customerissueresolutionportal.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IssueFilter {
    private String issueId;
    private String customerEmail;
    private IssueType issueType;
    private IssueStatus status;
    // We can add more fields like date range, agentIdn depending on our need and filter etc.
}

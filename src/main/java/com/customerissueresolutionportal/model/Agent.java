package com.customerissueresolutionportal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agent {
    private String agentId; // A unique ID, e.g., UUID or email
    private String agentName;
    private String agentEmail;
    private List<IssueType> expertise;
    private boolean isAvailable;  // Tracks if the agent is currently working on an issue or available
    private String currentIssueId; // ID of the issue the agent is currently assigned
    private List<String> workHistoryIssueIds; // To track resolved issues

}

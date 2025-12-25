package com.customerissueresolutionportal.strategy;


import com.customerissueresolutionportal.model.Agent;
import com.customerissueresolutionportal.model.Issue;

import java.util.List;
import java.util.Optional;

public interface IssueAssignmentStrategy {
    Optional<Agent> findAvailableAgent(Issue issue, List<Agent> allAgents);
}

package com.customerissueresolutionportal.strategy;


import com.customerissueresolutionportal.model.Agent;
import com.customerissueresolutionportal.model.Issue;

import java.util.List;
import java.util.Optional;

public class FirstAvailableAgentStrategy implements IssueAssignmentStrategy{
    @Override
    public Optional<Agent> findAvailableAgent(Issue issue, List<Agent> allAgents) {
        return allAgents.stream()
                .filter(Agent::isAvailable)
                .filter(agent -> agent.getExpertise().contains(issue.getIssueType()))
                .findFirst();
    }
}

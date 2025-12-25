package com.customerissueresolutionportal.service;


import com.customerissueresolutionportal.excpetion.NotFoundException;
import com.customerissueresolutionportal.model.Agent;
import com.customerissueresolutionportal.model.Issue;
import com.customerissueresolutionportal.model.IssueFilter;
import com.customerissueresolutionportal.model.IssueType;
import com.customerissueresolutionportal.repository.AgentRepository;
import com.customerissueresolutionportal.repository.IssueRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdminService {

    public final AgentRepository agentRepository;
    private final IssueRepository issueRepository;

    // Constructor Injection
    public AdminService(AgentRepository agentRepository, IssueRepository issueRepository) {
        this.agentRepository = agentRepository;
        this.issueRepository = issueRepository;
    }

    /**
     * Admin can onboard a new agent.
     * @return The created Agent object.
     */
    public Agent addAgent(String agentEmail, String agentName, List<String> expertiseText) {

        if (agentRepository.findByEmail(agentEmail) != null) {
            System.err.println("Agent with email " + agentEmail + " already exists.");
            // In a production system, this would throw a DuplicateAgentException
            return null;
        }

        String agentId = "A" + UUID.randomUUID().toString().toUpperCase();
        List<IssueType> expertise = expertiseText.stream()
                .map(IssueType::fromString)
                .collect(Collectors.toList());

        Agent agent = Agent.builder()
                .agentId(agentId)
                .agentName(agentName)
                .agentEmail(agentEmail)
                .expertise(expertise)
                .isAvailable(true)
                .workHistoryIssueIds(new ArrayList<>())
                .build();

        agentRepository.save(agent);
        System.out.printf(">>> Agent %s created\n", agentId);
        return agent;
    }

    /**
     * Agent/Admin can search for issues based on a filter.
     */
    public List<Issue> getIssues(IssueFilter filter) {
        return issueRepository.findAll().stream()
                .filter(issue -> filter.getIssueId() == null || issue.getIssueId().equals(filter.getIssueId()))
                .filter(issue -> filter.getCustomerEmail() == null || issue.getCustomerEmail().equals(filter.getCustomerEmail()))
                .filter(issue -> filter.getIssueType() == null || issue.getIssueType().equals(filter.getIssueType()))
                .filter(issue -> filter.getStatus() == null || issue.getStatus().equals(filter.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Admin can view the work history of all agents.
     * @return A map of Agent ID to the list of resolved Issues.
     */
    public Map<String, List<Issue>> viewAgentsWorkHistory() {
        return agentRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Agent::getAgentId,
                        agent -> agent.getWorkHistoryIssueIds().stream()
                                .map(issueId -> {
                                    try {
                                        return issueRepository.findById(issueId);
                                    } catch (NotFoundException e) {
                                        // Log a system error if history refers to a non-existent issue
                                        System.err.println("ERROR: Issue " + issueId + " in agent history not found.");
                                        return null;
                                    }
                                })
                                .filter(issue -> issue != null)
                                .collect(Collectors.toList())
                ));
    }
}

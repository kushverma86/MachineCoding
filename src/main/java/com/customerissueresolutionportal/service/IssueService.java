package com.customerissueresolutionportal.service;



import com.customerissueresolutionportal.excpetion.AssignmentException;
import com.customerissueresolutionportal.excpetion.InvalidStateException;
import com.customerissueresolutionportal.excpetion.IssueResolutionException;
import com.customerissueresolutionportal.model.Agent;
import com.customerissueresolutionportal.model.Issue;
import com.customerissueresolutionportal.model.IssueStatus;
import com.customerissueresolutionportal.model.IssueType;
import com.customerissueresolutionportal.repository.AgentRepository;
import com.customerissueresolutionportal.repository.IssueRepository;
import com.customerissueresolutionportal.strategy.IssueAssignmentStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class IssueService {

    // Dependency Injection via Constructor (Field injection is generally discouraged)
    private final IssueRepository issueRepository;
    private final AgentRepository agentRepository;
    private final IssueAssignmentStrategy assignmentStrategy;

    // Use a separate structure for WAITING issues, separated by issue type/expertise
    // For simplicity, we use one main queue for all WAITING issues.
    private final List<String> waitingIssueQueue;

    // Constructor Injection
    public IssueService(IssueRepository issueRepository,
                        AgentRepository agentRepository,
                        IssueAssignmentStrategy assignmentStrategy,
                        List<String> waitingIssueQueue) {
        this.issueRepository = issueRepository;
        this.agentRepository = agentRepository;
        this.assignmentStrategy = assignmentStrategy;
        this.waitingIssueQueue = waitingIssueQueue;
    }

    /**
     * Customer logs a complaint.
     * @return The created Issue object.
     */
    public Issue createIssue(String transactionId, String issueTypeText, String subject, String description, String email) {
        String issueId = "I" + UUID.randomUUID().toString().toUpperCase();
        IssueType issueType = IssueType.fromString(issueTypeText);
        LocalDateTime now = LocalDateTime.now();

        Issue issue = Issue.builder()
                .issueId(issueId)
                .transactionId(transactionId)
                .issueType(issueType)
                .subject(subject)
                .description(description)
                .customerEmail(email)
                .status(IssueStatus.OPEN)
                .createdOn(now)
                .lastUpdatedOn(now)
                .build();

        issueRepository.save(issue);
        System.out.printf(">>> Issue %s created against transaction \"%s\"\n", issueId, transactionId);

        // Try to assign immediately upon creation
//        try {
//            assignIssue(issueId);
//        } catch (IssueResolutionException e) {
//            // Log that assignment failed, but issue creation was successful.
//            System.out.println("Issue created but could not be assigned immediately: " + e.getMessage());
//        }

        return issue;
    }

    /**
     * Assigns an issue (OPEN or WAITING) to a suitable free agent based on the strategy.
     * If no agent is free, the issue is put in the waiting queue.
     * @return The Agent ID it was assigned to, or null if placed in waiting queue.
     */
    public String assignIssue(String issueId) throws IssueResolutionException {
        Issue issue = issueRepository.findById(issueId);

        if (issue.getStatus() != IssueStatus.OPEN && issue.getStatus() != IssueStatus.WAITING) {
            throw new InvalidStateException("Issue " + issueId + " is already " + issue.getStatus() + " and cannot be assigned.");
        }

        List<Agent> allAgents = agentRepository.findAll();
        Agent selectedAgent = assignmentStrategy.findAvailableAgent(issue, allAgents)
                .orElse(null);

        if (selectedAgent != null) {
            // Assign issue to the agent
            selectedAgent.setAvailable(false);
            selectedAgent.setCurrentIssueId(issue.getIssueId());
            agentRepository.save(selectedAgent); // Update agent state

            // Update issue state
            issue.setStatus(IssueStatus.IN_PROGRESS);
            issue.setAssignedAgentId(selectedAgent.getAgentId());
            issue.setLastUpdatedOn(LocalDateTime.now());
            issueRepository.save(issue);

            // Remove from waiting queue if it was there
            waitingIssueQueue.remove(issueId);

            System.out.printf(">>> Issue %s assigned to agent %s\n", issueId, selectedAgent.getAgentId());
            return selectedAgent.getAgentId();
        } else {
            // No suitable agent available -> place in waiting queue
            if (!waitingIssueQueue.contains(issueId)) { // Avoid duplicates
                waitingIssueQueue.add(issueId);
                issue.setStatus(IssueStatus.WAITING);
                issue.setLastUpdatedOn(LocalDateTime.now());
                issueRepository.save(issue);
            }
            System.out.printf(">>> Issue %s added to waitlist.\n", issueId);
            return null;
        }
    }

    /**
     * Allows an agent to update the status and/or add a comment to an assigned issue.
     */
    public Issue updateIssue(String issueId, IssueStatus newStatus, String resolutionComment) throws IssueResolutionException {
        Issue issue = issueRepository.findById(issueId);

        if (issue.getStatus() == IssueStatus.RESOLVED) {
            throw new InvalidStateException("Cannot update a RESOLVED issue: " + issueId);
        }

        issue.setStatus(newStatus);

        if (resolutionComment != null && !resolutionComment.isEmpty()) {
            issue.setResolution(resolutionComment); // Resolution can be used for in-progress comments
        }

        issue.setLastUpdatedOn(LocalDateTime.now());
        issueRepository.save(issue);
        System.out.printf(">>> %s status updated to %s\n", issueId, newStatus);
        return issue;
    }

    /**
     * Agent resolves the issue, freeing them up for the next assignment.
     */
    public Issue resolveIssue(String issueId, String finalResolution) throws IssueResolutionException {
        Issue issue = updateIssue(issueId, IssueStatus.RESOLVED, finalResolution);

        // Update Agent state
        Agent agent = agentRepository.findById(issue.getAssignedAgentId());

        // This check should ideally not fail if logic is correct
        if (!issueId.equals(agent.getCurrentIssueId())) {
            // Highly unusual state, log a major error
            throw new AssignmentException("Agent " + agent.getAgentId() + " is resolving an issue that is not their current assignment.");
        }

        agent.setAvailable(true);
        agent.setCurrentIssueId(null);
        agent.getWorkHistoryIssueIds().add(issueId);
        agentRepository.save(agent);

        System.out.printf(">>> %s issue marked resolved\n", issueId);

        // Crucial step: try to assign the next WAITING issue
        processWaitingQueue();

        return issue;
    }

    /**
     * Attempts to assign the next issue in the WAITING queue.
     */
    private void processWaitingQueue() {
        // Using an iterator to safely remove from the queue while iterating
        for (String waitingIssueId : waitingIssueQueue) {
            try {
                // Try to assign the next issue. If successful, it's removed from the queue inside assignIssue.
                // We only need to assign one issue for the newly available agent.
                String assignedAgentId = assignIssue(waitingIssueId);
                if (assignedAgentId != null) {
                    System.out.println("Assigned next waiting issue: " + waitingIssueId + " to agent " + assignedAgentId);
                    return; // Agent is busy again, stop processing the queue for now
                }
            } catch (IssueResolutionException e) {
                // Log and continue to the next waiting issue, if this one has an issue (e.g. agent expertise changed)
                System.err.println("Failed to assign waiting issue " + waitingIssueId + ": " + e.getMessage());
            }
        }
    }
}

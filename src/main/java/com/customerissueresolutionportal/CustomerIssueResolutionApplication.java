package com.customerissueresolutionportal;


import com.customerissueresolutionportal.excpetion.IssueResolutionException;
import com.customerissueresolutionportal.model.*;
import com.customerissueresolutionportal.repository.AgentRepository;
import com.customerissueresolutionportal.repository.IssueRepository;
import com.customerissueresolutionportal.service.AdminService;
import com.customerissueresolutionportal.service.IssueService;
import com.customerissueresolutionportal.strategy.FirstAvailableAgentStrategy;
import com.customerissueresolutionportal.strategy.IssueAssignmentStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class CustomerIssueResolutionApplication {

    private final IssueService issueService;
    private final AdminService adminService;

    public CustomerIssueResolutionApplication(IssueService issueService, AdminService adminService) {
        this.issueService = issueService;
        this.adminService = adminService;
    }

    public static void main(String[] args) throws IssueResolutionException {
        IssueRepository issueRepository = new IssueRepository();
        AgentRepository agentRepository = new AgentRepository();
        List<String> waitingIssueQueue = new CopyOnWriteArrayList<>();

        // 2. Initialize Strategy (Assignment Strategy Pattern)
        IssueAssignmentStrategy assignmentStrategy = new FirstAvailableAgentStrategy();

        // 3. Initialize Services (Business Logic Layer)
        IssueService issueService = new IssueService(issueRepository, agentRepository, assignmentStrategy, waitingIssueQueue);
        AdminService adminService = new AdminService(agentRepository, issueRepository);

        // 4. Run Application
        CustomerIssueResolutionApplication app = new CustomerIssueResolutionApplication(issueService, adminService);
        app.runExample();
    }

    // Convenience method to call the service functions
    public void createIssue(String transactionId, String issueType, String subject, String description, String email) {
        issueService.createIssue(transactionId, issueType, subject, description, email);
    }

    public void addAgent(String agentEmail, String agentName ,List<String> expertise) {
        adminService.addAgent(agentEmail, agentName, expertise);
    }

    public void assignIssue(String issueId) throws IssueResolutionException {
        issueService.assignIssue(issueId);
    }

    public List<Issue> getIssues(IssueFilter filter) {
        List<Issue> issues = adminService.getIssues(filter);
        // Print the result in the requested format
        System.out.printf(">>> getIssue(%s):%n", filter);
        issues.forEach(i ->
                System.out.printf(" %s {\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"}%n",
                        i.getIssueId(),
                        i.getTransactionId(),
                        i.getIssueType().toString(),
                        i.getSubject(),
                        i.getDescription(),
                        i.getCustomerEmail(),
                        i.getStatus().toString()
                )
        );
        return issues;
    }

    public void updateIssue(String issueId, IssueStatus status, String resolution) throws IssueResolutionException {
        issueService.updateIssue(issueId, status, resolution);
    }

    public void resolveIssue(String issueId, String resolution) throws IssueResolutionException {
        issueService.resolveIssue(issueId, resolution);
    }

    public void viewAgentsWorkHistory() {
        Map<String, List<Issue>> history = adminService.viewAgentsWorkHistory();
        System.out.println(">>> viewAgentsWorkHistory()");

        // Print the result in the requested format
        String result = history.entrySet().stream()
                .map(entry -> {
                    String issueList = entry.getValue().stream()
                            .map(Issue::getIssueId)
                            .collect(Collectors.joining(", "));
                    return String.format("%s -> {%s}", entry.getKey(), issueList);
                })
                .collect(Collectors.joining("\r\n"));

        System.out.println(result);
    }


    /**
     * Executes the example use cases from the prompt.
     */
    public void runExample() throws IssueResolutionException {

        // Simulating the Issue ID generation based on the example (I1, I2, I3)
        // In the real implementation, UUID is used. For the example, we'll manually
        // retrieve the first 3 created IDs for matching.

        // 1. Create Issues
        createIssue("T1", "Payment Related", "Payment Failed", "My payment failed but money is debited", "testUser1@test.com");
        createIssue("T2", "Mutual Fund Related", "Purchase Failed", "Unable to purchase Mutual Fund", "testUser2@test.com");
        createIssue("T3", "Payment Related", "Payment Failed", "My payment failed but money is debited", "testUser2@test.com");

        // Get the generated IDs for the example
        List<Issue> issues = adminService.getIssues(IssueFilter.builder().customerEmail("testUser1@test.com").build());
        String i1Id = issues.stream().filter(i -> "T1".equals(i.getTransactionId())).findFirst().get().getIssueId();
        issues = adminService.getIssues(IssueFilter.builder().customerEmail("testUser2@test.com").build());
        String i2Id = issues.stream().filter(i -> "T2".equals(i.getTransactionId())).findFirst().get().getIssueId();
        String i3Id = issues.stream().filter(i -> "T3".equals(i.getTransactionId())).findFirst().get().getIssueId();

        // 2. Add Agents
        addAgent("agent1@test.com", "Agent 1", Arrays.asList("Payment Related", "Gold Related"));
        addAgent("agent2@test.com", "Agent 2", Arrays.asList("Payment Related", "Mutual Fund Related"));

        // Get the generated IDs for the example
        Agent a1 = adminService.agentRepository.findByEmail("agent1@test.com");
        Agent a2 = adminService.agentRepository.findByEmail("agent2@test.com");
        String a1Id = a1.getAgentId();
        String a2Id = a2.getAgentId();
//        System.out.printf(">>> Agent %s created\n", a1Id);
//        System.out.printf(">>> Agent %s created\n", a2Id);


        // 3. Assign Issues
        assignIssue(i1Id); // I1 (Payment) -> A1 (Payment)
        assignIssue(i2Id); // I2 (Mutual Fund) -> A2 (Mutual Fund)
        // A1 and A2 are now busy. I3 (Payment) is WAITING for an agent.
        assignIssue(i3Id); // I3 (Payment) -> Waitlist/Waiting

        // 4. Get Issues (Filtering)
        getIssues(IssueFilter.builder().customerEmail("testUser2@test.com").build());
        System.out.println("---");
        getIssues(IssueFilter.builder().issueType(IssueType.PAYMENT_RELATED).build());
        System.out.println("---");

        // 5. Update and Resolve
        updateIssue(i3Id, IssueStatus.IN_PROGRESS, "Waiting for payment confirmation"); // This line is for testing the update logic. I3 is OPEN/WAITING, update should work.

        // Resolve I1 (Agent A1 becomes free and takes the next waiting issue: I3)
        resolveIssue(i1Id, "Transaction timed out, amount will be reversed shortly.");
        // I3 is now assigned to A1

        // Resolve I2 (Agent A2 becomes free)
        resolveIssue(i2Id, "Server-side error, purchase request re-initiated.");

        // Resolve I3 (Agent A1 becomes free again)
        resolveIssue(i3Id, "PaymentFailed debited amount will get reversed");

        // 6. View History
        viewAgentsWorkHistory();
    }
}
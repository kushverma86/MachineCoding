package com.customerissueresolutionportal.excpetion;

// Thrown when an issue is assigned to an incompatible agent or cannot be assigned
public class AssignmentException extends IssueResolutionException {
    public AssignmentException(String message) {
        super(message);
    }
}

package com.customerissueresolutionportal.excpetion;


// Thrown when an Issue or Agent cannot be found
public class NotFoundException extends IssueResolutionException {
    public NotFoundException(String entityType, String id) {
        super(entityType + " with ID " + id + " not found.");
    }
}

package com.customerissueresolutionportal.excpetion;

// Thrown when a business operation is attempted on an invalid state (e.g., resolving an OPEN issue)
public class InvalidStateException extends IssueResolutionException {
    public InvalidStateException(String message) {
        super(message);
    }
}
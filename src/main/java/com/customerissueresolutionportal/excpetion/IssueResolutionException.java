package com.customerissueresolutionportal.excpetion;

// Base class for all business exceptions
public class IssueResolutionException extends RuntimeException{
    public IssueResolutionException(String message) {
        super(message);
    }
}

package com.customerissueresolutionportal.model;

public enum IssueType {
//    payment-related, mutual fund-related, gold-related, or insurance-related

    PAYMENT_RELATED, MUTUAL_FUND_RELATED, GOLD_RELATED, INSURANCE_RELATED, OTHER;

    public static IssueType fromString(String text) {
        try {
            return IssueType.valueOf(text.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}

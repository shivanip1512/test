package com.cannontech.stars.util;

public class OptOutRuleFormatException extends WebClientException {
    private final String rule;
    
    public OptOutRuleFormatException(String message, String rule) {
        super(message);
        this.rule = rule;
    }
    
    public String getRule() {
        return rule;
    }
    
}

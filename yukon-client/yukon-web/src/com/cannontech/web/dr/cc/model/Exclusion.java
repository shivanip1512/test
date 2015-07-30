package com.cannontech.web.dr.cc.model;

public class Exclusion {
    public enum Status {EXCLUDE_OVERRIDABLE, EXCLUDE};
    private static final String keyBase = "yukon.web.modules.dr.cc.init.customerVerification.exclusion.";
    
    private String key;
    private Status status;
    private Object[] arguments;
    
    public Exclusion(String key, Status status, Object... arguments) {
        this.key = keyBase + key;
        this.status = status;
        this.arguments = arguments;
    }
    
    public String getKey() {
        return key;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public boolean isForceExcluded() {
        return status == Status.EXCLUDE;
    }
    
    public Object[] getArguments() {
        return arguments;
    }
}

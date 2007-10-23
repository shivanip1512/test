package com.cannontech.core.authorization.support;

public enum AllowDeny {
    ALLOW(true),DENY(false);
    
    private final boolean allowValue;

    private AllowDeny(boolean allowValue) {
        this.allowValue = allowValue;
    }
    
    public boolean getAllowValue() {
        return allowValue;
    }
    
    public boolean getDenyValue() {
        return !allowValue;
    }
}

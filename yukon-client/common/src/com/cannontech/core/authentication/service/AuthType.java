package com.cannontech.core.authentication.service;

public enum AuthType {
    PLAIN("Normal"),
    HASH_SHA("Hashed"),
    RADIUS("Radius"),
    NONE("No Login"),
    AD("Active Directory"),
    LDAP("LDAP");
    
    private final String title;

    private AuthType(String title) {
        this.title = title;
        
    }
    
    @Override
    public String toString() {
        return title;
    }
    
}

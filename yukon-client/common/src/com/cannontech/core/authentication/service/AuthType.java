package com.cannontech.core.authentication.service;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum AuthType implements DatabaseRepresentationSource, DisplayableEnum {
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
    
    @Override
    public String getFormatKey() {
        return "yukon.common.authType." + name();
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }

    public String getTitle() {
        return title;
    }
    
}
package com.cannontech.core.authorization.support;

/**
 * Enum of permissions
 */
public enum Permission {

    READ_COMMAND("read command", false), 
    WRITE_COMMAND("write command", false), 
    CONTROL_COMMAND("control command", false), 
    READ_DISCONNECT_COMMAND("read disconnect command", false), 
    WRITE_DISCONNECT_COMMAND("write disconnect command", false), 
    OTHER_COMMAND("Unrecognized device command", false), 
    LM_VISIBLE("allow LM visibility", false), 
    ALLOWED_COMMAND("Allowed Command", false),
    PAO_VISIBLE("Disallow pao visibility", true);

    private final String description;
    private final Boolean defaultAuth;
    
    private Permission(String description, Boolean defaultAuth) {
        this.description = description;
        this.defaultAuth = defaultAuth;
    }

    public String getDescription() {
        return this.description;
    }
    
    public Boolean getDefault() {
        return this.defaultAuth;
    }
}

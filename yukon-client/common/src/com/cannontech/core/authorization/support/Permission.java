package com.cannontech.core.authorization.support;

/**
 * Enum of permissions
 */
public enum Permission {

    READ_COMMAND("read command", false, false), 
    WRITE_COMMAND("write command", false, false), 
    CONTROL_COMMAND("control command", false, false), 
    OTHER_COMMAND("Unrecognized device command", false, false), 
    LM_VISIBLE("Load Management Visibility", false, true), 
    ALLOWED_COMMAND("Allowed Command", false, false),
    PAO_VISIBLE("Object Visibility", true, true),
    DEFAULT_ROUTE("Default Route", false, true);

    private final String description;
    private final boolean defaultAuth;
    private final boolean settablePerPao;
    
    private Permission(String description, boolean defaultAuth, boolean settablePerPao) {
        this.description = description;
        this.defaultAuth = defaultAuth;
        this.settablePerPao = settablePerPao;
    }

    public String getDescription() {
        return this.description;
    }
    
    public boolean getDefault() {
        return this.defaultAuth;
    }
    
    public boolean isSettablePerPao(){
        return this.settablePerPao;
    }
}

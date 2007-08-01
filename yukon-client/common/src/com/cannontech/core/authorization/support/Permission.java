package com.cannontech.core.authorization.support;

/**
 * Enum of permissions
 */
public enum Permission {

    READ_COMMAND("read command"), WRITE_COMMAND("write command"), CONTROL_COMMAND("control command"), READ_DISCONNECT_COMMAND(
            "read disconnect command"), WRITE_DISCONNECT_COMMAND("write disconnect command"), OTHER_COMMAND(
            "Unrecognized device command"), LM_VISIBLE("allow LM visibility"), ALLOWED_COMMAND("Allowed Command");

    private final String description;

    private Permission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}

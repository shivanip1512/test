package com.cannontech.common.databaseMigration.exception;

public class ConfigurationWarningException extends Exception {

    public ConfigurationWarningException(String message) {
        super(message);
    }

    public ConfigurationWarningException(String message, Throwable cause) {
        super(message, cause);
    }
}

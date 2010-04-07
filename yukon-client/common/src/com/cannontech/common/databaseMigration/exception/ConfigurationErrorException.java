package com.cannontech.common.databaseMigration.exception;

public class ConfigurationErrorException extends Exception {

    public ConfigurationErrorException(String message) {
        super(message);
    }

    public ConfigurationErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}

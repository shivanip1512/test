package com.cannontech.common.device.config.dao;

public class InvalidConfigurationRemovalException extends RuntimeException {
    public InvalidConfigurationRemovalException(String string, Throwable cause) {
        super(string,cause);
    }

    public InvalidConfigurationRemovalException(String string) {
        super(string);
    }
}

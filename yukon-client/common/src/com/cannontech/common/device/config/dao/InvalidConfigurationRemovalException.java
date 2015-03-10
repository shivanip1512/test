package com.cannontech.common.device.config.dao;

import java.security.InvalidParameterException;

public class InvalidConfigurationRemovalException extends InvalidParameterException {

    public InvalidConfigurationRemovalException(String string) {
        super(string);
    }
}

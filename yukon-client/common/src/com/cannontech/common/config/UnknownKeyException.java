package com.cannontech.common.config;

public class UnknownKeyException extends RuntimeException {

    public UnknownKeyException(String key) {
        super("\"" + key + "\" was not found in the configuration map.");
    }

}

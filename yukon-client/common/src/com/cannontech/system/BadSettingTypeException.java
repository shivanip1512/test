package com.cannontech.system;


public class BadSettingTypeException extends RuntimeException {
    public BadSettingTypeException(GlobalSettingType setting, String value, Throwable cause) {
        super("Unable to convert value of \"" + value + " for " + setting, cause);
    }
}

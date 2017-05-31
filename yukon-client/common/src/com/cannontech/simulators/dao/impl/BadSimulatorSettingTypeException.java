package com.cannontech.simulators.dao.impl;

import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;

public class BadSimulatorSettingTypeException extends RuntimeException {
    public BadSimulatorSettingTypeException(YukonSimulatorSettingsKey property, String value, Throwable cause) {
        super("Unable to convert value of \"" + value + "\" for " + property, cause);
    }
}
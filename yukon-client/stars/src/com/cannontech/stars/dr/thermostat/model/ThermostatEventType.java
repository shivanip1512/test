package com.cannontech.stars.dr.thermostat.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ThermostatEventType implements DisplayableEnum {
    MANUAL,
    SCHEDULE,
    RESTORE;
    
    // this key prefix can be found in the following file:
    // com.cannontech.yukon.dr.consumer.xml
    private final static String keyPrefix = "yukon.dr.consumer.thermostat.eventType.";
    
    /**
     * I18N key for the display text for this action
     * @return Display key
     */    
    public String getFormatKey() {
        return keyPrefix + name();
    }
}

package com.cannontech.stars.dr.thermostat.model;

/**
 * Enum which represents schedule update results
 */
public enum ThermostatScheduleUpdateResult {
    OPERATOR_UNAVAILABLE_ERROR, 
    OPERATOR_NO_SERIAL_ERROR, 
    CONSUMER_UPDATE_SCHEDULE_ERROR, 
    CONSUMER_NO_SERIAL_ERROR, 
    CONSUMER_UPDATE_SCHEDULE_SUCCESS,
    CONSUMER_SAVE_SCHEDULE_SUCCESS;

    // this key prefix can be found in the following file:
    // com.cannontech.yukon.dr.consumer.xml
    private final static String keyPrefix = "yukon.dr.consumer.scheduleUpdate.result.";

    /**
     * I18N key for the display text for this action
     * @return Display key
     */
    public String getDisplayKey() {
        return keyPrefix + name();
    }

}

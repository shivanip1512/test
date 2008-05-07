package com.cannontech.stars.dr.thermostat.model;

/**
 * Enum which represents schedule update results
 */
public enum ThermostatScheduleUpdateResult {
    OPERATOR_UNAVAILABLE_ERROR(true), 
    OPERATOR_NO_SERIAL_ERROR(true), 
    CONSUMER_UPDATE_SCHEDULE_ERROR(true), 
    CONSUMER_NO_SERIAL_ERROR(true), 
    CONSUMER_MULTIPLE_ERROR(true), 
    CONSUMER_UPDATE_SCHEDULE_SUCCESS(false),
    CONSUMER_SAVE_SCHEDULE_SUCCESS(false);

    // this key prefix can be found in the following file:
    // com.cannontech.yukon.dr.consumer.xml
    private final static String keyPrefix = "yukon.dr.consumer.scheduleUpdate.result.";

    private boolean failed;

    private ThermostatScheduleUpdateResult(boolean failed) {
        this.failed = failed;
    }

    public boolean isFailed() {
        return failed;
    }

    /**
     * I18N key for the display text for this action
     * @return Display key
     */
    public String getDisplayKey() {
        return keyPrefix + name();
    }

}

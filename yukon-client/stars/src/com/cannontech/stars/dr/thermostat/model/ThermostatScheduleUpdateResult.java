package com.cannontech.stars.dr.thermostat.model;

/**
 * Enum which represents schedule update results
 */
public enum ThermostatScheduleUpdateResult {
    UNAVAILABLE_ERROR(true), 
    NO_SERIAL_ERROR(true), 
    UPDATE_SCHEDULE_ERROR(true), 
    MULTIPLE_ERROR(true), 
    UPDATE_SCHEDULE_SUCCESS(false), 
    SEND_SCHEDULE_ERROR(true), 
    SEND_SCHEDULE_SUCCESS(false),
    ;

    private boolean failed;

    private ThermostatScheduleUpdateResult(boolean failed) {
        this.failed = failed;
    }

    public boolean isFailed() {
        return failed;
    }

}
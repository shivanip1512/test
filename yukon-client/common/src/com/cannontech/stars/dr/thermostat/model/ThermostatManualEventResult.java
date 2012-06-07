package com.cannontech.stars.dr.thermostat.model;

/**
 * Enum which represents manual event execution results
 */
public enum ThermostatManualEventResult {
    UNAVAILABLE_ERROR(true), 
    NO_SERIAL_ERROR(true), 
    MANUAL_ERROR(true), 
    MANUAL_SUCCESS(false),
    MANUAL_PROGRAM_SUCCESS(false), 
    MULTIPLE_ERROR(true),
    MANUAL_INVALID_TEMP_HIGH(true),
    MANUAL_INVALID_TEMP_LOW(true);

    private boolean failed;

    private ThermostatManualEventResult(boolean failed) {
        this.failed = failed;
    }

    public boolean isFailed() {
        return failed;
    }
    
}
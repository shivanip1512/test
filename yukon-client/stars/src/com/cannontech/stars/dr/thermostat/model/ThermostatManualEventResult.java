package com.cannontech.stars.dr.thermostat.model;

/**
 * Enum which represents manual event execution results
 */
public enum ThermostatManualEventResult {
    OPERATOR_UNAVAILABLE_ERROR(true), 
    CONSUMER_MANUAL_ERROR(true), 
    CONSUMER_MANUAL_SUCCESS(false),
    CONSUMER_MANUAL_PROGRAM_SUCCESS(false), 
    CONSUMER_MULTIPLE_ERROR(true),
    OPERATOR_NO_SERIAL_ERROR(true), 
    CONSUMER_NO_SERIAL_ERROR(true),
    CONSUMER_MANUAL_INVALID_TEMP_HIGH(true),
    CONSUMER_MANUAL_INVALID_TEMP_LOW(true);

    // this key prefix can be found in the following file:
    // com.cannontech.yukon.dr.consumer.xml
    private final static String keyPrefix = "yukon.dr.consumer.manualevent.result.";

    private boolean failed;

    private ThermostatManualEventResult(boolean failed) {
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

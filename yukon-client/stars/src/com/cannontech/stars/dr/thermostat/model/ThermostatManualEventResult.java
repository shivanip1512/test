package com.cannontech.stars.dr.thermostat.model;

/**
 * Enum which represents manual event execution results
 */
public enum ThermostatManualEventResult {
    OPERATOR_UNAVAILABLE_ERROR, CONSUMER_MANUAL_ERROR, CONSUMER_MANUAL_SUCCESS;

    // this key prefix can be found in the following file:
    // com.cannontech.yukon.dr.consumer.xml
    private final static String keyPrefix = "yukon.dr.consumer.manualevent.result.";

    /**
     * I18N key for the display text for this action
     * @return Display key
     */
    public String getDisplayKey() {
        return keyPrefix + name();
    }

}

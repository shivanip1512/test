package com.cannontech.infrastructure.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * The types of infrastructure warnings that the system can calculate.
 */
public enum InfrastructureWarningType implements DisplayableEnum {
    GATEWAY_CONNECTION_STATUS("Gateway connection status"),
    GATEWAY_COLOR("Gateway color"),
    GATEWAY_FAILSAFE("Gateway failsafe"),
    GATEWAY_CONNECTED_NODES("Gateway connected nodes"),
    GATEWAY_DATA_STREAMING_LOAD("Gateway data streaming load"),
    GATEWAY_READY_NODES("Gateway ready nodes"),
    INFRASTRUCTURE_OUTAGE("Infrastructure outage"),
    CELLULAR_DEVICE_CONNECTION_STATUS("Cellular Device Connection Status"),
    CELLULAR_RELAY_DESCENDANT_COUNT("Cellular Relay Descendant Count"),
    CELLULAR_METER_DESCENDANT_COUNT("Cellular Meter Descendant Count"),
    CCU_COMM_STATUS("CCU comm status"),
    REPEATER_COMM_STATUS("Repeater comm status"),
    GATEWAY_SECURITY_ALARM("Gateway security alarm"),
    GATEWAY_POWER_FAILURE("Gateway power failure"),
    GATEWAY_RADIO_FAILURE("Gateway radio failure"),
    GATEWAY_TIME_SYNC_FAILED("Gateway time sync failed"),
    GATEWAY_DOOR_OPEN("Gateway door open"),
    GATEWAY_NODE_COUNT_EXCEEDED("Gateway node count exceeded"),
    GATEWAY_UPS_BATTERY_VOLTAGE_LOW("Gateway UPS battery voltage low"),
    GATEWAY_CERT_EXPIRATION("Gateway certificate expiration"),
    GATEWAY_HIGH_DISK_USAGE("Gateway high disk usage"),
    GATEWAY_RTC_BATTERY_FAILURE("Gateway RTC battery failure"),
    GATEWAY_AC_POWER_FAILURE("Gateway AC power failure"),
    ;
    
    private static final String keyBase = "yukon.web.widgets.infrastructureWarnings.warningType.";
    private String warningTypeDescription;
    
    InfrastructureWarningType(String warningTypeDescription) {
        this.warningTypeDescription = warningTypeDescription;
    }

    @Override
    public String getFormatKey() {
        return keyBase + name();
    }
    
    @Override
    public String toString() {
        return warningTypeDescription;
    }
}

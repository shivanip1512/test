package com.cannontech.infrastructure.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * The types of infrastructure warnings that the system can calculate.
 */
public enum InfrastructureWarningType implements DisplayableEnum {
    GATEWAY_CONNECTION_STATUS,
    GATEWAY_COLOR,
    GATEWAY_FAILSAFE,
    GATEWAY_CONNECTED_NODES,
    GATEWAY_DATA_STREAMING_LOAD,
    GATEWAY_READY_NODES,
    RELAY_OUTAGE,
    CCU_COMM_STATUS,
    REPEATER_COMM_STATUS,
    ;
    
    private static final String keyBase = "yukon.web.widgets.infrastructureWarnings.warningType.";
    
    @Override
    public String getFormatKey() {
        return keyBase + name();
    }
}

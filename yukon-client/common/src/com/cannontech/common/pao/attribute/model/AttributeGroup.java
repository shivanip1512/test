package com.cannontech.common.pao.attribute.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AttributeGroup implements DisplayableEnum {
    // Data/readings attribute groups.
    BLINK_AND_OUTAGE,
    CAPCONTROL,
    CURRENT,
    CUSTOM,
    DEMAND,
    ITRON,
    OTHER,
    PROFILE,
    REACTIVE,
    RELAY,
    STATUS,
    USAGE,
    VOLTAGE,
    SYSTEM,
    
    //RFN event attribute groups
    RFN_CURRENT_EVENT,
    RFN_DEMAND_EVENT,
    RFN_HARDWARE_EVENT,
    RFN_METERING_EVENT,
    RFN_OTHER_EVENT,
    RFN_SOFTWARE_EVENT,
    RFN_VOLTAGE_EVENT,
    
    GATEWAY_STATISTICS,

    // Demand Response
    DEMAND_RESPONSE,
    ESTIMATED_LOAD,

    ;
    
    private static final String groupPrefix = "yukon.common.attributeGroups.";
    
    private AttributeGroup() { }

    @Override
    public String getFormatKey() {
        return groupPrefix + name();
    }
}

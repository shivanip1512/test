package com.cannontech.common.util.jms.api;

/**
 * Loose categories of Jms messaging in Yukon. These don't affect functionality, they're just for organizing the
 * display.
 */
public enum JmsApiCategory {
    DATA_STREAMING,
    DIGI_ZIGBEE,
    MONITOR,
    OTHER, //use for misc single apis that don't fit in other existing categories
    RFN_LCR,
    RFN_METER,
    RF_GATEWAY,
    RF_MISC, //use for misc single apis that are RF-related
    RF_NETWORK,
    SMART_NOTIFICATION,
    WIDGET_REFRESH,
    ;
}
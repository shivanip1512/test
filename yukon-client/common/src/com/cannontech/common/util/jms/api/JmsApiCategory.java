package com.cannontech.common.util.jms.api;

/**
 * Loose categories of Jms messaging in Yukon. These don't affect functionality, they're just for organizing the
 * display.
 */
public enum JmsApiCategory {
    DATA_STREAMING("Data Streaming"),
    DIGI_ZIGBEE("Digi Zigbee"),
    MONITOR("Monitor"),
    OTHER("Other"), //use for misc single apis that don't fit in other existing categories
    RFN_LCR("RFN LCR"),
    RFN_METER("RFN Meter"),
    RF_GATEWAY("RF Gateway"),
    RF_MISC("RF Misc."), //use for misc single apis that are RF-related
    RF_NETWORK("RF Network"),
    SMART_NOTIFICATION("Smart Notification"),
    WIDGET_REFRESH("Widget Refresh"),
    ;
    
    private final String niceString;
    
    private JmsApiCategory(String niceString) {
        this.niceString = niceString;
    }
    
    @Override
    public String toString() {
        return niceString;
    }
}
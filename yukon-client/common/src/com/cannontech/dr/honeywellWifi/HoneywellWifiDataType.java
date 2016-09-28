package com.cannontech.dr.honeywellWifi;

/**
 * The types of message Yukon can receive from the Honeywell Azure service bus.
 */
public enum HoneywellWifiDataType {
    UI_DATA_BASIC_EVENT,
    DEMAND_RESPONSE_EVENT,
    EQUIPMENT_STATUS_EVENT;
}

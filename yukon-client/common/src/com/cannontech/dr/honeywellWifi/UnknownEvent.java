package com.cannontech.dr.honeywellWifi;

/**
 * This object is for handling Honeywell Azure service bus events that Yukon doesn't know how to parse.
 */
public class UnknownEvent extends HoneywellWifiDataBase {

    @Override
    public HoneywellWifiDataType getType() {
        return HoneywellWifiDataType.UNKNOWN;
    }

}

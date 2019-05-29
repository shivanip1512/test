package com.cannontech.dr.honeywellWifi.azure.event;

import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This object is for handling Honeywell Azure service bus events that Yukon doesn't know how to parse.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UnknownEvent extends AbstractHoneywellWifiData {

    @Override
    public HoneywellWifiDataType getType() {
        return HoneywellWifiDataType.UNKNOWN;
    }

    @Override
    public String getMacId() {
        throw new MethodNotImplementedException();
    }
}

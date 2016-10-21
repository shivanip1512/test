package com.cannontech.dr.honeywellWifi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Honeywell Azure service bus demand response event. Contains info about what the device is doing in a DR event.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DemandResponseEvent extends HoneywellWifiDataBase {
    
    @JsonCreator
    public DemandResponseEvent() {
        //TODO add fields, getters, necessary serializers/deserializers
    }
    
    @Override
    public HoneywellWifiDataType getType() {
        return HoneywellWifiDataType.DEMAND_RESPONSE_EVENT;
    }

}

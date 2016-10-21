package com.cannontech.dr.honeywellWifi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class EquipmentStatusEvent extends HoneywellWifiDataBase {
    
    @JsonCreator
    public EquipmentStatusEvent() {
        //TODO add fields, getters, necessary serializers/deserializers
    }
    
    @Override
    public HoneywellWifiDataType getType() {
        return HoneywellWifiDataType.EQUIPMENT_STATUS_EVENT;
    }

}

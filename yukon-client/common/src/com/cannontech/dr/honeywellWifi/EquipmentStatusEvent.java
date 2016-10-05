package com.cannontech.dr.honeywellWifi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;

@JsonIgnoreProperties(ignoreUnknown=true)
public class EquipmentStatusEvent implements HoneywellWifiData {
    private BrokeredMessage originalMessage;
    
    @JsonCreator
    public EquipmentStatusEvent() {
        //TODO add fields, getters, necessary serializers/deserializers
    }
    
    @Override
    public BrokeredMessage getOriginalMessage() {
        return originalMessage;
    }
    
    @Override
    public void setOriginalMessage(BrokeredMessage originalMessage) {
        this.originalMessage = originalMessage;
    }
    
    @Override
    public HoneywellWifiDataType getType() {
        return HoneywellWifiDataType.EQUIPMENT_STATUS_EVENT;
    }

}

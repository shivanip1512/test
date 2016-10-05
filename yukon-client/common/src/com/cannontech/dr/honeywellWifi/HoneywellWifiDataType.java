package com.cannontech.dr.honeywellWifi;

import java.util.Arrays;
import java.util.Optional;

/**
 * The types of message Yukon can receive from the Honeywell Azure service bus.
 */
public enum HoneywellWifiDataType {
    UI_DATA_BASIC_EVENT("UIDataBasicEvent", UiDataBasicEvent.class),
    DEMAND_RESPONSE_EVENT("DemandResponseEvent", DemandResponseEvent.class),
    EQUIPMENT_STATUS_EVENT("EquipmentStatusEvent", EquipmentStatusEvent.class);
    
    private String jsonString;
    private Class<? extends HoneywellWifiData> messageClass;
    
    private HoneywellWifiDataType(String jsonString, Class<? extends HoneywellWifiData> messageClass) {
        this.jsonString = jsonString;
        this.messageClass = messageClass;
    }
    
    public String getJsonString() {
        return jsonString;
    }
    
    public Class<? extends HoneywellWifiData> getMessageClass() {
        return messageClass;
    }
    
    public static Optional<HoneywellWifiDataType> forJsonString(String stringType) {
        return Arrays.stream(values())
                     .filter(type -> type.getJsonString().equalsIgnoreCase(stringType))
                     .findFirst();
    }
}

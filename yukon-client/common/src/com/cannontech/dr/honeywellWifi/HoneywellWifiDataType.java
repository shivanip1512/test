package com.cannontech.dr.honeywellWifi;

import java.util.Arrays;
import java.util.Optional;

import com.cannontech.dr.honeywellWifi.azure.event.ApplicationAccessAddedEvent;
import com.cannontech.dr.honeywellWifi.azure.event.ApplicationAccessRemovedEvent;
import com.cannontech.dr.honeywellWifi.azure.event.ConnectionStatusEvent;
import com.cannontech.dr.honeywellWifi.azure.event.DemandResponseEvent;
import com.cannontech.dr.honeywellWifi.azure.event.EquipmentStatusEvent;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;
import com.cannontech.dr.honeywellWifi.azure.event.UiDataBasicEvent;
import com.cannontech.dr.honeywellWifi.azure.event.UnknownEvent;

/**
 * The types of message Yukon can receive from the Honeywell Azure service bus.
 */
public enum HoneywellWifiDataType {
    UI_DATA_BASIC_EVENT("UIDataBasicEvent", UiDataBasicEvent.class),
    DEMAND_RESPONSE_EVENT("DemandResponseEvent", DemandResponseEvent.class),
    EQUIPMENT_STATUS_EVENT("EquipmentStatusEvent", EquipmentStatusEvent.class),
    CONNECTION_STATUS_EVENT("ConnectionStatusEvent", ConnectionStatusEvent.class),
    APPLICATION_ACCESS_ADDED_EVENT("ApplicationAccessAddedEvent", ApplicationAccessAddedEvent.class),
    APPLICATION_ACCESS_REMOVED_EVENT("ApplicationAccessRemovedEvent", ApplicationAccessRemovedEvent.class),
    UNKNOWN("", UnknownEvent.class);
    
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

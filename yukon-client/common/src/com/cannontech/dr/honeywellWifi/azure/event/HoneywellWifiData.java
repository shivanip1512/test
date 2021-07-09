package com.cannontech.dr.honeywellWifi.azure.event;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;


/**
 * Interface for all data messages Yukon can receive from the Honeywell Azure service bus.
 */
public interface HoneywellWifiData {
    
    /**
     * @return The original message as received from Azure.
     */
    ServiceBusReceivedMessage getOriginalMessage();
    
    /**
     * @return The "wrapper" metadata that came with the message.
     */
    HoneywellWifiMessageWrapper getMessageWrapper();
    
    void setMessageWrapper(HoneywellWifiMessageWrapper wrapper);
    
    HoneywellWifiDataType getType();

    String getMacId();

    void setOriginalMessage(ServiceBusReceivedMessage message);
}

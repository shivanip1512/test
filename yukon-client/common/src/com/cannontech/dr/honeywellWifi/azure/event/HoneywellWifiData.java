package com.cannontech.dr.honeywellWifi.azure.event;

import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;

/**
 * Interface for all data messages Yukon can receive from the Honeywell Azure service bus.
 */
public interface HoneywellWifiData {
    
    /**
     * @return The original message as received from Azure.
     */
    BrokeredMessage getOriginalMessage();
    
    void setOriginalMessage(BrokeredMessage message);
    
    /**
     * @return The "wrapper" metadata that came with the message.
     */
    HoneywellWifiMessageWrapper getMessageWrapper();
    
    void setMessageWrapper(HoneywellWifiMessageWrapper wrapper);
    
    HoneywellWifiDataType getType();
}

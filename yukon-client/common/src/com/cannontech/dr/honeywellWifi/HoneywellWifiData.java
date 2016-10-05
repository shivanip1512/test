package com.cannontech.dr.honeywellWifi;

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
    
    HoneywellWifiDataType getType();
}

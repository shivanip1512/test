package com.cannontech.dr.honeywellWifi.azure.event;

import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.Message;


/**
 * Interface for all data messages Yukon can receive from the Honeywell Azure service bus.
 */
public interface HoneywellWifiData {
    
    /**
     * @return The original message as received from Azure.
     */
     IMessage getOriginalMessage();
    
    /**
     * @return The "wrapper" metadata that came with the message.
     */
    HoneywellWifiMessageWrapper getMessageWrapper();
    
    void setMessageWrapper(HoneywellWifiMessageWrapper wrapper);
    
    HoneywellWifiDataType getType();

    String getMacId();

    void setOriginalMessage(IMessage message);
}

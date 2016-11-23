package com.cannontech.dr.honeywellWifi;

import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiMessageWrapper;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;

public abstract class AbstractHoneywellWifiData implements HoneywellWifiData {
    private BrokeredMessage originalMessage;
    private HoneywellWifiMessageWrapper messageWrapper;
    
    @Override
    public BrokeredMessage getOriginalMessage() {
        return originalMessage;
    }

    @Override
    public void setOriginalMessage(BrokeredMessage message) {
        originalMessage = message;
    }
    
    @Override
    public HoneywellWifiMessageWrapper getMessageWrapper() {
        return messageWrapper;
    }
    
    @Override
    public void setMessageWrapper(HoneywellWifiMessageWrapper wrapper) {
        messageWrapper = wrapper;
    }

}

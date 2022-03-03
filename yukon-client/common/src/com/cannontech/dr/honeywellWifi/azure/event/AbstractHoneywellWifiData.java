package com.cannontech.dr.honeywellWifi.azure.event;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;

public abstract class AbstractHoneywellWifiData implements HoneywellWifiData {
    private ServiceBusReceivedMessage originalMessage;
    private HoneywellWifiMessageWrapper messageWrapper;
    
    @Override
    public ServiceBusReceivedMessage getOriginalMessage() {
        return originalMessage;
    }

    @Override
    public void setOriginalMessage(ServiceBusReceivedMessage message) {
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

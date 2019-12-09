package com.cannontech.dr.honeywellWifi.azure.event;

import com.microsoft.azure.servicebus.IMessage;


public abstract class AbstractHoneywellWifiData implements HoneywellWifiData {
    private IMessage originalMessage;
    private HoneywellWifiMessageWrapper messageWrapper;
    
    @Override
    public IMessage getOriginalMessage() {
        return originalMessage;
    }

    @Override
    public void setOriginalMessage(IMessage message) {
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

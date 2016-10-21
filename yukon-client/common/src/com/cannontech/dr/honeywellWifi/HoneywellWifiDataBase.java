package com.cannontech.dr.honeywellWifi;

import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;

public abstract class HoneywellWifiDataBase implements HoneywellWifiData {
    private BrokeredMessage originalMessage;
    
    @Override
    public BrokeredMessage getOriginalMessage() {
        return originalMessage;
    }

    @Override
    public void setOriginalMessage(BrokeredMessage message) {
        originalMessage = message;
    }

}

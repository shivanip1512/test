package com.cannontech.notif.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.CurtailmentEventMessage;
import com.cannontech.notif.server.NotifServerConnection;

public class CurtailmentEventMessageHandler implements MessageHandler<CurtailmentEventMessage> {
    
    private @Autowired CurtailmentEventScheduler curtailmentEventScheduler;

    @Override
    public Class<CurtailmentEventMessage> getSupportedMessageType() {
        return CurtailmentEventMessage.class;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection, BaseMessage message) {
            CurtailmentEventMessage msg = (CurtailmentEventMessage) message;
            curtailmentEventScheduler.handleCurtailmentMessage(msg);
    }
}

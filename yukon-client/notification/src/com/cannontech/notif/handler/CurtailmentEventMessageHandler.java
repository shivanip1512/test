package com.cannontech.notif.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.message.notif.CurtailmentEventMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.server.NotifServerConnection;

public class CurtailmentEventMessageHandler implements MessageHandler<CurtailmentEventMsg> {
    
    private @Autowired CurtailmentEventScheduler curtailmentEventScheduler;

    @Override
    public Class<CurtailmentEventMsg> getSupportedMessageType() {
        return CurtailmentEventMsg.class;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection, Message message) {
            CurtailmentEventMsg msg = (CurtailmentEventMsg) message;
            curtailmentEventScheduler.handleCurtailmentMessage(msg);
    }
}

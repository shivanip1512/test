package com.cannontech.notif.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.BooleanData;
import com.cannontech.messaging.message.notif.CurtailmentEventDeleteMessage;
import com.cannontech.messaging.message.server.ServerRequestMessage;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.notif.server.NotifServerConnection;

public class CurtailmentEventDeleteMessageHandler implements MessageHandler<CurtailmentEventDeleteMessage> {

    private @Autowired CurtailmentEventDao curtailmentEventDao;
    private @Autowired CurtailmentEventScheduler curtailmentEventScheduler;
    
    @Override
    public Class<CurtailmentEventDeleteMessage> getSupportedMessageType() {
        return CurtailmentEventDeleteMessage.class;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection, BaseMessage message) {
        ServerRequestMessage reqMsg = (ServerRequestMessage) message;
        CurtailmentEventDeleteMessage reqPayload = (CurtailmentEventDeleteMessage) reqMsg.getPayload();
        
        Integer curtailmentEventId = reqPayload.getCurtailmentEventId();
        
        CurtailmentEvent event = curtailmentEventDao.getForId(curtailmentEventId);
        boolean success = curtailmentEventScheduler.deleteEventNotification(event, 
                                                             reqPayload.isDeleteStart(), 
                                                             reqPayload.isDeleteStop());
        
        BooleanData respPayload = new BooleanData(success);
        
        ServerResponseMessage responseMsg = reqMsg.createResponseMsg();
        responseMsg.setPayload(respPayload);
        responseMsg.setStatus(ServerResponseMessage.STATUS_OK);
        connection.write(responseMsg);
    }

}

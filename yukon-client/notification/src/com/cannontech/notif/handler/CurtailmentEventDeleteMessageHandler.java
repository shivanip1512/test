package com.cannontech.notif.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.message.notif.CurtailmentEventDeleteMsg;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.*;
import com.cannontech.notif.server.NotifServerConnection;

public class CurtailmentEventDeleteMessageHandler implements MessageHandler<CurtailmentEventDeleteMsg> {

    private @Autowired CurtailmentEventDao curtailmentEventDao;
    private @Autowired CurtailmentEventScheduler curtailmentEventScheduler;
    
    @Override
    public Class<CurtailmentEventDeleteMsg> getSupportedMessageType() {
        return CurtailmentEventDeleteMsg.class;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection, Message message) {
        ServerRequestMsg reqMsg = (ServerRequestMsg) message;
        CurtailmentEventDeleteMsg reqPayload = (CurtailmentEventDeleteMsg) reqMsg.getPayload();
        
        Integer curtailmentEventId = reqPayload.curtailmentEventId;
        
        CurtailmentEvent event = curtailmentEventDao.getForId(curtailmentEventId);
        boolean success = curtailmentEventScheduler.deleteEventNotification(event, 
                                                             reqPayload.deleteStart, 
                                                             reqPayload.deleteStop);
        
        CollectableBoolean respPayload = new CollectableBoolean(success);
        
        ServerResponseMsg responseMsg = reqMsg.createResponseMsg();
        responseMsg.setPayload(respPayload);
        responseMsg.setStatus(ServerResponseMsg.STATUS_OK);
        connection.write(responseMsg);
    }

}

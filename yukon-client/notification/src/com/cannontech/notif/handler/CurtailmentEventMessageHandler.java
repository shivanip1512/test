package com.cannontech.notif.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.message.notif.*;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.*;
import com.cannontech.notif.server.NotifServerConnection;

public class CurtailmentEventMessageHandler implements MessageHandler {
    
    private @Autowired CurtailmentEventScheduler curtailmentEventScheduler;
    private @Autowired CurtailmentEventDao curtailmentEventDao;

    @Override
    public boolean supportsMessageType(Message message) {
        if (message instanceof CurtailmentEventMsg
            || ServerRequestHelper.isPayloadInstanceOf(message, CurtailmentEventDeleteMsg.class)) {
            
            return true;
        }
        return false;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection, Message msg_) {
        if (msg_ instanceof CurtailmentEventMsg) {
            CurtailmentEventMsg msg = (CurtailmentEventMsg) msg_;
            curtailmentEventScheduler.handleCurtailmentMessage(msg);
        } else if (ServerRequestHelper.isPayloadInstanceOf(msg_, CurtailmentEventDeleteMsg.class)) {
            ServerRequestMsg reqMsg = (ServerRequestMsg) msg_;
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
}

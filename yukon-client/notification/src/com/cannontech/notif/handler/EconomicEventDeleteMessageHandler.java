package com.cannontech.notif.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.BooleanData;
import com.cannontech.messaging.message.notif.EconomicEventDeleteMessage;
import com.cannontech.messaging.message.server.ServerRequestMessage;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.notif.server.NotifServerConnection;

public class EconomicEventDeleteMessageHandler implements MessageHandler<EconomicEventDeleteMessage> {

    private @Autowired EconomicEventScheduler economicEventScheduler;
    private @Autowired EconomicEventDao economicEventDao;
    
    @Override
    public void handleMessage(NotifServerConnection connection, BaseMessage message) {
        
        ServerRequestMessage reqMsg = (ServerRequestMessage) message;
        
        EconomicEventDeleteMessage reqPayload = (EconomicEventDeleteMessage) reqMsg.getPayload();
        Integer economicEventId = reqPayload.getEconomicEventId();
        EconomicEvent economicEvent =  economicEventDao.getForId(economicEventId);
        
        Boolean success = economicEventScheduler.deleteEventNotification(economicEvent, 
                                                             reqPayload.isDeleteStart(), 
                                                             reqPayload.isDeleteStop());
        
        BooleanData respPayload = new BooleanData(success);
        
        ServerResponseMessage responseMsg = reqMsg.createResponseMsg();
        responseMsg.setPayload(respPayload);
        responseMsg.setStatus(ServerResponseMessage.STATUS_OK);
        connection.write(responseMsg);
    }

    @Override
    public Class<EconomicEventDeleteMessage> getSupportedMessageType() {
        return EconomicEventDeleteMessage.class;
    }

}

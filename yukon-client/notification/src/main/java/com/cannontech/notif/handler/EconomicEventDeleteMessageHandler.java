package com.cannontech.notif.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.message.notif.EconomicEventDeleteMsg;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.CollectableBoolean;
import com.cannontech.message.util.Message;
import com.cannontech.notif.server.NotifServerConnection;

public class EconomicEventDeleteMessageHandler implements MessageHandler<EconomicEventDeleteMsg> {

    private @Autowired EconomicEventScheduler economicEventScheduler;
    private @Autowired EconomicEventDao economicEventDao;
    
    @Override
    public void handleMessage(NotifServerConnection connection, Message message) {
        
        ServerRequestMsg reqMsg = (ServerRequestMsg) message;
        
        EconomicEventDeleteMsg reqPayload = (EconomicEventDeleteMsg) reqMsg.getPayload();
        Integer economicEventId = reqPayload.economicEventId;
        EconomicEvent economicEvent =  economicEventDao.getForId(economicEventId);
        
        Boolean success = economicEventScheduler.deleteEventNotification(economicEvent, 
                                                             reqPayload.deleteStart, 
                                                             reqPayload.deleteStop);
        
        CollectableBoolean respPayload = new CollectableBoolean(success);
        
        ServerResponseMsg responseMsg = reqMsg.createResponseMsg();
        responseMsg.setPayload(respPayload);
        responseMsg.setStatus(ServerResponseMsg.STATUS_OK);
        connection.write(responseMsg);
    }

    @Override
    public Class<EconomicEventDeleteMsg> getSupportedMessageType() {
        return EconomicEventDeleteMsg.class;
    }

}

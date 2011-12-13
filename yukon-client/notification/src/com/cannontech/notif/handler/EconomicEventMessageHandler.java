package com.cannontech.notif.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.notif.EconomicEventDeleteMsg;
import com.cannontech.message.notif.EconomicEventMsg;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.*;
import com.cannontech.notif.server.NotifServerConnection;

public class EconomicEventMessageHandler implements MessageHandler {
    private @Autowired EconomicEventScheduler economicEventScheduler;
    private @Autowired EconomicEventDao economicEventDao;
 
    @Override
    public boolean supportsMessageType(Message message) {
        if (message instanceof EconomicEventMsg 
            || ServerRequestHelper.isPayloadInstanceOf(message, EconomicEventDeleteMsg.class)) {
            
            return true;
        }
        return false;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection, Message msg_) {
        if (msg_ instanceof EconomicEventMsg) {
            EconomicEventMsg msg = (EconomicEventMsg) msg_;
            handleEconomicMessage(msg);

        } else if (ServerRequestHelper.isPayloadInstanceOf(msg_, EconomicEventDeleteMsg.class)) {
            ServerRequestMsg reqMsg = (ServerRequestMsg) msg_;
            ServerResponseMsg responseMsg = handleEventDeleteMessage(reqMsg);
            connection.write(responseMsg);
        }
    }

    private ServerResponseMsg handleEventDeleteMessage(ServerRequestMsg reqMsg) {
        EconomicEventDeleteMsg reqPayload = (EconomicEventDeleteMsg) reqMsg.getPayload();
        Integer economicEventId = reqPayload.economicEventId;
        final EconomicEvent economicEvent = 
            economicEventDao.getForId(economicEventId);
        Boolean success = economicEventScheduler.deleteEventNotification(economicEvent, 
                                                             reqPayload.deleteStart, 
                                                             reqPayload.deleteStop);
        CollectableBoolean respPayload = new CollectableBoolean(success);
        ServerResponseMsg responseMsg = reqMsg.createResponseMsg();
        responseMsg.setPayload(respPayload);
        responseMsg.setStatus(ServerResponseMsg.STATUS_OK);
        return responseMsg;
    }

    private void handleEconomicMessage(EconomicEventMsg msg) {
        final EconomicEvent economicEvent = 
            economicEventDao.getForId(msg.economicEventId);
        final EconomicEventPricing economicEventPricing = 
            economicEvent.getRevisions().get(msg.revisionNumber);
        
        switch (msg.action) {
        case STARTING:
            economicEventScheduler.eventCreationNotification(economicEvent);
            break;
        case CANCELING:
            economicEventScheduler.eventCancellationNotification(economicEvent);
            break;
        case REVISING:
            economicEventScheduler.eventRevisionNotification(economicEventPricing);
            break;
        case EXTENDING:
            economicEventScheduler.eventExtensionNotification(economicEvent);
            break;
        default:
            CTILogger.error("Unknown action: " + msg.action);
        }
    }    
}

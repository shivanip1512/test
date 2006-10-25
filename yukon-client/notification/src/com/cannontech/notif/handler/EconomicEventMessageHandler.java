package com.cannontech.notif.handler;

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

public class EconomicEventMessageHandler extends MessageHandler {
    private final EconomicEventScheduler _scheduler;
    private EconomicEventDao economicEventDao;
    
    public EconomicEventMessageHandler(EconomicEventScheduler scheduler) {
        _scheduler = scheduler;
    }

    public boolean handleMessage(NotifServerConnection connection, Message msg_) {
        if (msg_ instanceof EconomicEventMsg) {
            EconomicEventMsg msg = (EconomicEventMsg) msg_;
            handleEconomicMessage(msg);
            return true;
        } else if (ServerRequestHelper.isPayloadInstanceOf(msg_, EconomicEventDeleteMsg.class)) {
            ServerRequestMsg reqMsg = (ServerRequestMsg) msg_;
            ServerResponseMsg responseMsg = handleEventDeleteMessage(reqMsg);
            connection.write(responseMsg);
            return true;
        } else {
            return false;
        }
    }

    private ServerResponseMsg handleEventDeleteMessage(ServerRequestMsg reqMsg) {
        EconomicEventDeleteMsg reqPayload = (EconomicEventDeleteMsg) reqMsg.getPayload();
        Integer economicEventId = reqPayload.economicEventId;
        final EconomicEvent economicEvent = 
            economicEventDao.getForId(economicEventId);
        Boolean success = _scheduler.deleteEventNotification(economicEvent, 
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
            _scheduler.eventCreationNotification(economicEvent);
            break;
        case CANCELING:
            _scheduler.eventCancellationNotification(economicEvent);
            break;
        case REVISING:
            _scheduler.eventRevisionNotification(economicEventPricing);
            break;
        case EXTENDING:
            _scheduler.eventExtensionNotification(economicEvent);
            break;
        default:
            CTILogger.error("Unknown action: " + msg.action);
        }
    }

    public EconomicEventDao getEconomicEventDao() {
        return economicEventDao;
    }

    public void setEconomicEventDao(EconomicEventDao economicEventDao) {
        this.economicEventDao = economicEventDao;
    }
    
}

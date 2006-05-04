package com.cannontech.notif.handler;

import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.service.EconomicService;
import com.cannontech.message.notif.EconomicEventDeleteMsg;
import com.cannontech.message.notif.EconomicEventMsg;
import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.*;
import com.cannontech.notif.server.NotifServerConnection;

public class EconomicEventMessageHandler extends MessageHandler {
    private final EconomicEventScheduler _scheduler;
    private EconomicService economicService;
    
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
        Integer curtailmentEventId = reqPayload.economicEventId;
        final EconomicEvent economicEvent = 
            economicService.getEvent(curtailmentEventId);
        Boolean success = _scheduler.deleteEventNotification(economicEvent);
        CollectableBoolean respPayload = new CollectableBoolean(success);
        ServerResponseMsg responseMsg = reqMsg.createResponseMsg();
        responseMsg.setPayload(respPayload);
        responseMsg.setStatus(ServerResponseMsg.STATUS_OK);
        return responseMsg;
    }

    private void handleEconomicMessage(EconomicEventMsg msg) {
        final EconomicEvent economicEvent = 
            economicService.getEvent(msg.economicEventId);
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
        }
    }
    
    public EconomicService getEconomicService() {
        return economicService;
    }

    public void setEconomicService(EconomicService economicService) {
        this.economicService = economicService;
    }

}

package com.cannontech.notif.handler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.EconomicEventMessage;
import com.cannontech.notif.server.NotifServerConnection;

public class EconomicEventMessageHandler implements MessageHandler<EconomicEventMessage> {
    
    private static final Logger log = YukonLogManager.getLogger(CustomerEmailMessageHandler.class);
    
    private @Autowired EconomicEventScheduler economicEventScheduler;
    private @Autowired EconomicEventDao economicEventDao;
 
    @Override
    public Class<EconomicEventMessage> getSupportedMessageType() {
        return EconomicEventMessage.class;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection, BaseMessage message) {
        EconomicEventMessage msg = (EconomicEventMessage) message;

        final EconomicEvent economicEvent = economicEventDao.getForId(msg.getEconomicEventId());
            final EconomicEventPricing economicEventPricing = economicEvent.getRevisions().get(msg.getRevisionNumber());
            
            switch (msg.getAction()) {
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
                    log.error("Unknown action: " + msg.getAction());
            }
    }
}

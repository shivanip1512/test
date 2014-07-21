package com.cannontech.notif.handler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.notif.EconomicEventMsg;
import com.cannontech.message.util.Message;
import com.cannontech.notif.server.NotifServerConnection;

public class EconomicEventMessageHandler implements MessageHandler<EconomicEventMsg> {
    
    private static final Logger log = YukonLogManager.getLogger(CustomerEmailMessageHandler.class);
    
    private @Autowired EconomicEventScheduler economicEventScheduler;
    private @Autowired EconomicEventDao economicEventDao;
 
    @Override
    public Class<EconomicEventMsg> getSupportedMessageType() {
        return EconomicEventMsg.class;
    }
    
    @Override
    public void handleMessage(NotifServerConnection connection, Message message) {
        EconomicEventMsg msg = (EconomicEventMsg) message;

        final EconomicEvent economicEvent = economicEventDao.getForId(msg.economicEventId);
            final EconomicEventPricing economicEventPricing = economicEvent.getRevisions().get(msg.revisionNumber);
            
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
                    log.error("Unknown action: " + msg.action);
            }
    }
}

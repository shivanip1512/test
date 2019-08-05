package com.cannontech.common.smartNotification.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventMulti;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class SmartNotificationEventCreationServiceImpl implements SmartNotificationEventCreationService {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationEventCreationServiceImpl.class);
    private static final Map<SmartNotificationEventType, String> queues = ImmutableMap.of(
        SmartNotificationEventType.INFRASTRUCTURE_WARNING, 
        JmsApiDirectory.SMART_NOTIFICATION_INFRASTRUCTURE_WARNINGS_EVENT.getQueue().getName(),
        
        SmartNotificationEventType.DEVICE_DATA_MONITOR, 
        JmsApiDirectory.SMART_NOTIFICATION_DEVICE_DATA_MONITOR_EVENT.getQueue().getName(),
        
        SmartNotificationEventType.YUKON_WATCHDOG,
        JmsApiDirectory.SMART_NOTIFICATION_YUKON_WATCHDOG_EVENT.getQueue().getName(),
        
        SmartNotificationEventType.ASSET_IMPORT,
        JmsApiDirectory.SMART_NOTIFICATION_DATA_IMPORT_EVENT.getQueue().getName(),
        
        SmartNotificationEventType.METER_DR,
        JmsApiDirectory.METER_DR_EVENT.getQueue().getName()
    );
    
    private Executor executor = Executors.newCachedThreadPool();
    private JmsTemplate jmsTemplate;
    private SmartNotificationEventDao eventsDao;
    
    @Autowired
    public SmartNotificationEventCreationServiceImpl(ConnectionFactory connectionFactory, SmartNotificationEventDao eventsDao) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.setPubSubDomain(false);
        
        this.eventsDao = eventsDao;
    }
    
    @Override
    public void send(SmartNotificationEventType type, List<SmartNotificationEvent> events) {
        if (!events.isEmpty()) {
            executor.execute(() -> {
                try {
                    log.debug(type + " Saving Smart Notification events " + events.size());
                    eventsDao.save(type, events);
                    sendEvents(type, events);
                } catch (Exception e) {
                    log.error("Exception sending smart notification event", e);
                }
            });
        }
    }
    
    @Override
    public void send(SmartNotificationEventType type, SmartNotificationEvent event) {
        send(type, Lists.newArrayList(event));
    }
    
    private void sendEvents(SmartNotificationEventType type, List<SmartNotificationEvent> events) {
        if (!events.isEmpty()) {
            if (log.isTraceEnabled()) {
                for (SmartNotificationEvent event : events) {
                    log.trace(event);
                }
            }
            log.debug(type + " Sending Smart Notification events " + events.size());
            jmsTemplate.convertAndSend(queues.get(type), new SmartNotificationEventMulti(type, events));
        }
    }
}

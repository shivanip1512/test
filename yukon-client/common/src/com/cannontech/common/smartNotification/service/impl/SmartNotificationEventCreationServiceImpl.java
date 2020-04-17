package com.cannontech.common.smartNotification.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventMulti;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class SmartNotificationEventCreationServiceImpl implements SmartNotificationEventCreationService {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationEventCreationServiceImpl.class);
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private Map<SmartNotificationEventType, YukonJmsTemplate> queues;

    @PostConstruct
    public void init() {
        queues = ImmutableMap.of(
                SmartNotificationEventType.INFRASTRUCTURE_WARNING,
                jmsTemplateFactory.createTemplate(JmsApiDirectory.SMART_NOTIFICATION_INFRASTRUCTURE_WARNINGS_EVENT),

                SmartNotificationEventType.DEVICE_DATA_MONITOR,
                jmsTemplateFactory.createTemplate(JmsApiDirectory.SMART_NOTIFICATION_DEVICE_DATA_MONITOR_EVENT),

                SmartNotificationEventType.YUKON_WATCHDOG,
                jmsTemplateFactory.createTemplate(JmsApiDirectory.SMART_NOTIFICATION_YUKON_WATCHDOG_EVENT),

                SmartNotificationEventType.ASSET_IMPORT,
                jmsTemplateFactory.createTemplate(JmsApiDirectory.SMART_NOTIFICATION_DATA_IMPORT_EVENT),

                SmartNotificationEventType.METER_DR,
                jmsTemplateFactory.createTemplate(JmsApiDirectory.SMART_NOTIFICATION_METER_DR_EVENT));

    }

    private Executor executor = Executors.newCachedThreadPool();
    private SmartNotificationEventDao eventsDao;
    
    @Autowired
    public SmartNotificationEventCreationServiceImpl(SmartNotificationEventDao eventsDao) {
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
            queues.get(type).convertAndSend(new SmartNotificationEventMulti(type, events));
        }
    }
}

package com.cannontech.common.smartNotification.service.impl;

import java.util.List;
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
import com.google.common.collect.Lists;

public class SmartNotificationEventCreationServiceImpl implements SmartNotificationEventCreationService {
    private static Logger snLogger = YukonLogManager.getSmartNotificationsLogger(SmartNotificationEventCreationServiceImpl.class);
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    private YukonJmsTemplate template;


    @PostConstruct
    public void init() {
        template = jmsTemplateFactory.createTemplate(JmsApiDirectory.SMART_NOTIFICATION_EVENT);
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
                    eventsDao.save(type, events);
                    sendEvents(type, events);
                } catch (Exception e) {
                    snLogger.error("Exception sending smart notification event", e);
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
            SmartNotificationEventMulti msg = new SmartNotificationEventMulti(type, events);
            snLogger.info("Sending Smart Notification {}", type,
                    msg.loggingString(snLogger.getLevel()));
            template.convertAndSend(msg);
        }
    }
}

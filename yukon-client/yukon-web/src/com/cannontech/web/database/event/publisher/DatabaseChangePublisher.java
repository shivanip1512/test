package com.cannontech.web.database.event.publisher;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;

@Service
public class DatabaseChangePublisher {

    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    private YukonJmsTemplate jmsTemplate;

    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.DATABASE_CHANGE_EVENT_REQUEST);
        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            publish(event);
        });
    }

    private void publish(DatabaseChangeEvent event) {
        jmsTemplate.convertAndSend(event);
    }
}
package com.cannontech.broker;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.broker.message.request.LoggingDbChangeRequest;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;

@Service
public class LoggingDbChangePublisher {

    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    private YukonJmsTemplate jmsTemplate;

    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.LOGGING_DB_CHANGE_REQUEST);
    }

    public void publish(LoggingDbChangeRequest dbChangeRequest) {
        jmsTemplate.convertAndSend(dbChangeRequest);
    }

}

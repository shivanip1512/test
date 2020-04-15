package com.cannontech.common.util.jms;

import javax.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.jms.api.JmsApi;

/**
 * This class is used to create JmsTemplate Object.
 *
 */
@Service
public class YukonJmsTemplateFactory {
    @Autowired ConnectionFactory connectionFactory;

    public YukonJmsTemplate createTemplate(JmsApi<?, ?, ?> jmsApi) {
        YukonJmsTemplate jmsTemplate = new YukonJmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName(jmsApi.getQueueName());
        jmsTemplate.setPubSubDomain(jmsApi.isTopic());
        jmsTemplate.setTimeToLive(jmsApi.getTimeToLive().getMillis());
        return jmsTemplate;
    }

    public YukonJmsTemplate createTemplate(JmsApi<?, ?, ?> jmsApi, MessageConverter messageConverter) {
        YukonJmsTemplate jmsTemplate = new YukonJmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName(jmsApi.getQueueName());
        jmsTemplate.setPubSubDomain(jmsApi.isTopic());
        jmsTemplate.setTimeToLive(jmsApi.getTimeToLive().getMillis());
        jmsTemplate.setMessageConverter(messageConverter);
        return jmsTemplate;
    }

    public YukonJmsTemplate createResponseTemplate(JmsApi<?, ?, ?> jmsApi) {
        YukonJmsTemplate jmsTemplate = new YukonJmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName(jmsApi.getResponseQueueName());
        jmsTemplate.setPubSubDomain(jmsApi.isTopic());
        jmsTemplate.setTimeToLive(jmsApi.getTimeToLive().getMillis());
        return jmsTemplate;
    }
}

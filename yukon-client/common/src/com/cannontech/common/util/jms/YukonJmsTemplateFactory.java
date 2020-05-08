package com.cannontech.common.util.jms;

import javax.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.jms.api.JmsApi;

/**
 * This class is used to create YukonJmsTemplate Object.
 *
 */
@Service
public class YukonJmsTemplateFactory {
    @Autowired ConnectionFactory connectionFactory;

    /**
     * Create and return a YukonJmsTemplate object after setting defaultDestinationName, pubSubDomain and timeToLive.
     */
    public YukonJmsTemplate createTemplate(JmsApi<?, ?, ?> jmsApi) {
        YukonJmsTemplate jmsTemplate = new YukonJmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName(jmsApi.getQueueName());
        jmsTemplate.setPubSubDomain(jmsApi.isTopic());
        jmsTemplate.setTimeToLive(jmsApi.getTimeToLive().getMillis());
        return jmsTemplate;
    }

    /**
     * Create and return a YukonJmsTemplate object after setting defaultDestinationName, pubSubDomain, timeToLive and
     * messageConverter.
     */
    public YukonJmsTemplate createTemplate(JmsApi<?, ?, ?> jmsApi, MessageConverter messageConverter) {
        YukonJmsTemplate jmsTemplate = new YukonJmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName(jmsApi.getQueueName());
        jmsTemplate.setPubSubDomain(jmsApi.isTopic());
        jmsTemplate.setTimeToLive(jmsApi.getTimeToLive().getMillis());
        jmsTemplate.setMessageConverter(messageConverter);
        return jmsTemplate;
    }

    /**
     * Create and return a YukonJmsTemplate object after setting defaultDestinationName, pubSubDomain, timeToLive and
     * recieveTimeout.
     */
    public YukonJmsTemplate createTemplate(JmsApi<?, ?, ?> jmsApi, int recieveTimeout) {
        YukonJmsTemplate jmsTemplate = new YukonJmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName(jmsApi.getQueueName());
        jmsTemplate.setPubSubDomain(jmsApi.isTopic());
        jmsTemplate.setTimeToLive(jmsApi.getTimeToLive().getMillis());
        jmsTemplate.setReceiveTimeout(recieveTimeout);
        return jmsTemplate;
    }
    
    /**
     * Create and return a YukonJmsTemplate object after setting responseQueueName in defaultDestinationName, pubSubDomain and
     * timeToLive.
     */
    public YukonJmsTemplate createResponseTemplate(JmsApi<?, ?, ?> jmsApi) {
        YukonJmsTemplate jmsTemplate = new YukonJmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName(jmsApi.getResponseQueueName());
        jmsTemplate.setPubSubDomain(jmsApi.isTopic());
        jmsTemplate.setTimeToLive(jmsApi.getTimeToLive().getMillis());
        return jmsTemplate;
    }
}

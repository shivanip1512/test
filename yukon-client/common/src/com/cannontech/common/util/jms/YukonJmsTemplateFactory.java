package com.cannontech.common.util.jms;

import javax.jms.ConnectionFactory;

import org.joda.time.Duration;
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
        jmsTemplate.setCommsLogger(jmsApi.getCommsLogger());
        return jmsTemplate;
    }

    /**
     * Create and return a YukonJmsTemplate object after setting defaultDestinationName, pubSubDomain, timeToLive and
     * messageConverter.
     */
    public YukonJmsTemplate createTemplate(JmsApi<?, ?, ?> jmsApi, MessageConverter messageConverter) {
        var jmsTemplate = createTemplate(jmsApi);
        jmsTemplate.setMessageConverter(messageConverter);
        return jmsTemplate;
    }

    /**
     * Create and return a YukonJmsTemplate object after setting defaultDestinationName, pubSubDomain, timeToLive and
     * recieveTimeout.
     */
    public YukonJmsTemplate createTemplate(JmsApi<?, ?, ?> jmsApi, Duration recieveTimeout) {
        var jmsTemplate = createTemplate(jmsApi);
        jmsTemplate.setReceiveTimeout(recieveTimeout.getMillis());
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
        jmsTemplate.setCommsLogger(jmsApi.getCommsLogger());
        return jmsTemplate;
    }

    /**
     * Create and return a YukonJmsTemplate object after setting responseQueueName in defaultDestinationName, pubSubDomain,
     * timeToLive, and receiveTimeout.
     */
    public YukonJmsTemplate createResponseTemplate(JmsApi<?, ?, ?> jmsApi, Duration recieveTimeout) {
        var jmsTemplate = createResponseTemplate(jmsApi);
        jmsTemplate.setReceiveTimeout(recieveTimeout.getMillis());
        return jmsTemplate;
    }
}

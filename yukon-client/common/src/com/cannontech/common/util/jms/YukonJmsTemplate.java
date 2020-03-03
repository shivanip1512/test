package com.cannontech.common.util.jms;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.jms.support.converter.MessageConverter;
import com.cannontech.common.util.jms.api.JmsApi;

/**
 * YukonJmsTemplate is a class that simplifies receiving and sending of messages through JMS. Default settings for
 * YukonJmsTemplate Sessions are "TTL: 1 Day " and "DeliveryMode.NON_PERSISTENT".
 */

public class YukonJmsTemplate extends JmsTemplate {

    @Autowired
    public YukonJmsTemplate(ConnectionFactory connectionFactory) {
        super(connectionFactory);
        setTimeToLive(86400000);
        setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        setExplicitQosEnabled(true);
    }

    /**
     * This method set topic/queue flag and queueName from JmsApi and then send the message to the given destination.
     * 
     */
    public void convertAndSend(JmsApi<?, ?, ?> jmsApi, Object message) throws JmsException {
        setPubSubDomain(jmsApi.isTopic().booleanValue());
        convertAndSend(jmsApi.getQueue().getName(), message);
    }

    /**
     * This method send the message to the destination provided in JmsApi with specified Receive Timeout.
     * 
     */
    public void convertAndSend(JmsApi<?, ?, ?> jmsApi, Object message, long receiveTimeout) throws JmsException {
        setReceiveTimeout(receiveTimeout);
        convertAndSend(jmsApi, message);
    }

    /**
     * This method send the message to the destination provided in JmsApi after serializing the message by the specified
     * MessageConverter.
     * 
     */
    public void convertAndSend(JmsApi<?, ?, ?> jmsApi, Object message, MessageConverter messageConverter) throws JmsException {
        setMessageConverter(messageConverter);
        convertAndSend(jmsApi, message);
    }

    /**
     * This method set topic/queue flag and queueName from JmsApi, apply the given MessagePostProcessor to the message, and send
     * the resulting message to the destination.
     * 
     */
    public void convertAndSend(JmsApi<?, ?, ?> jmsApi, Object message, MessagePostProcessor postProcessor) throws JmsException {
        setPubSubDomain(jmsApi.isTopic().booleanValue());
        convertAndSend(jmsApi.getQueue().getName(), message, postProcessor);
    }
}

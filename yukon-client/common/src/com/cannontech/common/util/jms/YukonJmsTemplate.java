package com.cannontech.common.util.jms;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;

import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.jms.support.converter.MessageConverter;
import com.cannontech.common.util.jms.api.JmsApi;

/**
 * YukonJmsTemplate is a class that simplifies receiving and sending of messages through JMS. Default settings for
 * YukonJmsTemplate Sessions is "DeliveryMode.NON_PERSISTENT".
 */

public class YukonJmsTemplate extends JmsTemplate {

    @Autowired
    public YukonJmsTemplate(ConnectionFactory connectionFactory) {
        super(connectionFactory);
        setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        setExplicitQosEnabled(true);
    }

    /**
     * This method set topic/queue flag, time-to-live and queueName from JmsApi and then send the message to the given destination.
     * 
     */
    public void convertAndSend(JmsApi<?, ?, ?> jmsApi, Object message) throws JmsException {
        setPubSubDomain(jmsApi.isTopic());
        setTimeToLive(jmsApi.getTimeToLive().getMillis());
        convertAndSend(jmsApi.getQueue().getName(), message);
    }

    /**
     * This method send the message to the destination provided in JmsApi with specified Receive Timeout.
     * 
     */
    public void convertAndSend(JmsApi<?, ?, ?> jmsApi, Object message, Duration receiveTimeout) throws JmsException {
        setReceiveTimeout(receiveTimeout.getMillis());
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
     * This method set topic/queue flag, time-to-live and queueName from JmsApi, apply the given MessagePostProcessor to the message, and send
     * the resulting message to the destination.
     * 
     */
    public void convertAndSend(JmsApi<?, ?, ?> jmsApi, Object message, MessagePostProcessor postProcessor) throws JmsException {
        setPubSubDomain(jmsApi.isTopic());
        setTimeToLive(jmsApi.getTimeToLive().getMillis());
        convertAndSend(jmsApi.getQueue().getName(), message, postProcessor);
    }

    /**
     * This method send the message to the provided Destination.
     * 
     */
    public void convertAndSend(Destination destination, Object message) throws JmsException {
        super.convertAndSend(destination, message);
    }

    /**
     * This method send the message to the Destination with specified Receive Timeout.
     * 
     */
    public void convertAndSend(Destination destination, Object message, Duration receiveTimeout) throws JmsException {
        setReceiveTimeout(receiveTimeout.getMillis());
        convertAndSend(destination, message);
    }

    /**
     * This method set topic/queue flag, time-to-live and queueName from JmsApi, and send the acknowledgement message to the
     * destination defined in responseQueue.
     * 
     */
    public void convertAndSendToResponseQueue(JmsApi<?, ?, ?> jmsApi, Object message) throws JmsException {
        setPubSubDomain(jmsApi.isTopic());
        setTimeToLive(jmsApi.getTimeToLive().getMillis());
        convertAndSend(jmsApi.getResponseQueueName(), message);
    }

    /**
     * This method send the acknowledgement message to the destination defined in responseQueue with specified Receive Timeout.
     * 
     */
    public void convertAndSendToResponseQueue(JmsApi<?, ?, ?> jmsApi, Object message, Duration receiveTimeout)
            throws JmsException {
        setReceiveTimeout(receiveTimeout.getMillis());
        convertAndSendToResponseQueue(jmsApi, message);
    }

    /**
     * This method receive a message synchronously from the specified queueName, but only wait up to a specified time for
     * delivery. This method will be used when multiple Queue has same queueName.
     * 
     */
    public Object receive(String queueName, Duration receiveTimeout) throws JmsException {
        setReceiveTimeout(receiveTimeout.getMillis());
        return receive(queueName);
    }

    /**
     * This method receive a message synchronously from the specified queueName defined in JmsApi, but only wait up to a specified
     * time for delivery.
     * 
     */
    public Object receive(JmsApi<?, ?, ?> jmsApi, Duration receiveTimeout) throws JmsException {
        setReceiveTimeout(receiveTimeout.getMillis());
        return receive(jmsApi.getQueue().getName());
    }

}

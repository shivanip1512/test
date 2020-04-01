package com.cannontech.common.util.jms;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;

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

    public static final int receiveTimeoutInMillis = 1000;

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
     * This method send the message to the destination provided in JmsApi with default Receive Timeout of 1 second.
     * 
     */
    public void convertAndSendWithReceiveTimeout(JmsApi<?, ?, ?> jmsApi, Object message) throws JmsException {
        setReceiveTimeout(receiveTimeoutInMillis);
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
     * This method send the message to the Destination with default Receive Timeout of 1 second.
     * 
     */
    public void convertAndSendWithReceiveTimeout(Destination destination, Object message) throws JmsException {
        setReceiveTimeout(receiveTimeoutInMillis);
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
     * This method send the acknowledgement message to the destination defined in responseQueue with default Receive Timeout of 1
     * second.
     * 
     */
    public void convertAndSendToResponseQueueWithReceiveTimeout(JmsApi<?, ?, ?> jmsApi, Object message)
            throws JmsException {
        setReceiveTimeout(receiveTimeoutInMillis);
        convertAndSendToResponseQueue(jmsApi, message);
    }

    /**
     * This method receive a message synchronously from the specified queueName defined in JmsApi, but only wait up to 1 second
     * time for delivery.
     * 
     */
    public Object receive(JmsApi<?, ?, ?> jmsApi) throws JmsException {
        setReceiveTimeout(receiveTimeoutInMillis);
        return receive(jmsApi.getQueue().getName());
    }

}

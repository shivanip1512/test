package com.cannontech.common.util.jms;

import javax.jms.Destination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.support.converter.MessageConverter;

import com.cannontech.common.util.jms.api.JmsApi;

/**
 * YukonJmsTemplate is a class that simplifies receiving and sending of messages through JMS.
 * 
 */

public class YukonJmsTemplate {

    @Autowired JmsTemplateFactory jmsTemplateFactory;

    /**
     * This method send the message to the destination provided in JmsApi.
     * 
     */
    public void convertAndSend(JmsApi<?, ?, ?> jmsApi, Object message) throws JmsException {
        JmsTemplate jmsTemplate = jmsTemplateFactory.createJmsTemplate(jmsApi);
        jmsTemplate.convertAndSend(jmsApi.getQueue().getName(), message);
    }

    /**
     * This method send the message to the destination provided in JmsApi after serializing the message by the specified
     * MessageConverter.
     * 
     */
    public void convertAndSend(JmsApi<?, ?, ?> jmsApi, Object message, MessageConverter messageConverter) throws JmsException {
        JmsTemplate jmsTemplate = jmsTemplateFactory.createJmsTemplate(jmsApi, messageConverter);
        jmsTemplate.convertAndSend(jmsApi.getQueue().getName(), message);
    }

    /**
     * This method apply the given MessagePostProcessor to the message, and send the resulting message to the destination.
     * 
     */
    public void convertAndSend(JmsApi<?, ?, ?> jmsApi, Object message, MessagePostProcessor postProcessor) throws JmsException {
        JmsTemplate jmsTemplate = jmsTemplateFactory.createJmsTemplate(jmsApi);
        jmsTemplate.convertAndSend(jmsApi.getQueue().getName(), message, postProcessor);
    }

    /**
     * This method send the message to the provided Destination.
     * 
     */
    public void convertAndSend(Destination destination, Object message) throws JmsException {
        JmsTemplate jmsTemplate = jmsTemplateFactory.createJmsTemplate();
        jmsTemplate.convertAndSend(destination, message);
    }

    /**
     * This method send the acknowledgement message to the destination defined in responseQueue.
     * 
     */
    public void convertAndSendToResponseQueue(JmsApi<?, ?, ?> jmsApi, Object message) throws JmsException {
        JmsTemplate jmsTemplate = jmsTemplateFactory.createJmsTemplate(jmsApi);
        jmsTemplate.convertAndSend(jmsApi.getResponseQueueName(), message);
    }

    /**
     * This method receive a message synchronously from the specified queueName defined in JmsApi, but only wait up to 1 second
     * time for delivery.
     * 
     */
    public Object receive(JmsApi<?, ?, ?> jmsApi) throws JmsException {
        JmsTemplate jmsTemplate = jmsTemplateFactory.createJmsTemplate(jmsApi);
        jmsTemplate.setReceiveTimeout(1000);
        return jmsTemplate.receive(jmsApi.getQueue().getName());
    }

    /**
     * This method apply the given MessagePostProcessor to the message, and send the resulting message to the destination.
     * 
     */
    public void convertAndSend(Destination destination, Object message, MessagePostProcessor postProcessor) {
        JmsTemplate jmsTemplate = jmsTemplateFactory.createJmsTemplate();
        jmsTemplate.convertAndSend(destination, message, postProcessor);
    }

    /**
     * This method execute the action specified by the given action object within a JMS Session.
     */
    public void execute(SessionCallback<?> action, boolean startConnection) {
        JmsTemplate jmsTemplate = jmsTemplateFactory.createJmsTemplate();
        jmsTemplate.execute(action, startConnection);
    }
}

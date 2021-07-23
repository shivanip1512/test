package com.cannontech.common.util.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.joda.time.Duration;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.cannontech.common.config.ConfigurationSource;

public class RequestReplyTemplateImpl<R extends Serializable> extends RequestReplyTemplateBase<JmsReplyHandler<R>> implements RequestReplyTemplate<R> {

    public RequestReplyTemplateImpl(String configurationName, ConfigurationSource configurationSource,
            YukonJmsTemplate jmsTemplate, boolean isInternalMessage) {
        super(configurationName, configurationSource, jmsTemplate, isInternalMessage);
    }
    
    public RequestReplyTemplateImpl(String configurationName, ConfigurationSource configurationSource,
            YukonJmsTemplate jmsTemplate) {
        super(configurationName, configurationSource, jmsTemplate, false);
    }

    @Override
    protected <Q extends Serializable> void doJmsWork(Session session,
            final Q requestPayload, JmsReplyHandler<R> callback) throws JMSException {
        final Duration replyTimeout =
                configurationSource.getDuration(configurationName + "_REPLY_TIMEOUT", Duration.standardMinutes(1));

        DynamicDestinationResolver resolver = new DynamicDestinationResolver();
        MessageProducer producer = session.createProducer(
                resolver.resolveDestinationName(session, jmsTemplate.getDefaultDestinationName(), jmsTemplate.isPubSubDomain()));

        TemporaryQueue replyQueue = session.createTemporaryQueue();
        MessageConsumer replyConsumer = session.createConsumer(replyQueue);
        
        ObjectMessage requestMessage = session.createObjectMessage(requestPayload);
        
        requestMessage.setJMSReplyTo(replyQueue);
        log.trace("Sending requestMessage to producer: {}", requestMessage.toString());
        logRequest(requestPayload.toString());
        sendMessage(producer, requestMessage);
        
        handleReplyOrTimeout(callback, replyTimeout, replyConsumer, requestPayload.toString());
        log.trace("Request replied or timed out: {}", requestMessage.toString());
        
        replyConsumer.close();
        replyQueue.delete();
        
        /**
         * This code doesn't seem to delete the queue if the message was send to the replyToQueue
         * (jmsTemplate.convertAndSend(message.getJMSReplyTo(),.....)
         *  
         * If you plan to send the reply message to temp queue add this code. Put break on first System.out. and observe temp queues
         * 
         * System.out.println("Before delete");
         * replyQueue.delete();
         * System.out.println("After delete");
         * 
         */
    }

    private void handleReplyOrTimeout(JmsReplyHandler<R> callback, final Duration replyTimeout,
            MessageConsumer replyConsumer, String requestPayload) throws JMSException {
        // Block for status response or until timeout.
        Message reply = replyConsumer.receive(replyTimeout.getMillis());

        if (reply == null) {
            logReply(requestPayload, "NULL");
            callback.handleTimeout();
            return;
        }

        R reply1Payload = JmsHelper.extractObject(reply, callback.getExpectedType());
        logReply(requestPayload, reply1Payload.toString());
        callback.handleReply(reply1Payload);
    }
}

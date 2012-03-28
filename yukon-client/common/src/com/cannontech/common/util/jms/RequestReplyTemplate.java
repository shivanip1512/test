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

public class RequestReplyTemplate<R extends Serializable> extends RequestReplyTemplateBase<JmsReplyHandler<R>> {
    @Override
    protected <Q extends Serializable> void doJmsWork(Session session,
        final Q requestPayload, JmsReplyHandler<R> callback) throws JMSException {
        final Duration replyTimeout =
                configurationSource.getDuration(configurationName + "_REPLY_TIMEOUT", Duration.standardMinutes(1));

        DynamicDestinationResolver resolver = new DynamicDestinationResolver();
        MessageProducer producer =
                session.createProducer(resolver.resolveDestinationName(session, requestQueueName, pubSubDomain));
        
        TemporaryQueue replyQueue = session.createTemporaryQueue();
        MessageConsumer replyConsumer = session.createConsumer(replyQueue);
        
        ObjectMessage requestMessage = session.createObjectMessage(requestPayload);
        
        requestMessage.setJMSReplyTo(replyQueue);
        producer.send(requestMessage);
        
        // Block for status response or until timeout.
        Message reply = replyConsumer.receive(replyTimeout.getMillis());
        
        if (reply == null) {
            callback.handleTimeout();
            return;
        }

        R reply1Payload = JmsHelper.extractObject(reply, callback.getExpectedType());
        callback.handleReply(reply1Payload);
    }
}

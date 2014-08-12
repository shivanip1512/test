package com.cannontech.common.util.jms;

import java.io.Serializable;

import javax.jms.ConnectionFactory;
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
            ConnectionFactory connectionFactory, String requestQueueName, boolean isPubSubDomain) {
        super(configurationName, configurationSource, connectionFactory, requestQueueName, isPubSubDomain);
    }

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
        
        handleReplyOrTimeout(callback, replyTimeout, replyConsumer);
        
        replyConsumer.close();
        replyQueue.delete();
    }

    private void handleReplyOrTimeout(JmsReplyHandler<R> callback, final Duration replyTimeout,
                                      MessageConsumer replyConsumer) throws JMSException {
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

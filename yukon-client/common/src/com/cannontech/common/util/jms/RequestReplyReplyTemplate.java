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

public class RequestReplyReplyTemplate<R1 extends Serializable, R2 extends Serializable>
    extends RequestReplyTemplateBase<JmsReplyReplyHandler<R1, R2>> {
    
    public RequestReplyReplyTemplate(String configurationName, ConfigurationSource configurationSource,
            ConnectionFactory connectionFactory, String requestQueueName, boolean isPubSubDomain) {
        super(configurationName, configurationSource, connectionFactory, requestQueueName, isPubSubDomain);
    }

    @Override
    protected <Q extends Serializable> void doJmsWork(Session session, final Q requestPayload, JmsReplyReplyHandler<R1, R2> callback) throws JMSException {
        final Duration reply1Timeout = configurationSource.getDuration(configurationName + "_REPLY1_TIMEOUT", Duration.standardMinutes(1));
        final Duration reply2Timeout = configurationSource.getDuration(configurationName + "_REPLY2_TIMEOUT", Duration.standardMinutes(10));

        DynamicDestinationResolver resolver = new DynamicDestinationResolver();
        MessageProducer producer = session.createProducer(resolver.resolveDestinationName(session, requestQueueName, pubSubDomain));
        
        TemporaryQueue replyQueue = session.createTemporaryQueue();
        MessageConsumer replyConsumer = session.createConsumer(replyQueue);
        
        ObjectMessage requestMessage = session.createObjectMessage(requestPayload);
        
        requestMessage.setJMSReplyTo(replyQueue);
        log.trace("Sending requestMessage to producer " + requestMessage.toString());
        producer.send(requestMessage);
        
        handleRepliesAndOrTimeouts(callback, reply1Timeout, reply2Timeout, replyConsumer);
        log.trace("Request replied or timed out " + requestMessage.toString());
        
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

    private void handleRepliesAndOrTimeouts(JmsReplyReplyHandler<R1, R2> callback,
                                            final Duration reply1Timeout,
                                            final Duration reply2Timeout, MessageConsumer replyConsumer)
            throws JMSException {
        /* Blocks for status response or until timeout*/
        Message reply1 = replyConsumer.receive(reply1Timeout.getMillis());
        
        if (reply1 == null) {
            callback.handleTimeout1();
            return;
        }
        R1 reply1Payload = JmsHelper.extractObject(reply1, callback.getExpectedType1());
        boolean keepGoing = callback.handleReply1(reply1Payload);
        
        if (!keepGoing) {
            return;
        }
        /* Blocks for reading point data or until timeout*/
        Message reply2 = replyConsumer.receive(reply2Timeout.getMillis());
        if (reply2 == null) {
            callback.handleTimeout2();
            return;
        }
        R2 reply2Payload = JmsHelper.extractObject(reply2, callback.getExpectedType2());
        callback.handleReply2(reply2Payload);
    }
}

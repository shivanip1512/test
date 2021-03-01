package com.cannontech.common.util.jms;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.joda.time.Duration;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;

public class ThriftRequestReplyReplyTemplate<Q extends Serializable, R1 extends Serializable, R2 extends Serializable>
    extends ThriftRequestReplyTemplateBase<Q, JmsReplyReplyHandler<R1, R2>> {
    
    private ThriftByteSerializer<Q> requestSerializer;
    private ThriftByteDeserializer<R1> reply1Deserializer;
    private ThriftByteDeserializer<R2> reply2Deserializer;

    public ThriftRequestReplyReplyTemplate(String configurationName, ConfigurationSource configurationSource,
            YukonJmsTemplate jmsTemplate, boolean isInternalMessage, ThriftByteSerializer<Q> requestSerializer, 
            ThriftByteDeserializer<R1> reply1Deserializer, ThriftByteDeserializer<R2> reply2Deserializer) {
        super(configurationName, configurationSource, jmsTemplate, isInternalMessage);
        this.requestSerializer = requestSerializer;
        this.reply1Deserializer = reply1Deserializer;
        this.reply2Deserializer = reply2Deserializer;
    }

    @Override
    protected void doJmsWork(Session session, final Q requestPayload, JmsReplyReplyHandler<R1, R2> callback) throws JMSException {
        final Duration reply1Timeout = configurationSource.getDuration(configurationName + "_REPLY1_TIMEOUT", Duration.standardMinutes(1));
        final Duration reply2Timeout = configurationSource.getDuration(configurationName + "_REPLY2_TIMEOUT", Duration.standardMinutes(10));

        DynamicDestinationResolver resolver = new DynamicDestinationResolver();
        MessageProducer producer = session.createProducer(
                resolver.resolveDestinationName(session, jmsTemplate.getDefaultDestinationName(), jmsTemplate.isPubSubDomain()));

        TemporaryQueue replyQueue = session.createTemporaryQueue();
        MessageConsumer replyConsumer = session.createConsumer(replyQueue);
        
        byte[] requestBytes = requestSerializer.toBytes(requestPayload);
        
        var requestMessage = session.createBytesMessage();
        requestMessage.writeBytes(requestBytes);
        
        requestMessage.setJMSReplyTo(replyQueue);

        log.trace("Sending requestMessage to producer: {}", requestMessage.toString());
        
        logRequest(requestPayload.toString());
        producer.send(requestMessage);
        
        handleRepliesAndOrTimeouts(callback, reply1Timeout, reply2Timeout, replyConsumer, requestPayload.toString());
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

    private void handleRepliesAndOrTimeouts(JmsReplyReplyHandler<R1, R2> callback,
            final Duration reply1Timeout,
            final Duration reply2Timeout, MessageConsumer replyConsumer,
            final String requestPayload)
            throws JMSException {
        /* Blocks for status response or until timeout */
        Message reply1 = replyConsumer.receive(reply1Timeout.getMillis());

        R1 reply1Payload = extractPayload(requestPayload, reply1, reply1Deserializer);
        
        if (reply1Payload == null) {
            logReply(requestPayload, "NULL");
            callback.handleTimeout1();
        }
        
        boolean keepGoing = callback.handleReply1(reply1Payload);
        
        if (!keepGoing) {
            return;
        }
        /* Blocks for reading point data or until timeout */
        Message reply2 = replyConsumer.receive(reply2Timeout.getMillis());

        R2 reply2Payload = extractPayload(requestPayload, reply2, reply2Deserializer);

        if (reply2 == null) {
            logReply(requestPayload, reply1Payload.toString());
            callback.handleTimeout2();
            return;
        }
        logReply(requestPayload, reply2Payload.toString());
        callback.handleReply2(reply2Payload);
    }

    private <T> T extractPayload(final String requestPayload, Message reply1, ThriftByteDeserializer<T> deserializer) throws JMSException {
        if (reply1 == null) {
            return null;
        }
        if (!(reply1 instanceof BytesMessage)) {
            logReply(requestPayload, "Not a Bytes message");
            return null;
        }
        var bytesMsg = (BytesMessage) reply1; 
        byte[] msgBytes = new byte[(int)bytesMsg.getBodyLength()];
        
        bytesMsg.readBytes(msgBytes);
        
        return deserializer.fromBytes(msgBytes);
    }
}

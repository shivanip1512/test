package com.cannontech.common.util.jms;

import java.util.concurrent.CompletableFuture;

import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ExceptionHelper;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;

public class ThriftRequestReplyTemplate<Q, R> {
    
    private static final Logger log = YukonLogManager.getLogger(ThriftRequestReplyTemplate.class);
    private static final Duration timeout = Duration.standardMinutes(1); 

    private JmsTemplate jmsTemplate;
    private String requestQueueName;
    
    @Autowired private ThriftByteSerializer<Q> requestSerializer;
    @Autowired private ThriftByteDeserializer<R> replyDeserializer;
    
    public ThriftRequestReplyTemplate(ConnectionFactory connectionFactory, String requestQueueName, 
            ThriftByteSerializer<Q> requestSerializer, ThriftByteDeserializer<R> replyDeserializer) {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
        this.jmsTemplate.setExplicitQosEnabled(true);
        this.jmsTemplate.setDeliveryPersistent(false);
        this.requestQueueName = requestQueueName;
        this.requestSerializer = requestSerializer;
        this.replyDeserializer = replyDeserializer;
    }
    
    public void send(final Q requestPayload, final CompletableFuture<R> callback) {
        try {
            log.trace("RequestReplyTemplateBase execute Start " + requestPayload.toString());
            jmsTemplate.execute(session -> {
                    try {
                        doJmsWork(session, requestPayload, callback);
                    } catch (Exception e) {
                        ExceptionHelper.throwOrWrap(e);
                    }
                    return null;
                }, true);
            log.trace("RequestReplyTemplateBase execute End " + requestPayload.toString());
        } catch (Exception e) {
            callback.completeExceptionally(e);
        }
    }

    private void doJmsWork(Session session,
        final Q requestPayload, final CompletableFuture<R> callback) throws JMSException {

        MessageProducer producer = session.createProducer(session.createQueue(requestQueueName));
        TemporaryQueue replyQueue = session.createTemporaryQueue();
        MessageConsumer replyConsumer = session.createConsumer(replyQueue);
        BytesMessage requestMessage = session.createBytesMessage();
        
        requestMessage.writeBytes(requestSerializer.toBytes(requestPayload));
        requestMessage.setJMSReplyTo(replyQueue);
        
        producer.send(requestMessage);
        
        handleReplyOrTimeout(callback, timeout, replyConsumer);
        
        replyConsumer.close();
        replyQueue.delete();
    }

    private void handleReplyOrTimeout(final CompletableFuture<R> callback, final Duration replyTimeout,
                                      MessageConsumer replyConsumer) throws JMSException {
        // Block for status response or until timeout.
        BytesMessage reply = (BytesMessage)replyConsumer.receive(replyTimeout.getMillis());
        
        if (reply == null) {
            callback.complete(null);
            return;
        }

        byte[] msgBytes = new byte[(int)reply.getBodyLength()];
        
        reply.readBytes(msgBytes);
        
        callback.complete(replyDeserializer.fromBytes(msgBytes));
    }
}

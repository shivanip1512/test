package com.cannontech.common.util.jms;

import java.util.concurrent.CompletableFuture;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ExceptionHelper;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;

public class ThriftRequestReplyTemplate<Q, R> {

    private YukonJmsTemplate jmsTemplate;

    private static final Logger log = YukonLogManager.getLogger(ThriftRequestReplyTemplate.class);
    private static final Duration timeout = Duration.standardMinutes(1); 

    private ThriftByteSerializer<Q> requestSerializer;
    private ThriftByteDeserializer<R> replyDeserializer;
    
    public ThriftRequestReplyTemplate(YukonJmsTemplate jmsTemplate, 
            ThriftByteSerializer<Q> requestSerializer, ThriftByteDeserializer<R> replyDeserializer) {
        this.jmsTemplate = jmsTemplate;
        this.requestSerializer = requestSerializer;
        this.replyDeserializer = replyDeserializer;
    }
    
    public void send(final Q requestPayload, final CompletableFuture<R> callback) {
        try {
            log.trace("RequestReplyTemplateBase execute Start " + requestPayload.toString());
            jmsTemplate.execute(session -> {
                    try {
                        doJmsWork(session, requestPayload, callback, null);
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
    
    public void send(final Q requestPayload, final CompletableFuture<R> callback, TemporaryQueue replyQueue) {
        try {
            log.trace("RequestReplyTemplateBase execute Start " + requestPayload.toString());
            jmsTemplate.execute(session -> {
                    try {
                        doJmsWork(session, requestPayload, callback, replyQueue);
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
        final Q requestPayload, final CompletableFuture<R> callback, TemporaryQueue replyQueue) throws JMSException {

        var resolver = new DynamicDestinationResolver();
        Destination destination = resolver.resolveDestinationName(session, jmsTemplate.getDefaultDestinationName(), jmsTemplate.isPubSubDomain());
        if (replyQueue.equals(null)) {
            replyQueue = session.createTemporaryQueue();
        }
        try ( 
            MessageProducer producer = session.createProducer(destination);
            MessageConsumer replyConsumer = session.createConsumer(replyQueue);
        ) {
            BytesMessage requestMessage = session.createBytesMessage();
            
            requestMessage.writeBytes(requestSerializer.toBytes(requestPayload));
            requestMessage.setJMSReplyTo(replyQueue);
            
            producer.send(requestMessage);
            
            handleReplyOrTimeout(callback, timeout, replyConsumer);
        } finally {
            replyQueue.delete();
        }
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

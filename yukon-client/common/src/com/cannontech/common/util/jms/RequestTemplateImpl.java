package com.cannontech.common.util.jms;

import java.io.Serializable;
import java.util.UUID;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.joda.time.Duration;
import org.springframework.jms.support.JmsUtils;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.cannontech.common.config.ConfigurationSource;

/**
 * This template will create destination queue for the replies.
 *
 */
public class RequestTemplateImpl<R extends Serializable> extends RequestReplyTemplateBase<JmsReplyHandler<R>> implements
        RequestReplyTemplate<R> {

    /**
     * @param isInternalMessage : if true, specifies that messages are being sent internally between Yukon
     *        services (not Network Manager). This prevents message details from be logged to the RFN comms
     *        log.
     */
    public RequestTemplateImpl(String configurationName, ConfigurationSource configurationSource,
            ConnectionFactory connectionFactory, String requestQueueName, boolean isPubSubDomain, boolean isInternalMessage) {
        super(configurationName, configurationSource, connectionFactory, requestQueueName, isPubSubDomain, isInternalMessage);
    }

    @Override
    protected <Q extends Serializable> void doJmsWork(Session session, final Q requestPayload,
            JmsReplyHandler<R> callback) throws JMSException {
        final Duration replyTimeout =
            configurationSource.getDuration(configurationName + "_REPLY_TIMEOUT", Duration.standardMinutes(1));
        MessageConsumer consumer = null;
        MessageProducer producer = null;
        DestinationResolver destinationResolver = new DynamicDestinationResolver();
        try {
            final String correlationId = UUID.randomUUID().toString();
            final Destination requestQueue =
                destinationResolver.resolveDestinationName(session, requestQueueName + ".Request", pubSubDomain);
            final Destination replyQueue =
                destinationResolver.resolveDestinationName(session, requestQueueName + ".Response", pubSubDomain);

            consumer = session.createConsumer(replyQueue, "JMSCorrelationID = '" + correlationId + "'");
            ObjectMessage requestMessage = session.createObjectMessage(requestPayload);
            requestMessage.setJMSReplyTo(replyQueue);
            requestMessage.setJMSCorrelationID(correlationId);
            producer = session.createProducer(requestQueue);
            producer.send(requestQueue, requestMessage);

            handleReplyOrTimeout(callback, replyTimeout, consumer);
        } finally {
            JmsUtils.closeMessageConsumer(consumer);
            JmsUtils.closeMessageProducer(producer);
        }
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

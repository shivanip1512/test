package com.cannontech.common.util.jms;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.logging.log4j.core.Logger;
import org.joda.time.Duration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.JmsMultiResponse;
import com.cannontech.common.util.jms.api.JmsApi;
import com.cannontech.common.util.jms.api.JmsCommunicationPattern;
import com.cannontech.common.util.jms.api.JmsQueue;

/**
 * JMS communications template that sends requests where each request may have multiple responses. The number of 
 * expected responses does not need to be known at the time of request.
 */
public class RequestMultiReplyTemplate<R extends Serializable, Q extends JmsMultiResponse> {
    private static final Logger log = YukonLogManager.getLogger(RequestMultiReplyTemplate.class);
    private static final Logger rfnLogger = YukonLogManager.getRfnLogger();
    private static final Duration defaultTimeout = Duration.standardSeconds(30);
    private static final boolean pubSubDomain = false; 
    
    private final ConnectionFactory connection;
    private final JmsApi<R,?,Q> api;
    private final Duration timeout;
    private final boolean isInternal;
    private final ExecutorService executor;
    
    /**
     * Create a new template, automatically using the default timeout and assuming external messaging 
     * (logged to rfn comms logs).
     * @param connection The ConnectionFactory to use for messaging.
     * @param api The JmsApi that describes the communications via this template.
     */
    public RequestMultiReplyTemplate(ConnectionFactory connection, JmsApi<R,?,Q> api) {
        this(connection, null, api, defaultTimeout, false);
    }
    
    /**
     * Create a new template.
     * @param connection The ConnectionFactory to use for messaging.
     * @param workerQueueSize Size of the worker queue. If null, the default will be used.
     * @param api The JmsApi that describes the communications via this template.
     * @param timeout The maximum length of time to wait for responses after the request is sent.
     * @param isInternal Whether the communications are internal to Yukon or external between Yukon and NM. External
     * comms are logged to the RFN Comms logs.
     */
    public RequestMultiReplyTemplate(ConnectionFactory connection, Integer workerQueueSize, 
                                     JmsApi<R,?,Q> api, Duration timeout, boolean isInternal) {
        
        if (api.getPattern() != JmsCommunicationPattern.REQUEST_MULTI_RESPONSE) {
            throw new IllegalArgumentException("Specified API: " + api.getName() + 
                                               " does not support Request-Multi-Response communication");
        }
        
        this.connection = connection;
        this.api = api;
        this.timeout = timeout;
        this.isInternal = isInternal;
        
        // Default to 50 if worker queue size is not specified
        int queueSize = workerQueueSize == null ? 50 : workerQueueSize;
        
        // Use the same queue size for core and max
        // - ThreadPoolExecutor does not create new threads unless its thread pool is full. 
        // - LinkedBlockingQueue has Integer.MAX_VALUE size by default - although this can be specified in the constructor.
        executor = new ThreadPoolExecutor(queueSize, queueSize, 
                                                       5, TimeUnit.MINUTES,
                                                       new LinkedBlockingQueue<Runnable>(),
                                                       new ThreadPoolExecutor.AbortPolicy());
        ((ThreadPoolExecutor)executor).allowCoreThreadTimeOut(true);
    }
    
    /**
     * Send the specified request, and handle the results with the specified replyHandler.
     * @param request The request to send.
     * @param replyHandler The handler for responses, timeout or error.
     */
    public void send(final R request, final JmsMultiResponseHandler<Q> replyHandler) {
        
        log.trace("ThreadPool size core:" + ((ThreadPoolExecutor)executor).getCorePoolSize() +
                  "  max:" + ((ThreadPoolExecutor)executor).getMaximumPoolSize() +
                  "  pool:" + ((ThreadPoolExecutor)executor).getPoolSize());

        executor.execute(() -> {
            try {
                jmsExecute(request, replyHandler);
            } catch (Exception e) {
                replyHandler.handleException(e);
            } finally {
                // Make sure complete() is called for success, timeout, or exception
                replyHandler.complete();
            }
        });
    }
    
    /**
     * Set up the JMS template and execute the send/receive.
     * @param request The request to send.
     * @param replyHandler The callback that will handle results.
     */
    private void jmsExecute(R request, JmsMultiResponseHandler<Q> replyHandler) throws JMSException {
        logBeforeSend(request);
        
        JmsTemplate jmsTemplate = new JmsTemplate(connection);
        jmsTemplate.execute(session -> {
            sendAndReceive(session, request, replyHandler);
            return null;
        }, true);
        log.trace("RequestMultiReply execute End " + request.toString());
    }
    
    /**
     * Set up JMS infrastructure, send the request, and handle the results.
     * @param session The JMS Session to use for sending and receiving.
     * @param request The request message to send.
     * @param replyHandler The callback that handles the results of the request, including responses, timeouts and errors.
     */
    private void sendAndReceive(Session session, final R request, JmsMultiResponseHandler<Q> replyHandler) throws JMSException {
        
        DynamicDestinationResolver resolver = new DynamicDestinationResolver();
        Destination replyQueue = getReplyQueue(session);
        
        // Create MessageProducer to send
        MessageProducer producer = session.createProducer(resolver.resolveDestinationName(session, getRequestQueueName(), pubSubDomain));
        // Create ReplyConsumer to receive
        MessageConsumer replyConsumer = session.createConsumer(replyQueue);
        
        try {
            ObjectMessage requestMessage = session.createObjectMessage(request);
            requestMessage.setJMSReplyTo(replyQueue);
            producer.send(requestMessage);
            handleRepliesAndOrTimeouts(replyHandler, replyConsumer);
        } catch (Exception e) {
            log.error("Error sending request.", e);
        } finally {
            producer.close();
            replyConsumer.close();
        }
    }
    
    /**
     * Handle replies to the request message, or timeouts, if they occur.
     * @param replyHandler The callback that will handle the responses.
     * @param replyConsumer The MessageConsumer that is receiving response messages from the JMS queue.
     */
    private void handleRepliesAndOrTimeouts(JmsMultiResponseHandler<Q> replyHandler, MessageConsumer replyConsumer) 
            throws JMSException {
        
        int expectedMessages = 0; //segmentNumber is 1-indexed
        int messagesReceived = 0;
        
        while (messagesReceived == 0 || messagesReceived < expectedMessages) {
            // Blocks for response or until timeout
            Message replyMessage = replyConsumer.receive(timeout.getMillis());
            
            // If we've timed out, give up and exit. No more messages will be received.
            if (replyMessage == null) {
                replyHandler.handleTimeout();
                return;
            }
            
            // Handle the reply, set the expected # of messages, and update the number of messages received so far 
            Q reply = JmsHelper.extractObject(replyMessage, replyHandler.getExpectedType());
            replyHandler.handleReply(reply);
            expectedMessages = reply.getTotalSegments();
            messagesReceived += 1;
        }
    }
    
    /**
     * Get the reply Destination, based on the information specified in the JmsApi. This may be a named queue, or a
     * temporary queue.
     */
    private Destination getReplyQueue(Session session) throws JMSException {
        JmsQueue responseQueue = getResponseQueue();
        Destination replyQueue;
        
        if (responseQueue == JmsQueue.TEMP_QUEUE) {
            replyQueue = session.createTemporaryQueue();
        } else {
            replyQueue = session.createQueue(responseQueue.getName());
        }
        return replyQueue;
    }
    
    /**
     * Log the request content before sending
     */
    private void logBeforeSend(R request) {
        if (!isInternal && rfnLogger.isInfoEnabled()) {
            rfnLogger.info("<<< " + request.toString());
        } else if (isInternal && rfnLogger.isDebugEnabled()) {
            rfnLogger.debug("<<< " + request.toString());
        }
        if (log.isTraceEnabled()) {
            log.trace("RequestMultiReplyTemplate execute Start " + request.toString());
        }
    }
    
    /**
     * @return The request queue name string.
     */
    private String getRequestQueueName() {
        return api.getQueue().getName();
    }
    
    /**
     * @return The response JmsQueue.
     */
    private JmsQueue getResponseQueue() {
        return api.getResponseQueue()
                  .orElseThrow();
    }
}

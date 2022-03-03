package com.cannontech.common.util.jms;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
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
    private static final Duration defaultTimeout = Duration.standardSeconds(30);
    private static final boolean pubSubDomain = false;
    
    private final YukonJmsTemplate jmsTemplate;
    private final JmsApi<R,?,Q> api;
    private final Duration timeout;
    private final ExecutorService executor;
    private final Logger commsLogger;
    
    /**
     * Create a new template, automatically using the default timeout and assuming external messaging 
     * (logged to rfn comms logs).
     * @param jmsTemplate The JmsTemplate to use for messaging.
     * @param api The JmsApi that describes the communications via this template.
     */
    public RequestMultiReplyTemplate(YukonJmsTemplate jmsTemplate, JmsApi<R, ?, Q> api) {
        this(jmsTemplate, null, api, defaultTimeout);
    }
    
    /**
     * Create a new template.
     * @param jmsTemplate The JmsTemplate to use for messaging.
     * @param workerQueueSize Size of the worker queue. If null, the default will be used.
     * @param api The JmsApi that describes the communications via this template.
     * @param timeout The maximum length of time to wait for responses after the request is sent.
     */
    public RequestMultiReplyTemplate(YukonJmsTemplate jmsTemplate, Integer workerQueueSize, JmsApi<R, ?, Q> api, Duration timeout) {
        
        if (api.getPattern() != JmsCommunicationPattern.REQUEST_MULTI_RESPONSE) {
            throw new IllegalArgumentException("Specified API: " + api.getName() + 
                                               " does not support Request-Multi-Response communication");
        }
        
        this.jmsTemplate = jmsTemplate;
        this.api = api;
        this.timeout = timeout;
        this.commsLogger = api.getCommsLogger();
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
        logRequest(request.toString());

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
            handleRepliesAndOrTimeouts(replyHandler, replyConsumer, request.toString());
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
    private void handleRepliesAndOrTimeouts(JmsMultiResponseHandler<Q> replyHandler, MessageConsumer replyConsumer, String request) 
            throws JMSException {
        
        int expectedMessages = 0; //segmentNumber is 1-indexed
        int messagesReceived = 0;
        
        while (messagesReceived == 0 || messagesReceived < expectedMessages) {
            // Blocks for response or until timeout
            Message replyMessage = replyConsumer.receive(timeout.getMillis());
            
            // If we've timed out, give up and exit. No more messages will be received.
            if (replyMessage == null) {
                logReply(request, "NULL", expectedMessages, messagesReceived);
                replyHandler.handleTimeout();
                return;
            }
            
            // Handle the reply, set the expected # of messages, and update the number of messages received so far 
            Q reply = JmsHelper.extractObject(replyMessage, replyHandler.getExpectedType());
            replyHandler.handleReply(reply);
            expectedMessages = reply.getTotalSegments();
            messagesReceived += 1;
            logReply(request, reply.loggingString(commsLogger.getLevel()), expectedMessages, messagesReceived);
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
     * Adds an entry in rfnLogger
     */
    private void log(String text) {
        if(commsLogger == null) {
            return;
        }
        if (commsLogger.isInfoEnabled()) {
            commsLogger.info(text);
        } else if (commsLogger.isDebugEnabled()) {
            commsLogger.debug(text);
        }
    }
    
    protected void logRequest(String request){
        log.trace("RequestMultiReplyTemplate execute start: {}", request);
        log("<<< Sent " + request);
    }
    
    protected void logReply(String request, String reply, int expectedMessages, int messagesReceived) {
        log.trace("RequestMultiReplyTemplate reply: {} [{} out of {}] {}", request, messagesReceived, expectedMessages, reply);
        log(">>> Received " + reply + " [" + messagesReceived + " out of " + expectedMessages + "] for " + request);
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

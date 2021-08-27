package com.cannontech.common.util.jms;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.SessionCallback;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.ExceptionHelper;

public abstract class RequestReplyTemplateBase<T extends JmsBaseReplyHandler> {
    protected static final Logger log = YukonLogManager.getLogger(RequestReplyTemplateBase.class);
    
    protected ConfigurationSource configurationSource;
    protected YukonJmsTemplate jmsTemplate;
    protected ExecutorService readRequestThreadPool;
    protected String configurationName;
    protected boolean internalMessage = false;
    
    /**
     * @param isInternalMessage : if true, specifies that messages are being sent internally between Yukon
     *        services (not Network Manager). This prevents message details from be logged to the RFN comms
     *        log.
     */
    public RequestReplyTemplateBase(String configurationName, ConfigurationSource configurationSource,
            YukonJmsTemplate jmsTemplate, boolean isInternalMessage) {
        this.configurationName = configurationName;
        this.configurationSource = configurationSource;
        this.jmsTemplate = jmsTemplate;
        this.internalMessage = isInternalMessage;
        
        int queueSize = configurationSource.getInteger("REQUEST_REPLY_WORKER_QUEUE_SIZE", 50);
        
        // Use the same queue size for core and max;
        // - ThreadPoolExecutor does not create new threads unless its thread pool is full. 
        // - LinkedBlockingQueue has Integer.MAX_VALUE size by default - although this can be specified in the constructor.
        readRequestThreadPool = new ThreadPoolExecutor(queueSize, queueSize, 
                                                       5, TimeUnit.MINUTES,
                                                       new LinkedBlockingQueue<Runnable>(),
                                                       new ThreadPoolExecutor.AbortPolicy());
        ((ThreadPoolExecutor)readRequestThreadPool).allowCoreThreadTimeOut(true);
    }
    
    public <Q extends Serializable> void send(final Q requestPayload, final T callback) {

        log.trace("ThreadPool size core:" + ((ThreadPoolExecutor)readRequestThreadPool).getCorePoolSize() +
                           "  max:" + ((ThreadPoolExecutor)readRequestThreadPool).getMaximumPoolSize() +
                           "  pool:" + ((ThreadPoolExecutor)readRequestThreadPool).getPoolSize());
        
        readRequestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    log.trace("RequestReplyTemplateBase execute Start {}", requestPayload.toString());
                    jmsTemplate.execute(new SessionCallback<Object>() {

                        @Override
                        public Object doInJms(Session session) throws JMSException {
                            try {
                                doJmsWork(session, requestPayload, callback);
                            } catch (Exception e) {
                                ExceptionHelper.throwOrWrap(e);
                            }
                            return null;
                        }

                    }, true);
                    log.trace("RequestReplyTemplateBase execute End {}", requestPayload.toString());
                } catch (Exception e) {
                    callback.handleException(e);
                } finally {
                    callback.complete();
                }
            }
        });
    }

    /**
     * Adds an entry in rfnLogger
     */
    private void log(String text) {
        if (jmsTemplate.isCommsLoggingDisabled()) {
            return;
        }
        Logger commsLogger = jmsTemplate.getCommsLogger();
        if (!internalMessage && commsLogger.isInfoEnabled()) {
            commsLogger.info(text);
        } else if (internalMessage && commsLogger.isDebugEnabled()) {
            commsLogger.debug(text);
        }
    }
    
    protected void logRequest(String request){
        log("<<< Sent " + request);
    }
    
    protected void logReply(String request, String reply) {
        log(">>> Received " + reply + " for " + request);
    }
    
    protected abstract <Q extends Serializable> void doJmsWork(Session session,
            Q requestPayload, T callback) throws JMSException;
    
    /**
     * Send message after setting DeliveryMode, Priority and TimeToLive if explicitQosEnabled is true.
     */
    protected void sendMessage(MessageProducer producer, ObjectMessage message) throws JMSException {
        if (jmsTemplate.isExplicitQosEnabled()) {
            producer.send(message, jmsTemplate.getDeliveryMode(), jmsTemplate.getPriority(), jmsTemplate.getTimeToLive());
        } else {
            producer.send(message);
        }
    }
}

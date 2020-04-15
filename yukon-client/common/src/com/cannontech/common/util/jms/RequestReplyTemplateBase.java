package com.cannontech.common.util.jms;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.SessionCallback;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.ExceptionHelper;
import com.cannontech.common.util.jms.api.JmsApi;

public abstract class RequestReplyTemplateBase<T extends JmsBaseReplyHandler> {
    protected static final Logger log = YukonLogManager.getLogger(RequestReplyTemplateBase.class);
    protected static final Logger rfnLogger = YukonLogManager.getRfnLogger();
    
    protected ConfigurationSource configurationSource;
    protected YukonJmsTemplate jmsTemplate;
    protected ExecutorService readRequestThreadPool;
    protected String configurationName;
    protected String requestQueueName;
    protected boolean pubSubDomain = false;   // Queue (not a Topic)
    protected boolean internalMessage = false;
    
    /**
     * @param isInternalMessage : if true, specifies that messages are being sent internally between Yukon
     *        services (not Network Manager). This prevents message details from be logged to the RFN comms
     *        log.
     */
    public RequestReplyTemplateBase(String configurationName, ConfigurationSource configurationSource,
            YukonJmsTemplate jmsTemplate, JmsApi<?, ?, ?> jmsApi, boolean isInternalMessage) {
        this.configurationName = configurationName;
        this.configurationSource = configurationSource;
        this.jmsTemplate = jmsTemplate;
        this.requestQueueName = jmsApi.getQueueName();
        this.pubSubDomain = jmsApi.isTopic();
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
        if (!internalMessage && rfnLogger.isInfoEnabled()) {
            rfnLogger.info(text);
        } else if (internalMessage && rfnLogger.isDebugEnabled()) {
            rfnLogger.debug(text);
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
}

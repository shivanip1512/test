package com.cannontech.common.util.jms;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.logging.log4j.Logger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.ExceptionHelper;

public abstract class ThriftRequestReplyTemplateBase<Q extends Serializable, T extends JmsBaseReplyHandler> {
    protected static final Logger log = YukonLogManager.getLogger(ThriftRequestReplyTemplateBase.class);
    protected final Logger commsLogger;
    
    protected ConfigurationSource configurationSource;
    protected YukonJmsTemplate jmsTemplate;
    protected ExecutorService readRequestThreadPool;
    protected String configurationName;
    protected boolean isDebugLog = false;
    
    /**
     * @param isDebugLog : if true, logs as debug otherwise logs as info
     */
    public ThriftRequestReplyTemplateBase(String configurationName, ConfigurationSource configurationSource,
            YukonJmsTemplate jmsTemplate, boolean isDebugLog) {
        this.commsLogger = jmsTemplate.getCommsLogger();
        this.configurationName = configurationName;
        this.configurationSource = configurationSource;
        this.jmsTemplate = jmsTemplate;
        this.isDebugLog = isDebugLog;
        
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
    
    public void send(final Q requestPayload, final T callback) {

        log.trace("ThreadPool size core:" + ((ThreadPoolExecutor)readRequestThreadPool).getCorePoolSize() +
                           "  max:" + ((ThreadPoolExecutor)readRequestThreadPool).getMaximumPoolSize() +
                           "  pool:" + ((ThreadPoolExecutor)readRequestThreadPool).getPoolSize());
        
        readRequestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    log.trace("RequestReplyTemplateBase execute Start {}", requestPayload.toString());
                    jmsTemplate.execute(session -> {
                            try {
                                doJmsWork(session, requestPayload, callback);
                            } catch (Exception e) {
                                ExceptionHelper.throwOrWrap(e);
                            }
                            return null;
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
        if(commsLogger == null) {
            return;
        }
        if (!isDebugLog && commsLogger.isInfoEnabled()) {
            commsLogger.info(text);
        } else if (isDebugLog && commsLogger.isDebugEnabled()) {
            commsLogger.debug(text);
        }
    }
    
    protected void logRequest(String request){
        log("<<< Sent " + request);
    }
    
    protected void logReply(String request, String reply) {
        log(">>> Received " + reply + " for " + request);
    }
    
    protected abstract void doJmsWork(Session session, Q requestPayload, T callback) throws JMSException;
}

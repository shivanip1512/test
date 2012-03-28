package com.cannontech.common.util.jms;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.ExceptionHelper;

public abstract class RequestReplyTemplateBase<T extends JmsBaseReplyHandler> {
    protected ConfigurationSource configurationSource;
    protected ConnectionFactory connectionFactory;
    protected ExecutorService readRequestThreadPool =
            new ThreadPoolExecutor(1, 6, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(),
                                   new ThreadPoolExecutor.AbortPolicy());
    protected String configurationName;
    protected String requestQueueName;
    protected boolean pubSubDomain = false;   // Queue (not a Topic)

    public <Q extends Serializable> void send(final Q requestPayload, final T callback) {
        readRequestThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);

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
                } catch (Exception e) {
                    callback.handleException(e);
                } finally {
                    callback.complete();
                }
            }
        });
    }

    protected abstract <Q extends Serializable> void doJmsWork(Session session,
            Q requestPayload, T callback) throws JMSException;

    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setReadRequestThreadPool(ExecutorService readRequestThreadPool) {
        this.readRequestThreadPool = readRequestThreadPool;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public void setRequestQueueName(String requestQueueName, boolean pubSubDomain) {
        this.requestQueueName = requestQueueName;
        this.pubSubDomain = pubSubDomain;
    }
}

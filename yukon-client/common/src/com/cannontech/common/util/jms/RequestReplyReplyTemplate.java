package com.cannontech.common.util.jms;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.joda.time.Duration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.ExceptionHelper;

public class RequestReplyReplyTemplate {

    private ConfigurationSource configurationSource;
    private ConnectionFactory connectionFactory;
    private ExecutorService readRequestThreadPool = new ThreadPoolExecutor(1, 6, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.AbortPolicy());

    private String configurationName;
    
    private String requestQueueName;
    private boolean pubSubDomain = false;   // Queue (not a Topic)



    public <Q extends Serializable,R1 extends Serializable,R2 extends Serializable> void send(final Q requestPayload, final JmsReplyReplyHandler<R1, R2> callback) {

        readRequestThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);

                    jmsTemplate.execute(new SessionCallback() {

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

    private <Q extends Serializable,R1 extends Serializable,R2 extends Serializable> void doJmsWork(Session session, final Q requestPayload, JmsReplyReplyHandler<R1, R2> callback) throws JMSException {
        final Duration reply1Timeout = configurationSource.getDuration(configurationName + "_REPLY1_TIMEOUT", Duration.standardMinutes(1));
        final Duration reply2Timeout = configurationSource.getDuration(configurationName + "_REPLY2_TIMEOUT", Duration.standardMinutes(10));

        DynamicDestinationResolver resolver = new DynamicDestinationResolver();
        MessageProducer producer = session.createProducer(resolver.resolveDestinationName(session, requestQueueName, pubSubDomain));
        
        TemporaryQueue replyQueue = session.createTemporaryQueue();
        MessageConsumer replyConsumer = session.createConsumer(replyQueue);
        
        ObjectMessage requestMessage = session.createObjectMessage(requestPayload);
        
        requestMessage.setJMSReplyTo(replyQueue);
        producer.send(requestMessage);
        
        /* Blocks for status response or until timeout*/
        Message reply1 = replyConsumer.receive(reply1Timeout.getMillis());
        
        if (reply1 == null) {
            callback.handleTimeout1();
            return;
        }
        R1 reply1Payload = JmsHelper.extractObject(reply1, callback.getExpectedType1());
        boolean keepGoing = callback.handleReply1(reply1Payload);
        
        if (!keepGoing) {
            return;
        }
        /* Blocks for reading point data or until timeout*/
        Message reply2 = replyConsumer.receive(reply2Timeout.getMillis());
        if (reply2 == null) {
            callback.handleTimeout2();
        }
        R2 reply2Payload = JmsHelper.extractObject(reply2, callback.getExpectedType2());
        callback.handleReply2(reply2Payload);
    }
    

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

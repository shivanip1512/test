package com.cannontech.amr.rfn.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectRequest;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeterIdentifier;
import com.cannontech.common.config.ConfigurationSource;

public class RfnMeterDisconnectService {
    
    private ConnectionFactory connectionFactory;
    private int timeout;
    private ConfigurationSource configurationSource;
    private ExecutorService readRequestThreadPool;
    
    public void send(final RfnMeterIdentifier meter, final RfnMeterDisconnectStatusType action, final RfnMeterDisconnectCallback callback) {
        
        readRequestThreadPool.execute(new Runnable() {
            
            @Override
            public void run() {
                JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
                
                jmsTemplate.execute(new SessionCallback() {
                    
                    @Override
                    public Object doInJms(Session session) throws JMSException {
                        try {
                            
                            DynamicDestinationResolver resolver = new DynamicDestinationResolver();
                            MessageProducer producer = session.createProducer(resolver.resolveDestinationName(session, "yukon.rr.obj.amr.rfn.MeterDisconnectRequest", false));
                            
                            TemporaryQueue replyQueue = session.createTemporaryQueue();
                            MessageConsumer replyConsumer = session.createConsumer(replyQueue);
                            
                            ObjectMessage requestMessage = session.createObjectMessage(new RfnMeterDisconnectRequest(meter, action));
                            
                            requestMessage.setJMSReplyTo(replyQueue);
                            producer.send(requestMessage);
                            
                            ObjectMessage replyMessage = (ObjectMessage)replyConsumer.receive(Duration.standardMinutes(timeout).getMillis());
                            RfnMeterDisconnectReplyType replyType = RfnMeterDisconnectReplyType.TIMEOUT;
                            
                            if(replyMessage != null) {
                                replyType = ((RfnMeterDisconnectReply) replyMessage.getObject()).getReplyType();
                            }
                            
                            callback.receivedData(replyType);
                            
                        } catch (Exception e) {
                            callback.processingExceptionOccured(e.getMessage());
                        } finally {
                            callback.complete();
                        }
                        return null;
                    }
                }, true);
            }
        });
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    @PostConstruct
    public void initialize() {
        timeout = configurationSource.getInteger("RFN_METER_DISCONNECT_TIMEOUT_MINUTES", 30);
        readRequestThreadPool = Executors.newFixedThreadPool(6);
    }
}
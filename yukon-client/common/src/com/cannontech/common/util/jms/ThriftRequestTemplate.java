package com.cannontech.common.util.jms;

import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ExceptionHelper;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;

public class ThriftRequestTemplate<Q> {
    
    private static final Logger log = YukonLogManager.getLogger(ThriftRequestTemplate.class);

    private JmsTemplate jmsTemplate;
    private String requestQueueName;
    
    private ThriftByteSerializer<Q> requestSerializer;
    
    public ThriftRequestTemplate(ConnectionFactory connectionFactory, String requestQueueName, ThriftByteSerializer<Q> requestSerializer) {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
        this.jmsTemplate.setExplicitQosEnabled(true);
        this.jmsTemplate.setDeliveryPersistent(false);
        this.requestQueueName = requestQueueName;
        this.requestSerializer = requestSerializer;
    }
    
    public void send(final Q requestPayload) {
        log.trace("RequestReplyTemplateBase execute Start " + requestPayload.toString());
        jmsTemplate.execute(session -> {
                try {
                    doJmsWork(session, requestPayload);
                } catch (Exception e) {
                    ExceptionHelper.throwOrWrap(e);
                }
                return null;
            }, true);
        log.trace("RequestReplyTemplateBase execute End " + requestPayload.toString());
    }
    
    public String getRequestQueueName() {
        return requestQueueName;
    }

    private void doJmsWork(Session session, final Q requestPayload) throws JMSException {

        MessageProducer producer = session.createProducer(session.createQueue(requestQueueName));
        BytesMessage requestMessage = session.createBytesMessage();
        
        requestMessage.writeBytes(requestSerializer.toBytes(requestPayload));
        
        producer.send(requestMessage);
    }
}

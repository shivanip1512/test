package com.cannontech.common.util.jms;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ExceptionHelper;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;

public class ThriftRequestTemplate<Q> {

    private static final Logger log = YukonLogManager.getLogger(ThriftRequestTemplate.class);

    private YukonJmsTemplate jmsTemplate;

    private ThriftByteSerializer<Q> requestSerializer;
    
    public ThriftRequestTemplate(YukonJmsTemplate jmsTemplate, ThriftByteSerializer<Q> requestSerializer) {
        this.jmsTemplate = jmsTemplate;
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
        return jmsTemplate.getDefaultDestinationName();
    }

    private void doJmsWork(Session session, final Q requestPayload) throws JMSException {

        var resolver = new DynamicDestinationResolver();
        Destination destination = resolver.resolveDestinationName(session, jmsTemplate.getDefaultDestinationName(), jmsTemplate.isPubSubDomain()); 
        try (
            MessageProducer producer = session.createProducer(destination);
        ) {
            BytesMessage requestMessage = session.createBytesMessage();
            
            requestMessage.writeBytes(requestSerializer.toBytes(requestPayload));
            
            producer.send(requestMessage);
        }
    }
}

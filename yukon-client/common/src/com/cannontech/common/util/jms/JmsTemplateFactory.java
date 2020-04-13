package com.cannontech.common.util.jms;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import com.cannontech.common.util.jms.api.JmsApi;

/**
 * This class is used to create JmsTemplate Object.
 *
 */
public class JmsTemplateFactory {
    @Autowired private ConnectionFactory connectionFactory;

    /**
     * This method creates and returns JmsTemplate object by using ConnectionFactory after setting deliveryMode to NON_PERSISTENT
     * and pubSubDomain / timeToLive defined in JmsApi object.
     * 
     */
    public synchronized JmsTemplate createJmsTemplate(JmsApi<?, ?, ?> jmsApi) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setPubSubDomain(jmsApi.isTopic());
        jmsTemplate.setTimeToLive(jmsApi.getTimeToLive().getMillis());
        return jmsTemplate;
    }

    /**
     * This method creates and returns JmsTemplate object by using ConnectionFactory after setting deliveryMode to NON_PERSISTENT,
     * pubSubDomain / timeToLive defined in JmsApi object and specified messageConverter.
     * 
     */
    public synchronized JmsTemplate createJmsTemplate(JmsApi<?, ?, ?> jmsApi, MessageConverter messageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setPubSubDomain(jmsApi.isTopic());
        jmsTemplate.setTimeToLive(jmsApi.getTimeToLive().getMillis());
        jmsTemplate.setMessageConverter(messageConverter);
        return jmsTemplate;
    }

    /**
     * This method creates and returns JmsTemplate object by using ConnectionFactory after setting deliveryMode to NON_PERSISTENT.
     * 
     */
    public synchronized JmsTemplate createJmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        jmsTemplate.setExplicitQosEnabled(true);
        return jmsTemplate;
    }

}

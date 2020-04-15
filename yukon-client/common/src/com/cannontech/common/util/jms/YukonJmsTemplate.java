package com.cannontech.common.util.jms;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

/**
 * YukonJmsTemplate is a class that simplifies receiving and sending of messages through JMS. Default settings for
 * YukonJmsTemplate Sessions is "DeliveryMode.NON_PERSISTENT".
 */

public class YukonJmsTemplate extends JmsTemplate {

    @Autowired
    public YukonJmsTemplate(ConnectionFactory connectionFactory) {
        super(connectionFactory);
        setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        setExplicitQosEnabled(true);
    }
}

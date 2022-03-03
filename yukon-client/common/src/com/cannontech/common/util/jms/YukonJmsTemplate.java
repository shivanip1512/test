package com.cannontech.common.util.jms;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;

/**
 * YukonJmsTemplate is a class that simplifies receiving and sending of messages through JMS. Default settings for
 * YukonJmsTemplate Sessions is "DeliveryMode.NON_PERSISTENT".
 */

public class YukonJmsTemplate extends JmsTemplate {

    // default logger
    private Logger commsLogger = YukonLogManager.getCommsLogger();
    
    @Autowired
    public YukonJmsTemplate(ConnectionFactory connectionFactory) {
        super(connectionFactory);
        setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        setExplicitQosEnabled(true);
    }

    public Logger getCommsLogger() {
        return commsLogger;
    }

    public void setCommsLogger(Logger commsLogger) {
        this.commsLogger = commsLogger;
    }
    
    public void disableCommLogging() {
        this.commsLogger = null;
    }
    
    public boolean isCommsLoggingDisabled() {
        return commsLogger == null;
    }
}

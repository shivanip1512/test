package com.cannontech.services.jms;

import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public class InternalMessagingConnectionFactory {

    private static final int _1MB = 1048576; // 2^20 (1 MB)
    //private static final int _8MB = 8388608; // 2^23>(8 MB)

    // create a logger for instances of this class and its subclasses
    protected Logger logger = YukonLogManager.getLogger(this.getClass());

    private ActiveMQConnectionFactory factory;
    
    public InternalMessagingConnectionFactory(ActiveMQConnectionFactory factory) {
        this.factory = factory;
    }

    public ActiveMQConnection createConnection() throws JMSException {
        ActiveMQConnection connection = (ActiveMQConnection) factory.createConnection();
        connection.setProducerWindowSize(_1MB);
        return connection;
    }

    public String getBrokerURL() {
        return factory.getBrokerURL();
    }
}

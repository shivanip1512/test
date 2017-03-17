package com.cannontech.messaging.connection.amq;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import com.cannontech.clientutils.YukonLogManager;

public class AmqConnectionFactoryService {

    private static final int _1MB = 1048576; // 2^20 (1 MB)
    //private static final int _8MB = 8388608; // 2^23>(8 MB)

    private ConnectionFactory connectionFactory;
    
    // create a logger for instances of this class and its subclasses
    protected Logger logger = YukonLogManager.getLogger(this.getClass());

    public AmqConnectionFactoryService(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public ActiveMQConnection createConnection() throws JMSException, InterruptedException {
        ActiveMQConnection connection = (ActiveMQConnection) connectionFactory.createConnection();
        connection.setProducerWindowSize(_1MB);        
        return connection;
    }

    public String getBrokerUrl() throws Exception {
        ActiveMQConnectionFactory amqConnectionfactory = (ActiveMQConnectionFactory) getTargetObject(
                connectionFactory, ConnectionFactory.class);
        return amqConnectionfactory.getBrokerURL();
    }

    static private <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return targetClass.cast(((Advised) proxy).getTargetSource().getTarget());
        }
        return targetClass.cast(proxy); // expected to be cglib proxy then, which is simply a specialized class
    }
}

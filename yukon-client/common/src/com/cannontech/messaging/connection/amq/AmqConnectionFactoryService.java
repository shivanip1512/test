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

    private static final String DEFAULT_CONNECTION_URI = "tcp://localhost:61616";

    private static AmqConnectionFactoryService defaultService;

    private ConnectionFactory connectionFactory;
    
    static {
        System.setProperty(
            "org.apache.activemq.SERIALIZABLE_PACKAGES",
            "java.lang,javax.security,java.util,javax.jms,com.thoughtworks.xstream.mapper,org.joda,com.cannontech");
    }
    // create a logger for instances of this class and its subclasses
    protected Logger logger = YukonLogManager.getLogger(this.getClass());

    static {
        defaultService = new AmqConnectionFactoryService();
    }

    public AmqConnectionFactoryService() {
        this(DEFAULT_CONNECTION_URI);
    }

    public AmqConnectionFactoryService(String connectionURI) {
        this(new ActiveMQConnectionFactory(connectionURI));
    }

    public AmqConnectionFactoryService(ConnectionFactory connectionFactory) {
        if(connectionFactory == null) {
            connectionFactory = new ActiveMQConnectionFactory(DEFAULT_CONNECTION_URI);
        }
        this.connectionFactory = connectionFactory;
    }

    public ActiveMQConnection createConnection() throws JMSException, InterruptedException {
        ActiveMQConnection connection = (ActiveMQConnection) connectionFactory.createConnection();
        connection.setProducerWindowSize(_1MB);        
        return connection;
    }

    public void setFactory(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;        
    }

    public static AmqConnectionFactoryService getDefaultService() {
        return defaultService;
    }

    public static void setDefaultService(AmqConnectionFactoryService defaultConnectionFactoryService) {
        AmqConnectionFactoryService.defaultService = defaultConnectionFactoryService;
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

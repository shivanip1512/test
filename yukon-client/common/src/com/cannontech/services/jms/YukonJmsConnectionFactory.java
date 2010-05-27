package com.cannontech.services.jms;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.connection.CachingConnectionFactory;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.CtiUtilities;
import com.google.common.base.Supplier;

public class YukonJmsConnectionFactory implements ConnectionFactory {
    private static final Logger log = YukonLogManager.getLogger(YukonJmsConnectionFactory.class);
    
    private ConfigurationSource configurationSource;
    
    private volatile ConnectionFactory delegate = null;

    public ConnectionFactory getDelegate() {
        return delegate;
    }
    
    @PostConstruct
    public void initialize() {
        createConnectionFactory();
        
    }
    
    private void createConnectionFactory() {
        // double-check now that we're in sync block

        final String applicationName = CtiUtilities.getApplicationName();

        if (applicationName.equals("ServiceManager")) {
            String serverListenConnection = configurationSource.getString("JMS_SERVER_BROKER_LISTEN_CONNECTION", "tcp://localhost:61616");
            ServerEmbeddedBroker serverEmbeddedBroker = new ServerEmbeddedBroker(applicationName, serverListenConnection);
            serverEmbeddedBroker.start();
            delegate = serverEmbeddedBroker.getConnectionFactory();
        } else if (!CtiUtilities.isRunningAsClient()) {
            delegate = new LazyConnectionFactory(new Supplier<ConnectionFactory>() {
                public ConnectionFactory get() {
                    String clientBrokerConnection = configurationSource.getString("JMS_CLIENT_BROKER_CONNECTION", "tcp://localhost:61616");
                    ClientEmbeddedBroker clientEmbeddedBroker = new ClientEmbeddedBroker(applicationName, clientBrokerConnection);
                    clientEmbeddedBroker.start();
                    return clientEmbeddedBroker.getConnectionFactory();
                }
            });
        } else {
            delegate = new LazyConnectionFactory(new Supplier<ConnectionFactory>() {
                public ConnectionFactory get() {
                    String clientConnection = configurationSource.getString("JMS_CLIENT_CONNECTION", "tcp://localhost:61616");
                    // Create a ConnectionFactory
                    ConnectionFactory factory = new ActiveMQConnectionFactory(clientConnection);

                    // if using Spring, create a CachingConnectionFactory
                    CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
                    cachingFactory.setTargetConnectionFactory(factory);
                    return cachingFactory;
                }
            });
        }
    }

    @Override
    public Connection createConnection() throws JMSException {
        return delegate.createConnection();
    }

    @Override
    public Connection createConnection(String userName, String password)
            throws JMSException {
        return delegate.createConnection(userName, password);
    }

    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

}

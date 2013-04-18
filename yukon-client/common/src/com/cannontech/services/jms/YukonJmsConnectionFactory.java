package com.cannontech.services.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.connection.CachingConnectionFactory;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;

public class YukonJmsConnectionFactory implements FactoryBean<ConnectionFactory> {
    private static final Logger log = YukonLogManager.getLogger(YukonJmsConnectionFactory.class);

    public enum ConnectionType {
        NORMAL,
        REMOTE_SERVICES
    }

    @Autowired private ConfigurationSource configurationSource;
    private ConnectionType connectionType = ConnectionType.NORMAL;

    @Override
    public ConnectionFactory getObject() throws Exception {
        ConnectionFactory delegate;

        if (connectionType == ConnectionType.REMOTE_SERVICES) {
            String clientConnection = configurationSource.getString("JMS_REMOTE_SERVICES_CONNECTION",
                "tcp://localhost:61616");
            // Create a ConnectionFactory
            ConnectionFactory factory = new ActiveMQConnectionFactory(clientConnection);

            // if using Spring, create a CachingConnectionFactory
            CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
            cachingFactory.setTargetConnectionFactory(factory);
            delegate = cachingFactory;
            log.debug("created remote services connectionFactory at " + clientConnection);
        } else {
            final String applicationName = BootstrapUtils.getApplicationName();

            if (applicationName.equals("ServiceManager")) {
                String serverListenConnection =
                        configurationSource.getString("JMS_SERVER_BROKER_LISTEN_CONNECTION", "tcp://localhost:61616");
                ServerEmbeddedBroker serverEmbeddedBroker =
                        new ServerEmbeddedBroker(applicationName, serverListenConnection);
                serverEmbeddedBroker.start();
                delegate = serverEmbeddedBroker.getConnectionFactory();
                log.debug("created ServiceManager ConnectionFactory at " + serverListenConnection);
            } else if (!CtiUtilities.isRunningAsClient()) {
                String clientBrokerConnection = configurationSource.getString("JMS_CLIENT_BROKER_CONNECTION",
                    "tcp://localhost:61616");
                ClientEmbeddedBroker clientEmbeddedBroker =
                        new ClientEmbeddedBroker(applicationName, clientBrokerConnection);
                clientEmbeddedBroker.start();
                delegate = clientEmbeddedBroker.getConnectionFactory();
                log.debug("created Server ConnectionFactory at " + clientBrokerConnection);
            } else {
                String clientConnection =
                        configurationSource.getString("JMS_CLIENT_CONNECTION", "tcp://localhost:61616");
                // Create a ConnectionFactory
                ConnectionFactory factory = new ActiveMQConnectionFactory(clientConnection);

                // if using Spring, create a CachingConnectionFactory
                CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
                cachingFactory.setTargetConnectionFactory(factory);
                delegate = cachingFactory;
                log.debug("created Client ConnectionFactory at " + clientConnection);
            }
        }

        return delegate;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public Class<?> getObjectType() {
        return ConnectionFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

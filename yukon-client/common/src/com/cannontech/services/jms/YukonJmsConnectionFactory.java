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
        REMOTE_SERVICES,
        INTERNAL_MESSAGING,
    }

    @Autowired private ConfigurationSource configurationSource;
    private ConnectionType connectionType = ConnectionType.NORMAL;

    @Override
    public ConnectionFactory getObject() throws Exception {
        String serverAddress =
                configurationSource.getString("JMS_BROKER_HOST", 
                                              "localhost");
        String serverListenConnection =
                configurationSource.getString("JMS_SERVER_BROKER_LISTEN_CONNECTION", 
                                              "tcp://" + serverAddress + ":61616");

        switch (connectionType) {
            case REMOTE_SERVICES: {
                String clientConnection = 
                        configurationSource.getString("JMS_REMOTE_SERVICES_CONNECTION",
                                                      serverListenConnection);
                // Create a ConnectionFactory
                ConnectionFactory factory = new ActiveMQConnectionFactory(clientConnection);
    
                // if using Spring, create a CachingConnectionFactory
                CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
                cachingFactory.setTargetConnectionFactory(factory);
                log.debug("created remote services connectionFactory at " + clientConnection);
                return cachingFactory;
            }
            case INTERNAL_MESSAGING: {
                final String applicationName = BootstrapUtils.getApplicationName();
                String connectionBase = serverListenConnection;

                if (applicationName.equals("ServiceManager")) {
                    connectionBase = "vm://ServiceManager?create=false";
                }
                
                String internalMessageConnectionDefault = connectionBase;

                String InternalConnectionString = internalMessageConnectionDefault;
                
                if (!applicationName.equals("ServiceManager")) {
                    InternalConnectionString =
                            configurationSource.getString("JMS_INTERNAL_MESSAGING_CONNECTION", 
                                                          internalMessageConnectionDefault);
                }
                return new ActiveMQConnectionFactory(InternalConnectionString);
            }
            default: 
            case NORMAL: {
                final String applicationName = BootstrapUtils.getApplicationName();
    
                if (applicationName.equals("ServiceManager")) {
                    ServerEmbeddedBroker serverEmbeddedBroker =
                            new ServerEmbeddedBroker(applicationName, serverListenConnection);
                    serverEmbeddedBroker.start();
                    log.debug("created ServiceManager ConnectionFactory at " + serverListenConnection);
                    return serverEmbeddedBroker.getConnectionFactory();
                } 
                if (!CtiUtilities.isRunningAsClient()) {
                    String clientBrokerConnection = 
                            configurationSource.getString("JMS_CLIENT_BROKER_CONNECTION",
                                                          serverListenConnection);
                    ClientEmbeddedBroker clientEmbeddedBroker =
                            new ClientEmbeddedBroker(applicationName, clientBrokerConnection);
                    clientEmbeddedBroker.start();
                    log.debug("created Server ConnectionFactory at " + clientBrokerConnection);
                    return clientEmbeddedBroker.getConnectionFactory();
                } 
                String clientConnection =
                        configurationSource.getString("JMS_CLIENT_CONNECTION", 
                                                      serverListenConnection);
                // Create a ConnectionFactory
                ConnectionFactory factory = new ActiveMQConnectionFactory(clientConnection);

                // if using Spring, create a CachingConnectionFactory
                CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
                cachingFactory.setTargetConnectionFactory(factory);
                log.debug("created Client ConnectionFactory at " + clientConnection);
                return cachingFactory;
            }
        }
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

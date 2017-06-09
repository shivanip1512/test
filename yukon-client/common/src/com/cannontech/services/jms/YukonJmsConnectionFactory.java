package com.cannontech.services.jms;

import static com.cannontech.common.config.MasterConfigString.JMS_CLIENT_BROKER_CONNECTION;
import static com.cannontech.common.config.MasterConfigString.JMS_CLIENT_CONNECTION;
import static com.cannontech.common.config.MasterConfigString.JMS_INTERNAL_MESSAGING_CONNECTION;
import static com.cannontech.common.config.MasterConfigString.JMS_SERVER_BROKER_LISTEN_CONNECTION;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class YukonJmsConnectionFactory implements FactoryBean<ConnectionFactory> {
    
    private static final long DEFAULT_WAIT_FOR_START_MILLIS = 120000; // 2 minutes - forces the VM transport to wait till the broker is started.
    
    private static final Logger log = YukonLogManager.getLogger(YukonJmsConnectionFactory.class);

    public enum ConnectionType {
        NORMAL,
        INTERNAL_MESSAGING,
    }
    
    static {
        System.setProperty(
            "org.apache.activemq.SERIALIZABLE_PACKAGES",
            "java.lang,javax.security,java.util,javax.jms,com.thoughtworks.xstream.mapper,org.joda,com.google.common.collect,com.cannontech");
    }
    
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;

    private ConnectionType connectionType = ConnectionType.NORMAL;

    @Override
    public ConnectionFactory getObject() throws Exception {
        String hostUri = "tcp://" + globalSettingDao.getString(GlobalSettingType.JMS_BROKER_HOST);
        Integer port = globalSettingDao.getNullableInteger(GlobalSettingType.JMS_BROKER_PORT);

        // MaxInactivityDuration controls how long AMQ keeps a socket open when it's not heard from
        // it.
        String maxInactivityDuration =
            "wireFormat.MaxInactivityDuration=" +
                    (globalSettingDao.getInteger(GlobalSettingType.MAX_INACTIVITY_DURATION) * 1000);

        String jmsHost = hostUri + (port == null ? ":61616" : ":" + port) + "?" + maxInactivityDuration;

        String serverListenConnection
            = configurationSource.getString(JMS_SERVER_BROKER_LISTEN_CONNECTION, jmsHost);

        switch (connectionType) {
            case INTERNAL_MESSAGING: {
                final String applicationName = BootstrapUtils.getApplicationName();
               
                String internalMessageConnection;
                
                if (applicationName.equals("ServiceManager")) {
                    long waitForStartMillis = DEFAULT_WAIT_FOR_START_MILLIS;
                    internalMessageConnection = "vm://ServiceManager?create=false&waitForStart=" + waitForStartMillis;
                } else {
                    internalMessageConnection = 
                            configurationSource.getString(JMS_INTERNAL_MESSAGING_CONNECTION, serverListenConnection);
                }
                
                log.info("created internal messaging connectionFactory at " + internalMessageConnection);
                return new ActiveMQConnectionFactory(internalMessageConnection);
            }
            default: 
            case NORMAL: {
                final String applicationName = BootstrapUtils.getApplicationName();
                
                if (applicationName.equals("MessageBroker")) {
                    long waitForStartMillis = DEFAULT_WAIT_FOR_START_MILLIS;
                    return new ActiveMQConnectionFactory("vm://YukonMessageBroker?create=false&waitForStart=" + waitForStartMillis);
                }
                
                if (!CtiUtilities.isRunningAsClient()) {
                    String appName = applicationName;
                    try {
                        appName = applicationName + InetAddress.getLocalHost().getHostName();
                    } catch (UnknownHostException e) {
                        log.info("Could not resolve host name.");
                    }
                    
                    String clientBrokerConnection = 
                            configurationSource.getString(JMS_CLIENT_BROKER_CONNECTION, serverListenConnection);
                    ClientEmbeddedBroker clientEmbeddedBroker =
                            new ClientEmbeddedBroker(appName, clientBrokerConnection);
                    clientEmbeddedBroker.start();
                    log.info("created Server ConnectionFactory at " + clientBrokerConnection);
                    return clientEmbeddedBroker.getConnectionFactory();
                } 
                String clientConnection =
                        configurationSource.getString(JMS_CLIENT_CONNECTION, serverListenConnection);
                // Create a ConnectionFactory
                ConnectionFactory factory = new ActiveMQConnectionFactory(clientConnection);

                // if using Spring, create a CachingConnectionFactory
                CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
                cachingFactory.setTargetConnectionFactory(factory);
                log.info("created Client ConnectionFactory at " + clientConnection);
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

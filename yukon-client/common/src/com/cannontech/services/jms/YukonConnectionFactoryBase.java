package com.cannontech.services.jms;

import static com.cannontech.common.config.MasterConfigString.JMS_CLIENT_BROKER_CONNECTION;
import static com.cannontech.common.config.MasterConfigString.JMS_SERVER_BROKER_LISTEN_CONNECTION;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

abstract class YukonConnectionFactoryBase {
    
    static final Logger log = YukonLogManager.getLogger(YukonJmsConnectionFactory.class);

    static private final String[] SERIALIZABLE_PACKAGE_NAMES = {
            "java.lang",
            "javax.security",
            "java.util",
            "javax.jms",
            "com.thoughtworks.xstream.mapper",
            "org.joda",
            "com.google.common.collect",
            "com.cannontech",
            "sun.util"
    };
    
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired protected ConfigurationSource configurationSource;
    private static ClientEmbeddedBroker clientEmbeddedBroker;
    
    static {
        System.setProperty(
            "org.apache.activemq.SERIALIZABLE_PACKAGES",
            String.join(",", SERIALIZABLE_PACKAGE_NAMES));
    }
    
    protected String getServerListenConnection() throws Exception {
        String hostUri = "tcp://" + globalSettingDao.getString(GlobalSettingType.JMS_BROKER_HOST);
        Integer port = globalSettingDao.getNullableInteger(GlobalSettingType.JMS_BROKER_PORT);

        // MaxInactivityDuration controls how long AMQ keeps a socket open when it has had no activity.
        String maxInactivityDuration =
            "wireFormat.MaxInactivityDuration=" +
                    (globalSettingDao.getInteger(GlobalSettingType.MAX_INACTIVITY_DURATION) * 1000);

        String jmsHost = hostUri + (port == null ? ":61616" : ":" + port) + "?" + maxInactivityDuration;

        String serverListenConnection
            = configurationSource.getString(JMS_SERVER_BROKER_LISTEN_CONNECTION, jmsHost);
        
        return serverListenConnection;
    }

    /** 
     * Initializes the ClientEmbeddedBroker on demand.
     * @param applicationId
     * @param serverListenConnection
     * @return
     */
    protected synchronized ClientEmbeddedBroker getClientEmbeddedBroker(ApplicationId applicationId, String serverListenConnection) {
        if (clientEmbeddedBroker == null) {
            String clientBrokerConnection = configurationSource.getString(JMS_CLIENT_BROKER_CONNECTION, serverListenConnection);
            clientEmbeddedBroker = new ClientEmbeddedBroker(applicationId, clientBrokerConnection);
            clientEmbeddedBroker.start();
            log.info("created Client Embedded Broker at " + clientBrokerConnection);
        }
        return clientEmbeddedBroker;    
    }
}

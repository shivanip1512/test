package com.cannontech.common.util.jmx;

import static com.cannontech.common.config.MasterConfigString.JMS_CLIENT_BROKER_CONNECTION;
import static com.cannontech.common.config.MasterConfigString.JMS_SERVER_BROKER_LISTEN_CONNECTION;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class JmxConnectorHelper {
    private static final Logger log = YukonLogManager.getLogger(JmxConnectorHelper.class);

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ConfigurationSource config;

    private JMXServiceURL messageBrokerServiceUrl;
    private JMXConnector messageBrokerJmxConnector;
    private JMXServiceURL serviceManagerServiceUrl;
    private JMXConnector serviceManagerJmxConnector;

    /*
     * Initialize JMXServiceURL & JMXConnector for both Yukon Message Broker and Service Maager.
     * JMX port for Message Broker : 1097
     * JMX port for Service Manager Broker : 1093
     */
    @PostConstruct
    public void init() {

        String hostUri = globalSettingDao.getString(GlobalSettingType.JMS_BROKER_HOST);
        String serverBrokerConnection = config.getString(JMS_SERVER_BROKER_LISTEN_CONNECTION);

        try {
            if (serverBrokerConnection != null) {
                hostUri = StringUtils.substringBetween(serverBrokerConnection, "//", ":");
            }
            String messageBrokerJMXConnectionUrl = buildJmxConnectionUrl(hostUri, ApplicationId.MESSAGE_BROKER);
            messageBrokerServiceUrl = new JMXServiceURL(messageBrokerJMXConnectionUrl);
            messageBrokerJmxConnector = JMXConnectorFactory.connect(messageBrokerServiceUrl);
        } catch (IOException e) {
            log.error("Could not init jmx connection for Yukon Message Broker.");
        }

        try {
            String clientBrokerConnection = config.getString(JMS_CLIENT_BROKER_CONNECTION);
            if (clientBrokerConnection != null) {
                hostUri = StringUtils.substringBetween(clientBrokerConnection, "//", ":");
            }
            String serviceManagerJMXConnectionUrl = buildJmxConnectionUrl(hostUri, ApplicationId.SERVICE_MANAGER);
            serviceManagerServiceUrl = new JMXServiceURL(serviceManagerJMXConnectionUrl);
            serviceManagerJmxConnector = JMXConnectorFactory.connect(serviceManagerServiceUrl);
        } catch (IOException e) {
            log.error("Could not init jmx connection for Service Manager.");
        }
    }

    private String buildJmxConnectionUrl(String hostUri, ApplicationId application) {
        return "service:jmx:rmi:///jndi/rmi://" + hostUri + ":" + JmxHelper.getApplicationJmxPort(application) + "/jmxrmi";
    }

    /**
     * Return JMXServiceURL for the application specified by ApplicationId
     */
    public JMXServiceURL getJMXServiceURL(ApplicationId applicationId) {
        switch (applicationId) {
        case MESSAGE_BROKER:
            return messageBrokerServiceUrl;
        case SERVICE_MANAGER:
            return serviceManagerServiceUrl;
        default:
            throw new IllegalArgumentException("Not implemented for " + applicationId);
        }
    }

    /**
     * Return JMXConnector for the application specified by ApplicationId
     */
    public JMXConnector getJMXConnector(ApplicationId applicationId) {
        switch (applicationId) {
        case MESSAGE_BROKER:
            return messageBrokerJmxConnector;
        case SERVICE_MANAGER:
            return serviceManagerJmxConnector;
        default:
            throw new IllegalArgumentException("Not implemented for " + applicationId);
        }
    }
}

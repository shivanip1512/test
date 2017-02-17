package com.cannontech.web.dev.service;

import static com.cannontech.common.config.MasterConfigString.JMS_SERVER_BROKER_LISTEN_CONNECTION;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.support.service.impl.BeanTypeForJMXConnector;

public class YsmJmxQueryService {

    private static final Logger log = YukonLogManager.getLogger(YsmJmxQueryService.class);
    /*
     * JMX port for Message Broker : 1097
     * JMX port for Service Manager Broker : 1099
     */
    private static final String serviceManagerJMXConnectionUrl = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";

    private JMXServiceURL messageBrokerServiceUrl;
    private JMXConnector messageBrokerJmxConnector;
    private JMXServiceURL serviceManagerServiceUrl;
    private JMXConnector serviceManagerJmxConnector;

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ConfigurationSource config;
    
    public Object get(ObjectName name, String attribute , JMXServiceURL serviceUrl, JMXConnector jmxConnector) throws Exception {
        
        if (config.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE, false)) {
            try {
                
                MBeanServerConnection mbeanConn = jmxConnector.getMBeanServerConnection();
                Object object = mbeanConn.getAttribute(name, attribute);
                
                return object;
            } catch (InstanceNotFoundException e) {
                //The requested attribute wasn't found. No need to reconnect in this case.
                log.debug("Instance not found. Name: " + name + ", Attribute: " + attribute);
                return null;
            } catch (Exception e) {
                try {
                    // Try to reconnect, maybe YSM was restarted.
                    jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);
                    
                    MBeanServerConnection mbeanConn = jmxConnector.getMBeanServerConnection();
                    Object object = mbeanConn.getAttribute(name, attribute);
                    
                    return object;
                } catch (Exception e2) {
                    log.error("Could not retrieve value.", e2);
                    return null;
                }
            }
        } else {
            return null;
        }
    }
    
    public <T> T getTypedValue(ObjectName name, String attribute, T defaultValue, Class<T> returnType, BeanTypeForJMXConnector beanType) throws Exception {
        
        Object returnObject = null;
        if (beanType == BeanTypeForJMXConnector.QUEUE) {
            returnObject = get(name, attribute, messageBrokerServiceUrl, messageBrokerJmxConnector);
        } else if (beanType == BeanTypeForJMXConnector.SERVICE) {
            returnObject = get(name, attribute, serviceManagerServiceUrl, serviceManagerJmxConnector);
        }
        if (returnObject == null) {
            return defaultValue;
        } else {
            return returnType.cast(returnObject);
        }
    }

    
    @PostConstruct
    public void init() {

        String hostUri;
        if (config.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE, false)) {
            try {
                String serverListenConnection = config.getString(JMS_SERVER_BROKER_LISTEN_CONNECTION);
                if (serverListenConnection != null) {
                    hostUri = StringUtils.substringBetween(serverListenConnection, "//", ":");
                } else {
                    hostUri = globalSettingDao.getString(GlobalSettingType.JMS_BROKER_HOST);
                }
                String messageBrokerJMXConnectionUrl = "service:jmx:rmi:///jndi/rmi://" + hostUri + ":1097/jmxrmi";
                messageBrokerServiceUrl = new JMXServiceURL(messageBrokerJMXConnectionUrl);
                messageBrokerJmxConnector = JMXConnectorFactory.connect(messageBrokerServiceUrl, null);
            } catch (IOException e) {
                log.error("Could not init jmx connection for Yukon Message Broker.");
            }

            try {
                serviceManagerServiceUrl = new JMXServiceURL(serviceManagerJMXConnectionUrl);
                serviceManagerJmxConnector = JMXConnectorFactory.connect(serviceManagerServiceUrl, null);
            } catch (IOException e) {
                log.error("Could not init jmx connection for Service Manager.");
            }
        }
    }

}
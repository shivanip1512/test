package com.cannontech.web.dev.service;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;

public class YsmJmxQueryService {

    private static final Logger log = YukonLogManager.getLogger(YsmJmxQueryService.class);
    /*
     * JMX port for Broker : 1097
     * JMX port for Service Manager Broker : 1099
     */
    private static final String mbService = "service:jmx:rmi:///jndi/rmi://localhost:1097/jmxrmi";
    private static final String smService = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";

    private JMXServiceURL mbServiceUrl;
    private JMXConnector mbJmxConnector;
    private JMXServiceURL smServiceUrl;
    private JMXConnector smJmxConnector;

    private @Autowired ConfigurationSource config;
    
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
    
    public <T> T getTypedValue(ObjectName name, String attribute, T defaultValue, Class<T> returnType, String beanType) throws Exception {
        
        Object returnObject = null;
        if (beanType == "queue") {
            returnObject = get(name, attribute, mbServiceUrl, mbJmxConnector);
        } else if (beanType == "service") {
            returnObject = get(name, attribute, smServiceUrl, smJmxConnector);
        }
        if (returnObject == null) {
            return defaultValue;
        } else {
            return returnType.cast(returnObject);
        }
    }

    
    @PostConstruct
    public void init() {
        if (config.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE, false)) {
            try {
                mbServiceUrl = new JMXServiceURL(mbService);
                mbJmxConnector = JMXConnectorFactory.connect(mbServiceUrl, null);
            } catch (IOException e) {
                log.error("Could not init jmx connection for Yukon Message Broker.");
            }

            try {
                smServiceUrl = new JMXServiceURL(smService);
                smJmxConnector = JMXConnectorFactory.connect(smServiceUrl, null);
            } catch (IOException e) {
                log.error("Could not init jmx connection for Service Manager.");
            }
        }
    }

}
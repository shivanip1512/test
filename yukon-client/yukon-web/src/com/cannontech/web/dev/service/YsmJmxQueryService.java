package com.cannontech.web.dev.service;

import java.io.IOException;

import javax.annotation.PostConstruct;
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
    private static final String service = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
    
    private JMXServiceURL serviceUrl;
    private JMXConnector jmxConnector;
    
    private @Autowired ConfigurationSource config;
    
    public Object get(ObjectName name, String attribute) throws Exception {
        
        if (config.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE, false)) {
            try {
                
                MBeanServerConnection mbeanConn = jmxConnector.getMBeanServerConnection();
                Object object = mbeanConn.getAttribute(name, attribute);
                
                return object;
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
    
    @PostConstruct
    public void init() {
        if (config.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE, false)) {
            try {
                serviceUrl = new JMXServiceURL(service);
                jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);
            } catch (IOException e) {
                log.error("Could not init jmx connection", e);
            }
        }
    }
    
}
package com.cannontech.web.dev.service;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public class YsmJmxQueryService {

    private static final Logger log = YukonLogManager.getLogger(YsmJmxQueryService.class);
    private static final String service = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
    
    private JMXServiceURL serviceUrl;
    private JMXConnector jmxConnector;
    
    public Object get(ObjectName name, String attribute) throws Exception {
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
                log.error("Could retrieve value.", e2);
                throw e;
            }
        }
    }
    
    @PostConstruct
    public void init() {
        try {
            serviceUrl = new JMXServiceURL(service);
            jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);
        } catch (IOException e) {
            log.error("Could not init jmx connection", e);
        }
    }
}
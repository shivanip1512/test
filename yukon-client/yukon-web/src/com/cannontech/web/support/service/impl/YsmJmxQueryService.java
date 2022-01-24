package com.cannontech.web.support.service.impl;

import javax.annotation.PostConstruct;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.jmx.JmxConnectorHelper;

public class YsmJmxQueryService {

    private static final Logger log = YukonLogManager.getLogger(YsmJmxQueryService.class);
    @Autowired private JmxConnectorHelper helper;

    private JMXServiceURL messageBrokerServiceUrl;
    private JMXConnector messageBrokerJmxConnector;
    private JMXServiceURL serviceManagerServiceUrl;
    private JMXConnector serviceManagerJmxConnector;

    @PostConstruct
    public void init() {
        messageBrokerServiceUrl = helper.getJMXServiceURL(ApplicationId.MESSAGE_BROKER);
        serviceManagerServiceUrl = helper.getJMXServiceURL(ApplicationId.SERVICE_MANAGER);
        messageBrokerJmxConnector = helper.getJMXConnector(ApplicationId.MESSAGE_BROKER);
        serviceManagerJmxConnector = helper.getJMXConnector(ApplicationId.SERVICE_MANAGER);
    }

    public Object get(ObjectName name, String attribute, JMXConnector jmxConnector, BeanTypeForJMXConnector beanType) throws Exception {
        
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
                // Try to reconnect, maybe YSM or MessageBroker was restarted.
                MBeanServerConnection mbeanConn = null;
                if (beanType == BeanTypeForJMXConnector.QUEUE) {
                    messageBrokerJmxConnector = JMXConnectorFactory.connect(messageBrokerServiceUrl, null);
                    mbeanConn = messageBrokerJmxConnector.getMBeanServerConnection();
                } else if (beanType == BeanTypeForJMXConnector.SERVICE) {
                    serviceManagerJmxConnector = JMXConnectorFactory.connect(serviceManagerServiceUrl, null);
                    mbeanConn = serviceManagerJmxConnector.getMBeanServerConnection();
                }
                Object object = mbeanConn.getAttribute(name, attribute);
                return object;
            } catch (Exception e2) {
                jmxConnector.close();
                log.error("Could not retrieve value.", e2);
                return null;
            }
        }
    }
    
    public <T> T getTypedValue(ObjectName name, String attribute, T defaultValue, Class<T> returnType, BeanTypeForJMXConnector beanType) throws Exception {
        
        Object returnObject = null;
        if (beanType == BeanTypeForJMXConnector.QUEUE) {
            returnObject = get(name, attribute, messageBrokerJmxConnector, beanType);
        } else if (beanType == BeanTypeForJMXConnector.SERVICE) {
            returnObject = get(name, attribute, serviceManagerJmxConnector, beanType);
        }
        if (returnObject == null) {
            return defaultValue;
        } else {
            return returnType.cast(returnObject);
        }
    }
}
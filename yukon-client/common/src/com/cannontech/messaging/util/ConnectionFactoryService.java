package com.cannontech.messaging.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ConnectionFactoryService {

    private static ConnectionFactoryService instance;

    @Autowired
    private BeanFactory beanFactory;
    private Map<String, String> connectionFactoryMap;
    private Map<String, String> listenerConnectionFactoryMap;

    private ConnectionFactoryService() {
        connectionFactoryMap = new HashMap<String, String>();
        listenerConnectionFactoryMap = new HashMap<String, String>();
        instance = this;
    }

    public ConnectionFactory findConnectionFactory(String name) {
        String beanName = connectionFactoryMap.get(name);

        if (beanName == null) {
            throw new RuntimeException("Can not find connection factory bean definition for \"" + name + "\"");
        }

        ConnectionFactory factory = beanFactory.getBean(beanName, ConnectionFactory.class);
        if (factory == null) {
            throw new RuntimeException("Can not create connection factory bean  \"" + beanName + "\"");
        }

        return factory;
    }

    public ListenerConnectionFactory findListenerConnectionFactory(String name) {
        String beanName = listenerConnectionFactoryMap.get(name);

        if (beanName == null) {
            throw new RuntimeException("Can not find listener connection factory bean definition for \"" + name + "\"");
        }

        ListenerConnectionFactory factory = beanFactory.getBean(beanName, ListenerConnectionFactory.class);
        if (factory == null) {
            throw new RuntimeException("Can not create listener connection factory bean  \"" + beanName + "\"");
        }

        return factory;
    }

    public static ConnectionFactoryService getInstance() {
        return instance;
    }

    public static class ConnectionFactoryServiceInitializer {
        private ConnectionFactoryServiceInitializer(ConnectionFactoryService factoryService,
                                                    Map<String, String> connectionFactoryMap,
                                                    Map<String, String> listenerConnectionFactoryMap) {

            if (connectionFactoryMap != null) {
                factoryService.connectionFactoryMap.putAll(connectionFactoryMap);
            }

            if (listenerConnectionFactoryMap != null) {
                factoryService.listenerConnectionFactoryMap.putAll(listenerConnectionFactoryMap);
            }
        }
    }
}

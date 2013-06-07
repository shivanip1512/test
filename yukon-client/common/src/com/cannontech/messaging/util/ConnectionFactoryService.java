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
    }

    public ConnectionFactory findConnectionFactory(String name) {
        String beanName = connectionFactoryMap.get(name);

        if (beanName == null) {
            throw new RuntimeException("Can not find connection factory bean definition for \"" + name + "\"");
        }

        ConnectionFactory factory = (ConnectionFactory) beanFactory.getBean(beanName);
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

        ListenerConnectionFactory factory = (ListenerConnectionFactory) beanFactory.getBean(beanName);
        if (factory == null) {
            throw new RuntimeException("Can not create listener connection factory bean  \"" + beanName + "\"");
        }

        return factory;
    }

    public void setConnectionFactoryMap(Map<String, String> connectionFactoryMap) {
        this.connectionFactoryMap = connectionFactoryMap;
    }

    public static ConnectionFactoryService getInstance() {
        return instance;
    }

    public void init() {
        instance = this;
    }

    public static class ConnectionFactoryServiceInitializer {

        @Autowired
        private ConnectionFactoryService factoryService;

        private Map<String, String> factoryMapToInitialize;
        private Map<String, String> listenerFactoryMapToInitialize;

        private ConnectionFactoryServiceInitializer(Map<String, String> connectionFactoryMap,
                                                    Map<String, String> listenerConnectionFactoryMap) {
            this.factoryMapToInitialize = connectionFactoryMap;
            this.listenerFactoryMapToInitialize = listenerConnectionFactoryMap;
        }

        public void init() {
            if (factoryMapToInitialize != null) {
                factoryService.connectionFactoryMap.putAll(this.factoryMapToInitialize);
            }
            
            if (listenerFactoryMapToInitialize != null) {
                factoryService.listenerConnectionFactoryMap.putAll(this.listenerFactoryMapToInitialize);
            }
        }
    }
}

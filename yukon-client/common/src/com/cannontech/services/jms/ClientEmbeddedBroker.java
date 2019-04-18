package com.cannontech.services.jms;

import java.net.URI;
import java.net.URISyntaxException;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.network.NetworkConnector;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.connection.CachingConnectionFactory;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.activemq.ActiveMQHelper;
import com.cannontech.common.util.jmx.JmxHelper;

public class ClientEmbeddedBroker {
	
    private static Logger log = YukonLogManager.getLogger(ClientEmbeddedBroker.class);

    private final ApplicationId applicationId;
    private final String connectionString;
    
    /**
     * @param JmsHelper.resolveBrokerName(applicationId) name used for the broker, mostly for debug
     * @param connectionString something like "tcp://10.10.10.10:61616"
     */
    public ClientEmbeddedBroker(ApplicationId applicationId, String connectionString) {
        this.applicationId = applicationId;
        this.connectionString = connectionString;
    }
    
    /**
     * Should only be called once.
     * @return a pooled ConnectionFactory
     * @throws URISyntaxException 
     */
    public ConnectionFactory getCachingConnectionFactory() throws URISyntaxException {
        // Create a ConnectionFactory
        ConnectionFactory factory = getConnectionFactory();
        
        // if using Spring, create a CachingConnectionFactory
        CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
        cachingFactory.setCacheConsumers(false);
        cachingFactory.setTargetConnectionFactory(factory);
        cachingFactory.afterPropertiesSet();
        factory = cachingFactory;
        return factory;
    }

    public void start() {
        try {
            BrokerService broker = new BrokerService();
            broker.getPersistenceAdapter().setDirectory(ActiveMQHelper.getKahaDbDirectory(applicationId));
            broker.setBrokerName(ActiveMQHelper.resolveBrokerName(applicationId));
            String discoveryAddress = "static:(" + connectionString + ")";
            
            //  Connect to Yukon Message Broker
            NetworkConnector networkConnector = broker.addNetworkConnector(discoveryAddress);
            networkConnector.setDuplex(true);
            
            //  Our largest route is currently 4 hops:
            //    Network Manager -> remote proxy -> local proxy -> broker -> web
            networkConnector.setNetworkTTL(5);
            
            Integer jmxPort = JmxHelper.getApplicationJmxPort(applicationId);
            
            if (jmxPort != null) {
                broker.setUseJmx(true);
                broker.getManagementContext().setConnectorPort(jmxPort);
            } else {
                log.warn("No JMX port found for application " + applicationId);
            }
            
            broker.start();
        } catch (Exception e) {
        	log.warn("Caught exception starting client broker: " + e.toString());
            throw new RuntimeException("Unable to start broker", e);
        }
    }

    public ActiveMQConnectionFactory getConnectionFactory() throws URISyntaxException {
        URI vmTransportUri = ActiveMQHelper.createVmTransportUri(applicationId);

        // Create a ConnectionFactory
        return new ActiveMQConnectionFactory(vmTransportUri);
    }
}

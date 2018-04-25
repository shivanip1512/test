package com.cannontech.services.jms;

import static com.cannontech.common.config.MasterConfigString.JMS_CLIENT_CONNECTION;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jms.connection.CachingConnectionFactory;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.activemq.ActiveMQHelper;

public class YukonJmsConnectionFactory extends YukonConnectionFactoryBase implements FactoryBean<ConnectionFactory> {
    
    static final Logger log = YukonLogManager.getLogger(YukonJmsConnectionFactory.class);

    @Override
    public ConnectionFactory getObject() throws Exception {
        String serverListenConnection = getServerListenConnection();

        final ApplicationId applicationId = BootstrapUtils.getApplicationId();
        
        //  Create a non-caching connection factory for the broker - it's just a VM transport, no reason to reuse/serialize on the connection
        if (applicationId == ApplicationId.MESSAGE_BROKER) {
            return new ActiveMQConnectionFactory(ActiveMQHelper.createVmTransportUri(applicationId));
        }
        
        if (!CtiUtilities.isRunningAsClient()) {
            //  Get the connection factory from the embedded broker
            return getClientEmbeddedBroker(applicationId, serverListenConnection).getCachingConnectionFactory();
        } 
        
        String clientConnection = configurationSource.getString(JMS_CLIENT_CONNECTION, serverListenConnection);
        // Create a ConnectionFactory
        ConnectionFactory factory = new ActiveMQConnectionFactory(clientConnection);

        // if using Spring, create a CachingConnectionFactory
        CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
        cachingFactory.setTargetConnectionFactory(factory);
        log.info("created Client ConnectionFactory at " + clientConnection);
        return cachingFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return ConnectionFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

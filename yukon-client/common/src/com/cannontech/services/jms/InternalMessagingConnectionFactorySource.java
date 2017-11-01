package com.cannontech.services.jms;

import static com.cannontech.common.config.MasterConfigString.JMS_INTERNAL_MESSAGING_CONNECTION;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;

public class InternalMessagingConnectionFactorySource extends YukonConnectionFactoryBase implements FactoryBean<InternalMessagingConnectionFactory> {
    
    private static final Logger log = YukonLogManager.getLogger(InternalMessagingConnectionFactorySource.class);

    @Override
    public InternalMessagingConnectionFactory getObject() throws Exception {
        final String serverListenConnection = getServerListenConnection();
        
        final ApplicationId applicationId = BootstrapUtils.getApplicationId();
        
        ActiveMQConnectionFactory amqConnectionFactory;
        
        if (!CtiUtilities.isRunningAsClient()) {
            amqConnectionFactory = getClientEmbeddedBroker(applicationId, serverListenConnection).getConnectionFactory();
        } else {
            String internalMessageConnection = configurationSource.getString(JMS_INTERNAL_MESSAGING_CONNECTION, serverListenConnection);
            amqConnectionFactory = new ActiveMQConnectionFactory(internalMessageConnection);
        }
        
        InternalMessagingConnectionFactory imcFactory = new InternalMessagingConnectionFactory(amqConnectionFactory);

        log.info("created internal messaging connectionFactory at " + imcFactory.getBrokerURL());
        
        return imcFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return InternalMessagingConnectionFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

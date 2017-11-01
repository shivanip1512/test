package com.cannontech.message.service;

import static com.cannontech.common.config.MasterConfigString.JMS_SERVER_BROKER_LISTEN_CONNECTION;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.activemq.ActiveMQHelper;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class BrokerService {
    
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    /**
     * Gets this broker service as a Spring bean and starts it.
     */
    public static void main(String[] args) {
        BootstrapUtils.setApplicationName(ApplicationId.MESSAGE_BROKER);
        YukonSpringHook.setDefaultContext(YukonSpringHook.BROKER_BEAN_FACTORY_KEY);
        Logger log = YukonLogManager.getLogger(BrokerService.class);
        try {
            log.info("Starting broker service from main method");
            BrokerService service = YukonSpringHook.getBean(BrokerService.class);
            service.start();
            log.info("Started broker service.");
        } catch (Throwable t) {
            log.error("Error in broker service", t);
            System.exit(1);
        }
    }
    
    private synchronized void start() {
        String hostUri = "tcp://" + globalSettingDao.getString(GlobalSettingType.JMS_BROKER_HOST);
        Integer port = globalSettingDao.getNullableInteger(GlobalSettingType.JMS_BROKER_PORT);

        // MaxInactivityDuration controls how long AMQ keeps a socket open when it's not heard from
        // it.
        String maxInactivityDuration =
            "wireFormat.MaxInactivityDuration=" +
                    (globalSettingDao.getInteger(GlobalSettingType.MAX_INACTIVITY_DURATION) * 1000);

        String jmsHost = hostUri + (port == null ? ":61616" : ":" + port) + "?" + maxInactivityDuration;

        String serverListenConnection
            = configurationSource.getString(JMS_SERVER_BROKER_LISTEN_CONNECTION, jmsHost);
        
        
        Broker broker =
                new Broker(ActiveMQHelper.MESSAGE_BROKER_NAME, serverListenConnection);
        broker.start();
    }
}

package com.cannontech.message.service;

import static com.cannontech.common.config.MasterConfigString.JMS_SERVER_BROKER_LISTEN_CONNECTION;

import org.apache.commons.lang3.RegExUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class BrokerService {
    
    private static ApplicationId APPLICATION_ID = ApplicationId.MESSAGE_BROKER;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    /**
     * Gets this broker service as a Spring bean and starts it.
     */
    public static void main(String[] args) {
        BootstrapUtils.setApplicationName(APPLICATION_ID);
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
        String serverListenConnection =
                configurationSource.getOptionalString(JMS_SERVER_BROKER_LISTEN_CONNECTION)
                    .map(str -> RegExUtils.replaceFirst(str, "^tcp://", "auto://"))
                    .orElseGet(() -> {
                        String hostUri = "auto://" + globalSettingDao.getString(GlobalSettingType.JMS_BROKER_HOST);
                        Integer port = globalSettingDao.getOptionalInteger(GlobalSettingType.JMS_BROKER_PORT).orElse(61616);

                        // MaxInactivityDuration controls how long AMQ keeps a socket open when it's not heard from
                        // it.
                        String maxInactivityDuration =
                            "wireFormat.MaxInactivityDuration=" +
                                    (globalSettingDao.getInteger(GlobalSettingType.MAX_INACTIVITY_DURATION) * 1000);

                        return hostUri + ":" + port + "?" + maxInactivityDuration;
                    });
        
        new Broker(APPLICATION_ID, serverListenConnection).start();
    }
}

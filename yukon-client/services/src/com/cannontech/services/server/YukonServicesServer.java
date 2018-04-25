package com.cannontech.services.server;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.services.YukonServiceManager;
import com.cannontech.spring.YukonSpringHook;

public class YukonServicesServer {
    public static void main(String[] args) {
        BootstrapUtils.setApplicationName(ApplicationId.SERVICE_MANAGER);
        YukonSpringHook.setDefaultContext(YukonSpringHook.SERVICES_BEAN_FACTORY_KEY);
        Logger log = YukonLogManager.getLogger(YukonServicesServer.class);
        try {
            YukonServiceManager serviceManager = YukonSpringHook.getBean(YukonServiceManager.class);

            serviceManager.waitForShutdown();
            log.info("main thread done");
        } catch (Throwable t) {
            log.error("Problem with loading services", t);
        }
    }
}

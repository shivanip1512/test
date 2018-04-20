package com.cannontech.watchdog;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.spring.YukonSpringHook;

public class WatchdogService {

    public static void main(String args[]) {

        CtiUtilities.setClientAppName(ApplicationId.WATCHDOG);
        YukonSpringHook.setDefaultContext(YukonSpringHook.WATCHDOG_BEAN_FACTORY_KEY);
        Logger log = YukonLogManager.getLogger(WatchdogService.class);

        try {
            log.info("Starting watchdog service from main method");
            WatchdogService service = YukonSpringHook.getBean(WatchdogService.class);
            service.start();
            log.info("Started watchdog service.");
        } catch (Throwable t) {
            log.error("Error in watchdog service", t);
            System.exit(1);
        }
    }

    private synchronized void start() {
       // Start watchdog service
    }

}

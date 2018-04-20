package com.cannontech.monitor.service;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.spring.YukonSpringHook;

public class MonitorService {

    public static void main(String args[]) {

        CtiUtilities.setClientAppName(ApplicationId.MONITOR);
        YukonSpringHook.setDefaultContext(YukonSpringHook.MONITOR_BEAN_FACTORY_KEY);
        Logger log = YukonLogManager.getLogger(MonitorService.class);

        try {
            log.info("Starting monitor service from main method");
            MonitorService service = YukonSpringHook.getBean(MonitorService.class);
            service.start();
            log.info("Started monitor service.");
        } catch (Throwable t) {
            log.error("Error in monitor service", t);
            System.exit(1);
        }
    }

    private synchronized void start() {
       // Start monitor service
    }

}

package com.cannontech.watchdog;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.watchdog.base.Watchdog;

public class WatchdogService {

    private List<Watchdog> watchdog;
    
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
    
    private synchronized void start() throws Exception {
        // TODO: This is temporary code, this will have to be replaced
        // Can create separate thread for each watchdog

        watchdog.stream().forEach(watcher -> {
            if (watcher.shouldRun()) {
                watcher.start();
            }
        });
    }
    

    @Autowired
    void setMonitors(List<Watchdog> watchdog) {
        this.watchdog = watchdog;
    }

}

package com.cannontech.watchdog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.watchdog.base.Watchdog;

public class WatchdogService {

    private static class LogHolder {
        static final Logger log = YukonLogManager.getLogger(WatchdogService.class);
    }

    private List<Watchdog> watchdog;
    private static ScheduledFuture<?> schdfuture;
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    
    public static void main(String args[]) {
        CtiUtilities.setClientAppName(ApplicationId.WATCHDOG);
        YukonSpringHook.setDefaultContext(YukonSpringHook.WATCHDOG_BEAN_FACTORY_KEY);
        try {
            getLogger().info("Starting watchdog service from main method");
            WatchdogService service = YukonSpringHook.getBean(WatchdogService.class);
            service.start();
            getLogger().info("Started watchdog service.");
        } catch (Throwable t) {
            getLogger().error("Error in watchdog service", t);
            System.exit(1);
        }
    }
    
    /*
     * This method checks and starts watchdogs, if the watchdog should to be started.
     * It attempts to start watchdog daily until all watchdogs have started.
     */
    private synchronized void start() throws Exception {
        schdfuture = executor.scheduleAtFixedRate(() -> {
            List<Watchdog> startedWatchdogs = new ArrayList<Watchdog>();
            // Cancel the daily scheduler if there are no more watchdog service to start 
            if (watchdog.isEmpty()) {
                schdfuture.cancel(true);
            }
            watchdog.forEach(watchdog -> {
                if (watchdog.shouldRun()) {
                    watchdog.start();
                    startedWatchdogs.add(watchdog);
                }
            });
            watchdog.removeAll(startedWatchdogs);
        }, 0, 1, TimeUnit.DAYS);
    }

    @Autowired
    void setMonitors(List<Watchdog> watchdog) {
        this.watchdog = watchdog;
    }

    private static Logger getLogger() {
        return LogHolder.log;
    }
}

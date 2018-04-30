package com.cannontech.watchdogs.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.watchdog.base.WatchdogBase;
import com.cannontech.watchdog.base.Watchdogs;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

@Component
public class ServiceStatusWatchdogImpl  extends WatchdogBase implements ServiceStatusWatchdog {

    Logger log = YukonLogManager.getLogger(ServiceStatusWatchdogImpl.class);
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    ScheduledFuture<?> scheduler;

    @Override
    public Watchdogs getName() {
        return Watchdogs.SERVICESTATUS;
    }
    
    @Override
    public boolean isServiceRunning(YukonServices service) {
        return true;
    }

    @Override
    public void start() {
        // TODO: This is temporary code, this will have to be replaced  
        scheduler = executor.scheduleAtFixedRate(() -> {
            watchAndNotify();
        }, 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public List<WatchdogWarnings> watch() {
        // TODO: This is temporary code, this will have to be replaced  
        List<WatchdogWarnings> warnings = new ArrayList<>();
        List<Object> arguments = new ArrayList<>();
        arguments.add("DISCONNECTED");
        
        WatchdogWarnings wd = new WatchdogWarnings(WatchdogWarningType.DB_CONNECTION_STATUS, arguments);
        warnings.add(wd);
        return warnings;
    }

}

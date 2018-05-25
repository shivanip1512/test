package com.cannontech.watchdogs.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

@Service
public class NotificationServiceWatcher extends ServiceStatusWatchdogImpl {
    private static final Logger log = YukonLogManager.getLogger(NotificationServiceWatcher.class);
    
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;

    @Override
    public void start() {
        executor.scheduleAtFixedRate(() -> {
            watchAndNotify();
        }, 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getNotificationServerStatus();
        log.debug("Status of notification server " + connectionStatus);
        return generateWarning(WatchdogWarningType.NOTIFICATION_SERVICE_CONNECTION_STATUS, connectionStatus);
    }

    private ServiceStatus getNotificationServerStatus() {
        String serviceName = "YukonNotificationServer";
        return getStatusFromWindows(serviceName);
    }
}

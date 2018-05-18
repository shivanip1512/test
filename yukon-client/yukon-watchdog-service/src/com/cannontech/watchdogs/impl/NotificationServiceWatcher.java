package com.cannontech.watchdogs.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

@Service
public class NotificationServiceWatcher extends ServiceStatusWatchdogImpl {

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;

    @Override
    public void start() {
        executor.scheduleAtFixedRate(() -> {
            watchAndNotify();
        }, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getServiceManagerStatus();
        return generateWarning(WatchdogWarningType.NOTIFICATION_SERVICE_CONNECTION_STATUS, connectionStatus);
    }

    private ServiceStatus getServiceManagerStatus() {
        String serviceName = "YukonNotificationServer";
        return getStatusFromWindows(serviceName);
    }
}

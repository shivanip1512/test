package com.cannontech.watchdogs.impl;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

@Service
public class NotificationServiceWatcher extends ServiceStatusWatchdogImpl {
    public NotificationServiceWatcher(ConfigurationSource configSource) {
        super(configSource);
    }

    private static final Logger log = YukonLogManager.getLogger(NotificationServiceWatcher.class);

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getNotificationServerStatus();
        log.info("Status of notification server " + connectionStatus);
        return generateWarning(WatchdogWarningType.YUKON_NOTIFICATION_SERVER, connectionStatus);
    }

    private ServiceStatus getNotificationServerStatus() {
        String serviceName = "YukonNotificationServer";
        return getStatusFromWindows(serviceName);
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.NOTIFICATIONSERVICE;
    }

    @Override
    public boolean isServiceRunning() {
        return getNotificationServerStatus() == (ServiceStatus.RUNNING);
    }
}

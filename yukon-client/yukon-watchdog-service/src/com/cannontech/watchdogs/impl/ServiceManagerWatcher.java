package com.cannontech.watchdogs.impl;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

@Service
public class ServiceManagerWatcher extends ServiceStatusWatchdogImpl {
    private static final Logger log = YukonLogManager.getLogger(ServiceManagerWatcher.class);

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getServiceManagerStatus();
        log.info("Status of service manager " + connectionStatus);
        return generateWarning(WatchdogWarningType.SERVICE_MANAGER_SERVICE_STATUS, connectionStatus);
    }

    private ServiceStatus getServiceManagerStatus() {
        String serviceName = "YukonServiceMgr";
        return getStatusFromWindows(serviceName);
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.SERVICEMANAGER;
    }

    @Override
    public boolean isServiceRunning() {
        return getServiceManagerStatus() == (ServiceStatus.RUNNING);
    }
}

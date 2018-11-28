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
public class ServiceManagerWatcher extends ServiceStatusWatchdogImpl {
    public ServiceManagerWatcher(ConfigurationSource configSource) {
        super(configSource);
    }

    private static final Logger log = YukonLogManager.getLogger(ServiceManagerWatcher.class);

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getServiceManagerStatus();
        log.info("Status of service manager " + connectionStatus);
        return generateWarning(WatchdogWarningType.YUKON_SERVICE_MANAGER, connectionStatus);
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

package com.cannontech.watchdogs.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.watchdog.base.WatchdogBase;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.model.Watchdogs;
import com.cannontech.watchdogs.impl.ServiceStatusWatchdogImpl.ServiceStatus;
import com.google.common.collect.Maps;

@Component
public abstract class DBConnectionWatchdogImpl extends WatchdogBase {

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;

    public static final String WARNING_TYPE = "WarningType";
    public static final String SERVICE_STATUS = "Status";

    Logger log = YukonLogManager.getLogger(DBConnectionWatchdogImpl.class);

    @Override
    public void start() {
        log.info("Starting DBConnectionMonitor");
        executor.scheduleAtFixedRate(this::doWatchAction, 1, 5, TimeUnit.MINUTES);
    }

    private void doWatchAction() {
        watchAndNotify();
    }

    @Override
    public Watchdogs getName() {
        return Watchdogs.DB_CONNECTION;
    }

    public List<WatchdogWarnings> generateWarning(WatchdogWarningType type, ServiceStatus serviceStatus) {
        List<WatchdogWarnings> warnings = new ArrayList<>();
        if (serviceStatus == ServiceStatus.STOPPED) {
            Map<String, Object> arguments = Maps.newLinkedHashMap();
            arguments.put(WARNING_TYPE, type.name());
            arguments.put(SERVICE_STATUS, ServiceStatus.STOPPED.name());
            WatchdogWarnings watchdogWarning = new WatchdogWarnings(type, arguments);
            warnings.add(watchdogWarning);
            warnings.stream()
                    .forEach(warning -> log.info("A Watchdog Warning is generated : " + warning.getWarningType().getName()
                            + " is " + warning.getArguments().get(SERVICE_STATUS)));
        }
        return warnings;
    }
}

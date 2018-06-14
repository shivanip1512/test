package com.cannontech.watchdogs.impl;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.watchdog.base.WatchdogBase;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.model.Watchdogs;

@Component
public class DBConnectionWatchdogImpl extends WatchdogBase implements DBConnectionWatchdog {

    Logger log = YukonLogManager.getLogger(DBConnectionWatchdogImpl.class);

    @Override
    public void start() {
        log.info("Starting DBConnectionMonitor");
    }

    @Override
    public Watchdogs getName() {
        return Watchdogs.DB_CONNECTION;
    }

    @Override
    public boolean isDBConnected(DBName dbName) {
        return true;
    }

    @Override
    public List<WatchdogWarnings> watch() {
        // Will send query to database and check if DB connection is working. If not will send notification
        return null;
    }

}

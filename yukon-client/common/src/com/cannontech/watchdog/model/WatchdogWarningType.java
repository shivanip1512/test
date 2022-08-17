package com.cannontech.watchdog.model;

import com.cannontech.watchdog.model.Watchdogs;


public enum WatchdogWarningType {

    YUKON_WEB_APPLICATION_SERVICE("Yukon Web Application Service", Watchdogs.SERVICE_STATUS),
    YUKON_SERVICE_MANAGER("Yukon Service Manager", Watchdogs.SERVICE_STATUS),
    YUKON_NOTIFICATION_SERVER("Yukon Notification Server", Watchdogs.SERVICE_STATUS),
    YUKON_MESSAGE_BROKER("Yukon Message Broker", Watchdogs.SERVICE_STATUS),
    YUKON_DISPATCH_SERVICE("Yukon Dispatch Service", Watchdogs.SERVICE_STATUS),
    YUKON_CAP_CONTROL_SERVICE("Yukon Cap Control Service", Watchdogs.SERVICE_STATUS),
    YUKON_LOAD_MANAGEMENT_SERVICE("Yukon Load Management Service", Watchdogs.SERVICE_STATUS),
    YUKON_PORT_CONTROL_SERVICE("Yukon Port Control Service", Watchdogs.SERVICE_STATUS),
    YUKON_MAC_SCHEDULER_SERVICE("Yukon MAC Scheduler Service", Watchdogs.SERVICE_STATUS),
    YUKON_FOREIGN_DATA_SERVICE("Yukon Foreign Data Service", Watchdogs.SERVICE_STATUS),
    YUKON_CALC_LOGIC_SERVICE("Yukon Calc-Logic Service", Watchdogs.SERVICE_STATUS),
    YUKON_REAL_TIME_SCAN_SERVICE("Yukon Real-Time Scan Service", Watchdogs.SERVICE_STATUS),
    YUKON_NETWORK_MANAGER("Yukon Network Manager", Watchdogs.SERVICE_STATUS),
    YUKON_ITRON_SERVICE("Yukon Itron Service", Watchdogs.SERVICE_STATUS);

    private final Watchdogs watchdogName;

    private final String name;

    public Watchdogs getWatchdogName() {
        return watchdogName;
    }

    public String getName() {
        return name;
    }

    private WatchdogWarningType(String name, Watchdogs watchdogName) {
        this.watchdogName = watchdogName;
        this.name = name;
    }

}

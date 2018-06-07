package com.cannontech.watchdog.model;

public enum WatchdogCategory {
    WEBSERVER_SERVICE,
    SERVICE_MANAGER_SERVICE,
    NOTIFICATION_SERVICE,
    YUKON_MESSAGE_BROKER_SERVICE,
    DISPATCH_SERVICE,
    PORTER_SERVICE,
    DB_CONNECTION,
    CRASH_DUMP,
    CONFIG_FILE,
    LOW_DISK_SPACE;

    @Override
    public String toString() {
        return name().toLowerCase().replaceAll("_", " ");
    }

}

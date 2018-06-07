package com.cannontech.watchdog.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.watchdog.model.WatchdogCategory;


public enum WatchdogWarningType implements DisplayableEnum {

    WEB_SERVER_CONNECTION_STATUS(WatchdogCategory.WEBSERVER_SERVICE),
    SERVICE_MANAGER_CONNECTION_STATUS(WatchdogCategory.SERVICE_MANAGER_SERVICE),
    NOTIFICATION_SERVICE_CONNECTION_STATUS(WatchdogCategory.NOTIFICATION_SERVICE),
    YUKON_MESSAGE_BROKER_CONNECTION_STATUS(WatchdogCategory.YUKON_MESSAGE_BROKER_SERVICE),
    DISPATCH_CONNECTION_STATUS(WatchdogCategory.DISPATCH_SERVICE),
    PORTER_CONNECTION_STATUS(WatchdogCategory.PORTER_SERVICE),
    DB_CONNECTION_STATUS(WatchdogCategory.DB_CONNECTION),
    CRASH_DUMP_LOCATION(WatchdogCategory.CRASH_DUMP),
    CONFIG_FILE_MODIFICATION(WatchdogCategory.CONFIG_FILE),
    LOW_DISK_SPACE(WatchdogCategory.LOW_DISK_SPACE)
    ;

    private final WatchdogCategory watchdogCategory;
    

    public WatchdogCategory getWatchdogCategory() {
        return watchdogCategory;
    }

    private WatchdogWarningType(WatchdogCategory watchdogCategory) {
        this.watchdogCategory = watchdogCategory;
    }

    
    private static final String keyBase = "";

    @Override
    public String getFormatKey() {
        return keyBase + name();
    }

    @Override
    public String toString() {
        return name().toLowerCase().replaceAll("_", " ");
    }
}

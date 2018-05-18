package com.cannontech.watchdog.model;

import com.cannontech.common.i18n.DisplayableEnum;


public enum WatchdogWarningType implements DisplayableEnum {
    // TODO: This is temporary code, this will have to be replaced  
    WEB_SERVER_CONNECTION_STATUS,
    SERVICE_MANAGER_CONNECTION_STATUS,
    NOTIFICATION_SERVICE_CONNECTION_STATUS,
    YUKON_MESSAGE_BROKER_CONNECTION_STATUS,
    DISPATCH_CONNECTION_STATUS,
    PORTER_CONNECTION_STATUS,
    DB_CONNECTION_STATUS,
    CRASH_DUMP_LOCATION,
    CONFIG_FILE_MODIFICATION,
    LOW_DISK_SPACE
    ;
    
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

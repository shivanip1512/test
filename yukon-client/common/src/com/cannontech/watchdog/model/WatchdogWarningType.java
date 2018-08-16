package com.cannontech.watchdog.model;

import org.apache.commons.lang3.text.WordUtils;
import com.cannontech.watchdog.model.Watchdogs;


public enum WatchdogWarningType {

    YUKON_WEB_APPLICATION_SERVICE(Watchdogs.SERVICE_STATUS),
    YUKON_SERVICE_MANAGER(Watchdogs.SERVICE_STATUS),
    YUKON_NOTIFICATION_SERVER(Watchdogs.SERVICE_STATUS),
    YUKON_MESSAGE_BROKER(Watchdogs.SERVICE_STATUS),
    YUKON_DISPATCH_SERVICE(Watchdogs.SERVICE_STATUS),
    YUKON_CAP_CONTROL_SERVICE(Watchdogs.SERVICE_STATUS),
    YUKON_LOAD_MANAGEMENT_SERVICE(Watchdogs.SERVICE_STATUS),
    YUKON_PORT_CONTROL_SERVICE(Watchdogs.SERVICE_STATUS),
    YUKON_MAC_SCHEDULER_SERVICE(Watchdogs.SERVICE_STATUS),
    YUKON_FOREIGN_DATA_SERVICE(Watchdogs.SERVICE_STATUS),
    YUKON_CALC_LOGIC_SERVICE(Watchdogs.SERVICE_STATUS),
    YUKON_REAL_TIME_SCAN_SERVICE(Watchdogs.SERVICE_STATUS),
    YUKON_NETWORK_MANAGER(Watchdogs.SERVICE_STATUS),    
    DB_CONNECTION_STATUS(Watchdogs.DB_CONNECTION),
    CRASH_DUMP_LOCATION(Watchdogs.CRASH_DUMP),
    CONFIG_FILE_MODIFICATION(Watchdogs.CONFIG_FILE),
    LOW_DISK_SPACE(Watchdogs.DISK_SPACE);

    private final Watchdogs watchdogName;

    public Watchdogs getWatchdogName() {
        return watchdogName;
    }

    private WatchdogWarningType(Watchdogs watchdogName) {
        this.watchdogName = watchdogName;
    }

    /**
     * This method receives object in two format . first one will be the watchdogwarningtype and other one will be the 
     * arguments in "{key=value}" format . As we need value part so if it didn't matches the enums it will return the 
     * value part.
     */
    public static Object fromObject(Object obj) {
        String argument = obj.toString();
        for (WatchdogWarningType wwt : WatchdogWarningType.values()) {
            if (wwt.name().equalsIgnoreCase(argument)) {
                return wwt.toString();
            }
        }
        String[] strArray = argument.split("=");
        if (strArray.length == 2) {
            return strArray[1];
        } else {
            return WordUtils.capitalizeFully(strArray[0].toLowerCase().replaceAll("_", " "));
        }
    }


    @Override
    public String toString() {
        return WordUtils.capitalizeFully(name().toLowerCase().replaceAll("_", " "));
    }
}

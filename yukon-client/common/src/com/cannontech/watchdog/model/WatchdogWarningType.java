package com.cannontech.watchdog.model;

import org.apache.commons.lang3.text.WordUtils;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.watchdog.model.Watchdogs;


public enum WatchdogWarningType implements DisplayableEnum {

    WEB_SERVER_SERVICE_STATUS(Watchdogs.SERVICE_STATUS),
    SERVICE_MANAGER_SERVICE_STATUS(Watchdogs.SERVICE_STATUS),
    NOTIFICATION_SERVER_SERVICE_STATUS(Watchdogs.SERVICE_STATUS),
    YUKON_MESSAGE_BROKER_SERVICE_STATUS(Watchdogs.SERVICE_STATUS),
    DISPATCH_SERVICE_STATUS(Watchdogs.SERVICE_STATUS),
    PORTER_SERVICE_STATUS(Watchdogs.SERVICE_STATUS),
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
        return strArray[1];
    }

    private static final String keyBase = "yukon.web.widgets.watchdogWarnings.warningType.";

    @Override
    public String getFormatKey() {
        return keyBase + name();
    }

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(name().toLowerCase().replaceAll("_", " "));
    }
}

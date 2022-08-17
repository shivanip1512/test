package com.cannontech.common.smartNotification.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Enumeration of the possible sources of Smart Notification events.
 */
public enum SmartNotificationEventType implements DisplayableEnum {
    DEVICE_DATA_MONITOR("deviceDataMonitor"),
    INFRASTRUCTURE_WARNING("infrastructureWarnings"),
    YUKON_WATCHDOG("watchdogWarnings"),
    ASSET_IMPORT("assetImport"),
    METER_DR("meterDr") 
    ;
    
    private String urlPath;
    
    private SmartNotificationEventType(String urlPath) {
        this.urlPath = urlPath;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.smartNotifications.eventType." + name();
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }
    
    public static SmartNotificationEventType retrieveByUrlPath(String urlPath) {
        for (SmartNotificationEventType event : SmartNotificationEventType.values()) {
            if (event.getUrlPath().equals(urlPath)) {
                return event;
            }
        }
        return null;
    }
}

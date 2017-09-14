package com.cannontech.common.smartNotification.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Enumeration of the possible sources of Smart Notification events.
 */
public enum SmartNotificationEventType implements DisplayableEnum {
    DEVICE_DATA_MONITOR,
    INFRASTRUCTURE_WARNING,
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.smartNotifications.eventType." + name();
    }
}

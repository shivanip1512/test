package com.cannontech.common.smartNotification.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * A rough specification of how long a smart notification message should be. It is up to the message builders to
 * interpret this value appropriately for a given media type.
 */
public enum SmartNotificationVerbosity implements DisplayableEnum {
    SUMMARY,
    EXPANDED,
    ;
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.smartNotifications.messageDetail." + name();
    }
}

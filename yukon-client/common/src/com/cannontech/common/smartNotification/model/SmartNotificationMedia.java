package com.cannontech.common.smartNotification.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Describes the way the notification will be transmitted.
 */
public enum SmartNotificationMedia implements DisplayableEnum {
    EMAIL,
    //SMS,
    ;
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.smartNotifications.media." + name();
    }
}

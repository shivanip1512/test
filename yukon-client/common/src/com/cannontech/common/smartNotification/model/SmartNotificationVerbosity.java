package com.cannontech.common.smartNotification.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum SmartNotificationVerbosity implements DisplayableEnum {
    SUMMARY,
    EXPANDED,
    ;
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.smartNotifications.messageDetail." + name();
    }
}

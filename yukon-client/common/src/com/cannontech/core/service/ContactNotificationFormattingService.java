package com.cannontech.core.service;

import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.user.YukonUserContext;

public interface ContactNotificationFormattingService {
    
    /**
     * Convert a LiteContactNotification to a string.
     * @param notif
     * @param context
     * @return
     */
    public String formatNotification(LiteContactNotification notif, YukonUserContext context);
    
}

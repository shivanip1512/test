package com.cannontech.core.service;

import com.cannontech.user.YukonUserContext;

public interface ContactNotificationFormattingService {
    
    /**
     * Convert a ContactNotification to a string.
     * The type dictates a suggested style, but not a specific format.
     * 
     * @param notif
     * @param type
     * @return
     */
    public String formatNotification(String notif, int type, YukonUserContext context);
    
}

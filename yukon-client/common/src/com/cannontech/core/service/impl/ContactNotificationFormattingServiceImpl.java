package com.cannontech.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.core.service.ContactNotificationFormattingService;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.user.YukonUserContext;

public class ContactNotificationFormattingServiceImpl implements ContactNotificationFormattingService{
    
    private PhoneNumberFormattingService phoneNumberFormattingService;

    @Override
    public String formatNotification(LiteContactNotification notif, YukonUserContext context) throws IllegalArgumentException {
        if (notif != null) {
            if (notif.getContactNotificationType().isPhoneType() || notif.getContactNotificationType().isFaxType()) {
                try {
                    return phoneNumberFormattingService.formatPhoneNumber(notif.getNotification(), context);
                } catch (NoSuchMessageException e) {
                    return notif.getNotification();
                }
            }else {
                return notif.getNotification();
            }
        } else {
            // home and work phone, email etc are optional
            return "";
        }
    }
    
    @Autowired
    public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
        this.phoneNumberFormattingService = phoneNumberFormattingService;
    }
}

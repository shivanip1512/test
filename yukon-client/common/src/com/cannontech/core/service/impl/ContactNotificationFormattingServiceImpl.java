package com.cannontech.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.service.ContactNotificationFormattingService;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.user.YukonUserContext;

public class ContactNotificationFormattingServiceImpl implements ContactNotificationFormattingService{
    
    private PhoneNumberFormattingService phoneNumberFormattingService;
    private YukonListDao yukonListDao;

    @Override
    public String formatNotification(String notif, int type, YukonUserContext context) throws IllegalArgumentException {
        if (notif != null) {
            if(yukonListDao.isPhoneNumber(type)) {
                return phoneNumberFormattingService.formatPhoneNumber(notif, context);
            }else {
                return notif;
            }
        } else {
            throw new IllegalArgumentException("Contact Notification object is null in ContactNotificationFormattingServiceImpl.formatNotification()");
        }
    }
    
    @Autowired
    public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
        this.phoneNumberFormattingService = phoneNumberFormattingService;
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }

}

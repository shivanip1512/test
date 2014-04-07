package com.cannontech.stars.dr.optout.service;

import javax.mail.MessagingException;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

public interface OptOutNotificationService {

    void sendOptOutNotification(CustomerAccount customerAccount, EnergyCompany energyCompany, 
        OptOutRequest request, LiteYukonUser user) throws MessagingException;

    void sendCancelScheduledNotification(CustomerAccount customerAccount, EnergyCompany energyCompany,
        OptOutRequest request, LiteYukonUser user) throws MessagingException;

    void sendReenableNotification(CustomerAccount customerAccount, EnergyCompany energyCompany,
        OptOutRequest request, LiteYukonUser user) throws MessagingException;
    
}

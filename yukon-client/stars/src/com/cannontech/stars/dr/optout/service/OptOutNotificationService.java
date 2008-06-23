package com.cannontech.stars.dr.optout.service;

import javax.mail.MessagingException;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;

public interface OptOutNotificationService {

    public void sendNotification(CustomerAccount customerAccount,  
            LiteStarsEnergyCompany energyCompany, OptOutRequest request, 
            YukonUserContext yukonUserContext)
        throws MessagingException;
    
}

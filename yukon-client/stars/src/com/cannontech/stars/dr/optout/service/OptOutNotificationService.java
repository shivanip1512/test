package com.cannontech.stars.dr.optout.service;

import javax.mail.MessagingException;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;

/**
 * Interface used to send opt out notifications
 */
public interface OptOutNotificationService {

    public void sendOptOutNotification(CustomerAccount customerAccount,  
            LiteStarsEnergyCompany energyCompany, OptOutRequest request, 
            YukonUserContext yukonUserContext)
        throws MessagingException;

    public void sendCancelScheduledNotification(CustomerAccount customerAccount,  
    		LiteStarsEnergyCompany energyCompany, OptOutRequest request, 
    		YukonUserContext yukonUserContext)
    throws MessagingException;

    public void sendReenableNotification(CustomerAccount customerAccount,  
    		LiteStarsEnergyCompany energyCompany, OptOutRequest request, 
    		YukonUserContext yukonUserContext)
    throws MessagingException;
    
}

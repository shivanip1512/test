package com.cannontech.stars.dr.optout.service;

import javax.mail.MessagingException;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;

/**
 * Interface used to send opt out notifications
 */
public interface OptOutNotificationService {

    public void sendOptOutNotification(CustomerAccount customerAccount,  
            LiteStarsEnergyCompany energyCompany, OptOutRequest request, 
            LiteYukonUser user)
        throws MessagingException;

    public void sendCancelScheduledNotification(CustomerAccount customerAccount,  
    		LiteStarsEnergyCompany energyCompany, OptOutRequest request, 
    		LiteYukonUser user)
    throws MessagingException;

    public void sendReenableNotification(CustomerAccount customerAccount,  
    		LiteStarsEnergyCompany energyCompany, OptOutRequest request, 
    		LiteYukonUser user)
    throws MessagingException;
    
}

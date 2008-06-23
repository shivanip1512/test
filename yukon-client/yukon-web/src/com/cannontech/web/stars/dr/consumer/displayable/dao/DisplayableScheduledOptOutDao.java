package com.cannontech.web.stars.dr.consumer.displayable.dao;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableScheduledOptOut;

public interface DisplayableScheduledOptOutDao {

    public DisplayableScheduledOptOut getLastDisplayableScheduledOptOut(CustomerAccount customerAccount, 
            YukonUserContext yukonUserContext);
    
}

package com.cannontech.web.stars.dr.consumer.displayable.dao;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableOptOut;

public interface DisplayableOptOutDao {

    public DisplayableOptOut getDisplayableOptOut(CustomerAccount customerAccount, 
            YukonUserContext yukonUserContext);
    
}

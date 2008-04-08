package com.cannontech.stars.dr.optout.dao;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.model.ScheduledOptOut;
import com.cannontech.user.YukonUserContext;

public interface ScheduledOptOutDao {

    public ScheduledOptOut getByCustomerAccount(CustomerAccount customerAccount, 
            YukonUserContext yukonUserContext) throws NotFoundException;
    
}

package com.cannontech.stars.dr.optout.dao;

import java.util.List;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.model.ScheduledOptOut;
import com.cannontech.user.YukonUserContext;

public interface ScheduledOptOutDao {

    public ScheduledOptOut getLastScheduledOptOut(CustomerAccount customerAccount, 
            YukonUserContext yukonUserContext) throws NotFoundException;
    
    public List<ScheduledOptOut> getAll(CustomerAccount customerAccount, 
            YukonUserContext yukonUserContext) throws NotFoundException;
    
}

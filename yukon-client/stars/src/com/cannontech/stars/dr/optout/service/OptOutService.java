package com.cannontech.stars.dr.optout.service;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;

public interface OptOutService {

    public MessageSourceResolvable processOptOutRequest(CustomerAccount customerAccount,
            OptOutRequest request, YukonUserContext yukonUserContext);
    
}

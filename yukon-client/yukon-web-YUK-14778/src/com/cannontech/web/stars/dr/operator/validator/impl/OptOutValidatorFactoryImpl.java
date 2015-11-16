package com.cannontech.web.stars.dr.operator.validator.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.validator.OptOutValidator;
import com.cannontech.web.stars.dr.operator.validator.OptOutValidatorFactory;

public class OptOutValidatorFactoryImpl implements OptOutValidatorFactory {

    private OptOutService optOutService;
    
    public OptOutValidator getOptOutValidator(YukonUserContext userContext,
                                              boolean isOperator,
                                              AccountInfoFragment accountInfoFragment) {

        return new OptOutValidator(userContext, isOperator, accountInfoFragment, optOutService);
        
    }
    
    @Autowired
    public void setOptOutService(OptOutService optOutService) {
        this.optOutService = optOutService;
    }
    
}

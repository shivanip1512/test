package com.cannontech.web.stars.dr.operator.validator.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.validator.OptOutValidator;
import com.cannontech.web.stars.dr.operator.validator.OptOutValidatorFactory;

public class OptOutValidatorFactoryImpl implements OptOutValidatorFactory {

    private DateFormattingService dateFormattingService;
    private RolePropertyDao rolePropertyDao;
    
    public OptOutValidator getOptOutValidator(YukonUserContext userContext) {

        return new OptOutValidator(userContext, dateFormattingService, rolePropertyDao);
        
    }
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
}

package com.cannontech.web.stars.dr.operator.validator.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.validator.OptOutValidator;
import com.cannontech.web.stars.dr.operator.validator.OptOutValidatorFactory;

public class OptOutValidatorFactoryImpl implements OptOutValidatorFactory {

    private RolePropertyDao rolePropertyDao;
    
    public OptOutValidator getOptOutValidator(YukonUserContext userContext) {

        return new OptOutValidator(userContext, rolePropertyDao);
        
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
}

package com.cannontech.web.stars.dr.operator.validator.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.validator.OptOutValidator;
import com.cannontech.web.stars.dr.operator.validator.OptOutValidatorFactory;

public class OptOutValidatorFactoryImpl implements OptOutValidatorFactory {

    private RolePropertyDao rolePropertyDao;
    private DisplayableInventoryDao displayableInventoryDao;
    
    public OptOutValidator getOptOutValidator(YukonUserContext userContext, 
                                              AccountInfoFragment accountInfoFragment) {

        return new OptOutValidator(userContext, accountInfoFragment, rolePropertyDao, displayableInventoryDao);
        
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setDisplayableInventoryDao(DisplayableInventoryDao displayableInventoryDao) {
        this.displayableInventoryDao = displayableInventoryDao;
    }
    
}

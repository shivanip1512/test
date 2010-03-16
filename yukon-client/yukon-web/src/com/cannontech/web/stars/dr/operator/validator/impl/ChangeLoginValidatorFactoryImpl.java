package com.cannontech.web.stars.dr.operator.validator.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.validator.ChangeLoginValidator;
import com.cannontech.web.stars.dr.operator.validator.ChangeLoginValidatorFactory;

public class ChangeLoginValidatorFactoryImpl implements ChangeLoginValidatorFactory {

    private AuthenticationService authenticationService;
    private RolePropertyDao rolePropertyDao;
    private YukonUserDao yukonUserDao;
    
    public ChangeLoginValidator getChangeLoginValidator(LiteYukonUser residentialUser, YukonUserContext userContext) {

        return new ChangeLoginValidator(residentialUser, userContext, authenticationService, rolePropertyDao, yukonUserDao);
        
    }
    
    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
}

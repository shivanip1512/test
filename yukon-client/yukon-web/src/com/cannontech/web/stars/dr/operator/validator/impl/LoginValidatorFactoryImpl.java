package com.cannontech.web.stars.dr.operator.validator.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.dr.operator.validator.LoginValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginValidatorFactory;

public class LoginValidatorFactoryImpl implements LoginValidatorFactory {

    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonUserDao yukonUserDao;
    
    public LoginValidator getLoginValidator(LiteYukonUser residentialUser) {
        return new LoginValidator(residentialUser, passwordPolicyService, userGroupDao, yukonUserDao);
    }
}

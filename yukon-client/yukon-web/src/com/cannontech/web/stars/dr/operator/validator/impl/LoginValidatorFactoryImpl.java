package com.cannontech.web.stars.dr.operator.validator.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.dr.operator.validator.LoginValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginValidatorFactory;

public class LoginValidatorFactoryImpl implements LoginValidatorFactory {

    private YukonUserDao yukonUserDao;
    
    public LoginValidator getLoginValidator(LiteYukonUser residentialUser) {
        return new LoginValidator(residentialUser, yukonUserDao);
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
}

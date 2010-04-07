package com.cannontech.web.stars.dr.operator.validator.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.dr.operator.validator.ChangeLoginValidator;
import com.cannontech.web.stars.dr.operator.validator.ChangeLoginValidatorFactory;

public class ChangeLoginValidatorFactoryImpl implements ChangeLoginValidatorFactory {

    private YukonUserDao yukonUserDao;
    
    public ChangeLoginValidator getChangeLoginValidator(LiteYukonUser residentialUser) {

        return new ChangeLoginValidator(residentialUser, yukonUserDao);
        
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
}

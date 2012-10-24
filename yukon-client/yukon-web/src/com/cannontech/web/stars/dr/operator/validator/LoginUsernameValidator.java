package com.cannontech.web.stars.dr.operator.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.dr.operator.model.LoginBackingBean;

public class LoginUsernameValidator extends SimpleValidator<LoginBackingBean> {

    private YukonUserDao yukonUserDao;

    private LiteYukonUser user;
    
    public LoginUsernameValidator(LiteYukonUser user, YukonUserDao yukonUserDao){
    	super(LoginBackingBean.class);
    	this.user = user;
    	this.yukonUserDao = yukonUserDao;
    }

    @Override
    public void doValidation(LoginBackingBean loginBackingBean, Errors errors) {
        if (user != null) {
            ValidationUtils.rejectIfEmpty(errors, "username", "yukon.web.modules.operator.account.loginInfoError.usernameRequired");
            YukonValidationUtils.checkExceedsMaxLength(errors, "username", loginBackingBean.getUsername(), 64);
            
            LiteYukonUser usernameCheckUser = yukonUserDao.findUserByUsername(loginBackingBean.getUsername());
            if (usernameCheckUser != null && user.getUserID() != usernameCheckUser.getUserID()) {
                errors.rejectValue("username", "yukon.web.modules.operator.account.loginInfoError.usernameAlreadyExists");
                return;
            }
        }
    }
}
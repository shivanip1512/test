package com.cannontech.web.stars.dr.operator.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.dr.operator.model.LoginBackingBean;

public class LoginValidator extends SimpleValidator<LoginBackingBean> {

    private YukonUserDao yukonUserDao;
    private LiteYukonUser user;
    
    public LoginValidator(LiteYukonUser user, YukonUserDao yukonUserDao){
    	super(LoginBackingBean.class);
    	this.user = user;
        this.yukonUserDao = yukonUserDao;
    }

    @Override
    public void doValidation(LoginBackingBean target, Errors errors) {

        LoginBackingBean loginBackingBean = (LoginBackingBean)target;

        if (user != null) {
            ValidationUtils.rejectIfEmpty(errors, "username", "yukon.web.modules.operator.account.loginInfoError.usernameRequired");
            YukonValidationUtils.checkExceedsMaxLength(errors, "username", loginBackingBean.getUsername(), 64);
            LiteYukonUser usernameCheckUser = yukonUserDao.findUserByUsername(loginBackingBean.getUsername());
            if (usernameCheckUser != null &&
                user.getUserID() != usernameCheckUser.getUserID()) {
                errors.rejectValue("username", "yukon.web.modules.operator.account.loginInfoError.usernameAlreadyExists");
            }
        }
        
        // Password Validation
        YukonValidationUtils.checkExceedsMaxLength(errors, "password1", loginBackingBean.getPassword1(), 64);
        YukonValidationUtils.checkExceedsMaxLength(errors, "password2", loginBackingBean.getPassword2(), 64);
        if (!StringUtils.isBlank(loginBackingBean.getPassword1()) ||
            !StringUtils.isBlank(loginBackingBean.getPassword2())) {
            
            if (!loginBackingBean.getPassword1().equals(loginBackingBean.getPassword2())) {
                errors.rejectValue("password1", "yukon.web.modules.operator.account.loginInfoError.passwordNoMatch");
                errors.rejectValue("password2", "yukon.web.modules.operator.account.loginInfoError.passwordNoMatch");
            }
        }
    }

}
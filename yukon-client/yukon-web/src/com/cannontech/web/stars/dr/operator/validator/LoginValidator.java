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
    private LiteYukonUser residentialUser;
    
    public LoginValidator(LiteYukonUser residentialUser, YukonUserDao yukonUserDao){
    	super(LoginBackingBean.class);
    	this.residentialUser = residentialUser;
        this.yukonUserDao = yukonUserDao;
    }

    @Override
    public void doValidation(LoginBackingBean target, Errors errors) {

        LoginBackingBean loginBackingBean = (LoginBackingBean)target;

        ValidationUtils.rejectIfEmpty(errors, "loginBackingBean.username", "yukon.web.modules.operator.account.loginInfoError.usernameRequired");
        YukonValidationUtils.checkExceedsMaxLength(errors, "loginBackingBean.username", loginBackingBean.getUsername(), 64);
        LiteYukonUser usernameCheckUser = yukonUserDao.findUserByUsername(loginBackingBean.getUsername());
        if (usernameCheckUser != null &&
            residentialUser.getUserID() != usernameCheckUser.getUserID()) {
            errors.rejectValue("loginBackingBean.username", "yukon.web.modules.operator.account.loginInfoError.usernameAlreadyExists");
        }
        
        // Password Validation
        YukonValidationUtils.checkExceedsMaxLength(errors, "loginBackingBean.password1", loginBackingBean.getPassword1(), 64);
        YukonValidationUtils.checkExceedsMaxLength(errors, "loginBackingBean.password2", loginBackingBean.getPassword2(), 64);
        if (!StringUtils.isBlank(loginBackingBean.getPassword1()) ||
            !StringUtils.isBlank(loginBackingBean.getPassword2())) {
            
            if (!loginBackingBean.getPassword1().equals(loginBackingBean.getPassword2())) {
                errors.rejectValue("loginBackingBean.password1", "yukon.web.modules.operator.account.loginInfoError.passwordNoMatch");
                errors.rejectValue("loginBackingBean.password2", "yukon.web.modules.operator.account.loginInfoError.passwordNoMatch");
            }
        }
    }

}
package com.cannontech.web.stars.dr.operator.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.dr.operator.model.LoginBackingBean;
import com.google.common.collect.Lists;

public class LoginValidator extends SimpleValidator<LoginBackingBean> {

    private PasswordPolicyService passwordPolicyService;
    private YukonGroupDao yukonGroupDao;
    private YukonUserDao yukonUserDao;

    private LiteYukonUser user;
    
    public LoginValidator(LiteYukonUser user, PasswordPolicyService passwordPolicyService, YukonGroupDao yukonGroupDao, YukonUserDao yukonUserDao){
    	super(LoginBackingBean.class);
    	this.user = user;
    	this.passwordPolicyService = passwordPolicyService;
    	this.yukonGroupDao = yukonGroupDao;
    	this.yukonUserDao = yukonUserDao;
    }

    @Override
    public void doValidation(LoginBackingBean loginBackingBean, Errors errors) {
        if (user != null) {
            ValidationUtils.rejectIfEmpty(errors, "username", "yukon.web.modules.operator.account.loginInfoError.usernameRequired");
            YukonValidationUtils.checkExceedsMaxLength(errors, "username", loginBackingBean.getUsername(), 64);
            LiteYukonUser usernameCheckUser = yukonUserDao.findUserByUsername(loginBackingBean.getUsername());
            if (usernameCheckUser != null &&
                user.getUserID() != usernameCheckUser.getUserID()) {
                errors.rejectValue("username", "yukon.web.modules.operator.account.loginInfoError.usernameAlreadyExists");
                return;
            }
        }
        
        // Password Validation
        YukonValidationUtils.checkExceedsMaxLength(errors, "password1", loginBackingBean.getPassword1(), 64);
        YukonValidationUtils.checkExceedsMaxLength(errors, "password2", loginBackingBean.getPassword2(), 64);
        if (!StringUtils.isBlank(loginBackingBean.getPassword1()) ||
            !StringUtils.isBlank(loginBackingBean.getPassword2())) {
            
            if (!loginBackingBean.getPassword1().equals(loginBackingBean.getPassword2())) {
                passwordError(errors, "yukon.web.modules.operator.account.loginInfoError.passwordNoMatch");
                return;
            }
            
            // Check the password against the password policy.
            LiteYukonGroup liteYukonGroup = yukonGroupDao.getLiteYukonGroupByName(loginBackingBean.getLoginGroupName());
            String password = loginBackingBean.getPassword1();
            PasswordPolicyError passwordPolicyError = passwordPolicyService.checkPasswordPolicy(password, user, liteYukonGroup);

            if (PasswordPolicyError.PASSWORD_DOES_NOT_MEET_POLICY_QUALITY == passwordPolicyError) {
                List<Object> errorArgs = Lists.newArrayList();
                PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user, liteYukonGroup);
                errorArgs.add(passwordPolicy.numberOfRulesMet(password));
                errorArgs.add(passwordPolicy.getPasswordQualityCheck());
                
                passwordError(errors, passwordPolicyError.getFormatKey(), errorArgs.toArray());
                return;
            }
            if (passwordPolicyError != null) {
                passwordError(errors, passwordPolicyError.getFormatKey());
                return;
            }
        }
    }

    private void passwordError(Errors errors, String errorMessageKey, Object... errorArgs) {
        YukonValidationUtils.rejectValues(errors, errorMessageKey, errorArgs, "password1", "password2");
    }
}
package com.cannontech.web.stars.dr.operator.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.dr.operator.model.LoginBackingBean;
import com.google.common.collect.Lists;

public class LoginValidator extends SimpleValidator<LoginBackingBean> {

    private AuthenticationService authenticationService;
    private PasswordPolicyService passwordPolicyService;
    private YukonUserDao yukonUserDao;

    private LiteYukonUser user;
    
    public LoginValidator(LiteYukonUser user, AuthenticationService authenticationService, PasswordPolicyService passwordPolicyService, YukonUserDao yukonUserDao){
    	super(LoginBackingBean.class);
    	this.user = user;
    	this.authenticationService = authenticationService;
    	this.passwordPolicyService = passwordPolicyService;
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
                passwordError(errors, "yukon.web.modules.operator.account.loginInfoError.passwordNoMatch");
                return;
            }
            
            // Check the password against the password policy.
            PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user);
            if (passwordPolicy != null && !errors.hasErrors()) {
                String password = loginBackingBean.getPassword1();
                
                if (password.length() < passwordPolicy.getMinPasswordLength()) {
                   passwordError(errors, "yukon.web.modules.passwordPolicy.minPasswordLengthNotMet");
                   return;
                }
                
                if (!passwordPolicy.isPasswordAgeRequirementMet(user)) {
                    passwordError(errors, "yukon.web.modules.passwordPolicy.minPasswordAgeNotMet");
                    return;
                }
                
                if (authenticationService.isPasswordBeingReused(user, password)) {
                    passwordError(errors, "yukon.web.modules.passwordPolicy.passwordUsedTooRecently");
                    return;
                }
                
                if (!passwordPolicy.isPasswordQualityCheckMet(password)) {
                    List<Object> errorArgs = Lists.newArrayList();
                    errorArgs.add(passwordPolicy.numberOfRulesMet(password));
                    errorArgs.add(passwordPolicy.getPasswordQualityCheck());
                    
                    passwordError(errors, "yukon.web.modules.passwordPolicy.passwordDoesNotMetPolicyQuality", errorArgs.toArray());
                    return;
                }
            }
        }
    }

    private void passwordError(Errors errors, String errorMessageKey) {
        passwordError(errors, errorMessageKey, null);
    }

    private void passwordError(Errors errors, String errorMessageKey, Object[] errorArgs) {
        YukonValidationUtils.rejectValues(errors, errorMessageKey, errorArgs, "password1", "password2");
    }
}
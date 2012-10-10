package com.cannontech.web.stars.dr.operator.validator;

import java.util.List;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.dr.operator.model.LoginBackingBean;
import com.google.common.collect.Lists;

public class LoginPasswordValidator extends SimpleValidator<LoginBackingBean> {

    private PasswordPolicyService passwordPolicyService;
    private UserGroupDao userGroupDao;

    private LiteYukonUser user;
    
    public LoginPasswordValidator(LiteYukonUser user, PasswordPolicyService passwordPolicyService, UserGroupDao userGroupDao){
    	super(LoginBackingBean.class);
    	this.user = user;
    	this.passwordPolicyService = passwordPolicyService;
    	this.userGroupDao = userGroupDao;
    }

    @Override
    public void doValidation(LoginBackingBean loginBackingBean, Errors errors) {
        // Password Validation
        YukonValidationUtils.checkExceedsMaxLength(errors, "password1", loginBackingBean.getPassword1(), 64);
        YukonValidationUtils.checkExceedsMaxLength(errors, "password2", loginBackingBean.getPassword2(), 64);

        if (!loginBackingBean.getPassword1().equals(loginBackingBean.getPassword2())) {
            passwordError(errors, "yukon.web.modules.operator.account.loginInfoError.passwordNoMatch");
            return;
        }

        // Check the password against the password policy.
        LiteUserGroup liteUserGroup = userGroupDao.findLiteUserGroupByUserGroupName(loginBackingBean.getUserGroupName());
        String password = loginBackingBean.getPassword1();
        PasswordPolicyError passwordPolicyError = passwordPolicyService.checkPasswordPolicy(password, user, liteUserGroup);

        if (PasswordPolicyError.PASSWORD_DOES_NOT_MEET_POLICY_QUALITY == passwordPolicyError) {
            List<Object> errorArgs = Lists.newArrayList();
            PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user, liteUserGroup);
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

    private void passwordError(Errors errors, String errorMessageKey, Object... errorArgs) {
        YukonValidationUtils.rejectValues(errors, errorMessageKey, errorArgs, "password1", "password2");
    }
}
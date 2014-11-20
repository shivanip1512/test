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
import com.cannontech.web.login.model.Login;
import com.google.common.collect.Lists;

/**
 * Validates:
 *      * maximum lengths of both fields
 *      * if they match
 *      * checks vs password policy.
 */
public class LoginPasswordValidator extends SimpleValidator<Login> {
    public static final int PASSWORD_MAXIMUM_CHAR_LENGTH = 64;

    private PasswordPolicyService passwordPolicyService;
    private UserGroupDao userGroupDao;

    private LiteYukonUser user;
    
    public LoginPasswordValidator(LiteYukonUser user, PasswordPolicyService passwordPolicyService, UserGroupDao userGroupDao){
    	super(Login.class);
    	this.user = user;
    	this.passwordPolicyService = passwordPolicyService;
    	this.userGroupDao = userGroupDao;
    }

    /**
     * REQUIRES:
     *          LoginBackingBean.getPassword1()
     *          LoginBackingBean.getPassword2()
     */
    @Override
    public void doValidation(Login loginBackingBean, Errors errors) {
        // Password Validation
        YukonValidationUtils.checkExceedsMaxLength(errors, "password1", loginBackingBean.getPassword1(), PASSWORD_MAXIMUM_CHAR_LENGTH);
        YukonValidationUtils.checkExceedsMaxLength(errors, "password2", loginBackingBean.getPassword2(), PASSWORD_MAXIMUM_CHAR_LENGTH);

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
        else if(PasswordPolicyError.INVALID_PASSWORD_LENGTH == passwordPolicyError){
            List<Object> errorArgs = Lists.newArrayList();
            PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user, liteUserGroup);
            errorArgs.add(passwordPolicy.getMinPasswordLength());
            
            passwordError(errors, passwordPolicyError.getFormatKey(), errorArgs.toArray());
            return;
        }
        else if(passwordPolicyError == PasswordPolicyError.PASSWORD_USED_TOO_RECENTLY) {
            List<Object> errorArgs = Lists.newArrayList();
            PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user, liteUserGroup);
            errorArgs.add(passwordPolicy.getPasswordHistory());
            
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
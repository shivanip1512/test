package com.cannontech.web.user.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.login.ChangeLoginMessage;
import com.cannontech.web.login.model.Login;
import com.cannontech.web.stars.dr.operator.validator.LoginPasswordValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginValidatorFactory;
import com.cannontech.web.user.model.ChangePassword;

/**
 * SpringMVC validator Factory
 * 
 * REASON: factory+class needed as validator has a field which may be set during validation and needs to be read later.
 */
@Component
public class ChangePasswordValidatorFactory {

    private String FIELDNAME_ORIGINAL_PASSWORD = "oldPassword";
    private String FIELDNAME_NEW_PASSWORD = "password1";
    private String FIELDNAME_CONFIRM_PASSWORD = "password2";

    private String MSGKEY_BASE = "yukon.web.modules.user.profile.changePassword.error.";
    private final String MSGKEY_CANNOT_VALIDATE_OBJECT_GIVEN = "yukon.common.validator.error.wrong_object";

    private final static int PASSWORD_MAX_LENGTH = LoginPasswordValidator.PASSWORD_MAXIMUM_CHAR_LENGTH;

    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private AuthenticationService authenticationService;
    @Autowired private LoginValidatorFactory loginValidatorFactory;
    @Autowired private PasswordPolicyService passwordPolicyService;

    public ChangePasswordValidator getValidator() {
        return new ChangePasswordValidator();
    }


    /**
     * START: Actual validator class
     * @author ChrisD
     */
    public class ChangePasswordValidator implements Validator {

        private boolean addMessageConfirmPasswordDoesntMatch;

        @Override
        public boolean supports(Class<?> clazz) {
            return ChangePassword.class.equals(clazz);
        }
    
        @Override
        public void validate(Object input, Errors errors) {
            if (input instanceof ChangePassword) {
                validate((ChangePassword)input, errors);
            } else {
                errors.reject(MSGKEY_CANNOT_VALIDATE_OBJECT_GIVEN, new Object[]{"ChangePassword", input.getClass().getName()}, "ChangePasswordValidator was passed something other than a ChangePassword object.");
            }
            
        }
    
        public void validate(ChangePassword input, Errors errors) {
    
            final LiteYukonUser user = yukonUserDao.getLiteYukonUser(input.getUserId());
    
            UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
            AuthenticationCategory authenticationCategory = userAuthenticationInfo.getAuthenticationCategory();
            boolean supportsPasswordChange = authenticationService.supportsPasswordSet(authenticationCategory);
            if (!supportsPasswordChange) {
                errors.reject(MSGKEY_BASE +"auth.unsupported");
                return;
            }
    
            // Verify the original password
            int currErrCnt = errors.getErrorCount();
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, FIELDNAME_ORIGINAL_PASSWORD, MSGKEY_BASE +"currentpassword.required");
            boolean didOldPasswordFail = currErrCnt < errors.getErrorCount();
            if (!didOldPasswordFail) {
                boolean isAuthenticated = false;
                try {
                    isAuthenticated = validateCredentials(user.getUsername(), input.getOldPassword());
                } catch (AuthenticationThrottleException e){
                    long retrySeconds = e.getThrottleSeconds();
                    input.setRetrySeconds(retrySeconds);
              //          } catch (PasswordExpiredException e) {    // Let them change their password even if expired... right?
                }
    
                if (!isAuthenticated) {
                    errors.rejectValue(FIELDNAME_ORIGINAL_PASSWORD, MSGKEY_BASE +"user.notAuthenticated");
                }
            }
    
            // Verify the new password
            currErrCnt = errors.getErrorCount();
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, FIELDNAME_NEW_PASSWORD, MSGKEY_BASE +"newpassword.required");
            boolean didNewPasswordFail = currErrCnt < errors.getErrorCount();
            if (!didNewPasswordFail) {
                if (input.getNewPassword().length() > PASSWORD_MAX_LENGTH) {
                    errors.rejectValue(FIELDNAME_NEW_PASSWORD, MSGKEY_BASE +"newpassword.too_long", 
                                       new Object[]{PASSWORD_MAX_LENGTH}, "longer than "+ PASSWORD_MAX_LENGTH);
                    didNewPasswordFail = true;
                }
            }
    
            if(!didNewPasswordFail) {
                // Check the password against the password policy.
                // This seems to do ...nothing?
                PasswordPolicyError passwordPolicyError = passwordPolicyService.checkPasswordPolicy(input.getNewPassword(), user);
                if (passwordPolicyError != null) {
                    ChangeLoginMessage chkResult = ChangeLoginMessage.valueOf(passwordPolicyError.name());
                    if (chkResult != ChangeLoginMessage.LOGIN_PASSWORD_CHANGED) {
                        errors.rejectValue(FIELDNAME_NEW_PASSWORD, chkResult.getFormatKey());
                        didNewPasswordFail = true;
                    }
                }
            }
    
            currErrCnt = errors.getErrorCount();
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, FIELDNAME_CONFIRM_PASSWORD, MSGKEY_BASE +"confirmpassword.required");
            boolean didConfirmPasswordFail = currErrCnt < errors.getErrorCount();
            if (!didConfirmPasswordFail) {
                if (!input.getNewPassword().equals(input.getConfirmPassword())) {
                    if (addMessageConfirmPasswordDoesntMatch)
                    errors.rejectValue(FIELDNAME_CONFIRM_PASSWORD, MSGKEY_BASE +"confirmpassword.nomatch");
                    didConfirmPasswordFail = true;
                }
            }
    
            if (!didNewPasswordFail && !didConfirmPasswordFail) {
                // How about this: does it validate anything about the make-up of the new password?
                Login login = new Login();
                login.setPassword1(input.getNewPassword());
                login.setPassword2(input.getConfirmPassword());
                LoginPasswordValidator passwordValidator = loginValidatorFactory.getPasswordValidator(user);
                passwordValidator.validate(login, errors);
            }
        }
    
        /**
         * 
         * @param username
         * @param password
         * @return
         * @throws AuthenticationThrottleException
         * @throws PasswordExpiredException
         * 
         * @see {@link ChangeLoginController.isValidPassword(username, password)}
         */
        protected boolean validateCredentials(String username, String password) 
        throws AuthenticationThrottleException {
            try {
                authenticationService.login(username, password);
                return true;
            } catch (AuthenticationThrottleException e) {
                throw e;
            } catch (BadAuthenticationException e) {
                return false;
            } catch (PasswordExpiredException e) {
                return true;
            }
        }
    
        public boolean isAddMessageConfirmPasswordDoesntMatch() {
            return addMessageConfirmPasswordDoesntMatch;
        }
    
        public void setAddMessageConfirmPasswordDoesntMatch(boolean addMessageConfirmPasswordDoesntMatch) {
            this.addMessageConfirmPasswordDoesntMatch = addMessageConfirmPasswordDoesntMatch;
        }
    }
}

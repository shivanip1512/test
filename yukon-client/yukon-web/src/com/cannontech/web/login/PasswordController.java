package com.cannontech.web.login;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.core.authentication.model.PolicyRule;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.login.model.PasswordResetInfo;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.captcha.model.Captcha;
import com.cannontech.web.common.captcha.model.CaptchaResponse;
import com.cannontech.web.common.captcha.service.CaptchaService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.login.model.Login;
import com.cannontech.web.stars.dr.operator.validator.LoginPasswordValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginUsernameValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginValidatorFactory;
import com.cannontech.web.stars.service.PasswordResetService;
import com.cannontech.web.util.TextView;
import com.cannontech.web.util.YukonUserContextResolver;
import com.google.common.collect.Maps;


@Controller
public class PasswordController {
    
    private static final String baseKey = "yukon.web.modules.login.";
    
    @Autowired private AuthenticationService authService;
    @Autowired private CaptchaService captchaService;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private LoginValidatorFactory loginValidatorFactory;
    @Autowired private YukonUserDao userDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonUserContextResolver contextResolver;
    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private LoginService loginService;
    
    @RequestMapping(value="generate-password")
    public TextView generatePassword(HttpServletResponse response, Integer userId, String userGroupName) {

        LiteYukonUser user = null;
        if (userId != null) {
            user = userDao.getLiteYukonUser(userId);
        }

        LiteUserGroup liteUserGroup = userGroupDao.findLiteUserGroupByUserGroupName(userGroupName);
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user, liteUserGroup);
        String generatedPassword = "";
        generatedPassword = passwordPolicy.generatePassword();
        response.setContentType("text/plain; charset=UTF-8");

        return new TextView(generatedPassword);
    }
    
    @RequestMapping(value = "/forgottenPassword", method = RequestMethod.GET)
    public String newForgottenPassword(ModelMap model, HttpServletRequest request) throws Exception {
        
        globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
        
        setupModelMap(model, request);
        model.addAttribute("forgottenPassword", new ForgottenPassword());
        
        return "forgottenPassword.jsp";
    }
    
    @RequestMapping(value = "/forgottenPassword", method = RequestMethod.POST)
    public String forgottenPasswordRequest(HttpServletRequest request, ModelMap model, FlashScope flash,
            @ModelAttribute ForgottenPassword forgottenPassword,
            @RequestParam(value = "g-recaptcha-response", required = false) String gRecaptchaResponse)
            throws Exception {

        globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
        // Process Captcha
            Captcha captcha = new Captcha(request.getServerName(), gRecaptchaResponse);
            CaptchaResponse captchaResponse = captchaService.checkCaptcha(captcha);

        // The Captcha failed. return the user the forgotten password page
        if (captchaResponse.isError()) {
            forgottenPasswordFieldError(forgottenPassword, flash, model, request,
                captchaResponse.getError().getFormatKey());

            return "forgottenPassword.jsp";
        }

        // Getting the need password reset information.
        PasswordResetInfo passwordResetInfo =
            passwordResetService.getPasswordResetInfo(forgottenPassword.getForgottenPasswordField());

        // Validate the request.
        if (!passwordResetInfo.isPasswordResetInfoValid()) {
            forgottenPasswordFieldError(forgottenPassword, flash, model, request,
                baseKey + "forgottenPassword.invalidProvidedInformation");
            return "forgottenPassword.jsp";
        }

        // Are we allowed to set this password?
        UserAuthenticationInfo userAuthenticationInfo =
            userDao.getUserAuthenticationInfo(passwordResetInfo.getUser().getUserID());
        if (!authService.supportsPasswordSet(userAuthenticationInfo.getAuthenticationCategory())) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "passwordChange.passwordChangeNotSupported"));
            return "redirect:login.jsp";
        }

        String passwordResetUrl =
            passwordResetService.getPasswordResetUrl(passwordResetInfo.getUser().getUsername(), request, true);
        YukonUserContext passwordResetUserContext =
            contextResolver.resolveContext(passwordResetInfo.getUser(), request);

        // Send out the forgotten password email
        try {
            passwordResetService.sendPasswordResetEmail(passwordResetUrl, passwordResetInfo.getContact(),
                passwordResetUserContext);
        } catch (NotFoundException e) {
            forgottenPasswordFieldError(forgottenPassword, flash, model, request,
                baseKey + "forgottenPassword.emailNotFound");
            return "forgottenPassword.jsp";
        } catch (EmailException e) {
            forgottenPasswordFieldError(forgottenPassword, flash, model, request,
                baseKey + "forgottenPassword.emailConnectionIssues");
            return "forgottenPassword.jsp";
        }

        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "forgottenPassword.forgottenPasswordEmailSent"));
        return "redirect:/login.jsp";
    }
    
    @RequestMapping(value = "/authenticated/change-password", method = RequestMethod.GET)
    public String changePassword(ModelMap model, LiteYukonUser user) {
        
        if (!authService.isPasswordExpired(user)) {
            globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
        }
        
        Login login = new Login();
        login.setUserId(user.getUserID());

        model.addAttribute("minPasswordAgeNotMet", !passwordPolicyService.isMinPasswordAgeMet(user, null));
        model.addAttribute("login", login);
        model.addAttribute("passwordPolicy", passwordPolicyService.getPasswordPolicy(user));
        
        return "changePasswordPopup.jsp";
    }
    
    @RequestMapping(value = "/authenticated/change-password", method = RequestMethod.POST)
    public String authenticatedPasswordChange(HttpServletRequest req, HttpServletResponse resp,
            @ModelAttribute Login login, BindingResult result, FlashScope flashScope, ModelMap model, LiteYukonUser user) {
        boolean isValidPassword = authService.validateOldPassword(user.getUsername(), login.getOldPassword());
        if (!isValidPassword) {
            flashScope.setMessage(new YukonMessageSourceResolvable(
                "yukon.web.modules.adminSetup.auth.user.incorrectPassword"), FlashScopeMessageType.ERROR);
            return null;
        }

        if (!authService.isPasswordExpired(user)) {
            globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
        }
        
        // Validate login change.
        LoginPasswordValidator validator = loginValidatorFactory.getPasswordValidator(user);
        validator.validate(login, result);
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("passwordPolicy", passwordPolicyService.getPasswordPolicy(user));
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            
            return "changePasswordPopup.jsp";
        }
        
        // Update the user's password to their new supplied password.
        authService.setPassword(user, login.getPassword1(), user);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.login.passwordChange.successful.reLogin"));
        resp.setStatus(HttpStatus.NO_CONTENT.value());
       
        HttpSession session = req.getSession(false);
        session.setAttribute("passwordExpired", true);
        return null;
    }
    
    @RequestMapping(value = "/change-password", method = RequestMethod.GET)
    public String changePassword(ModelMap model, String k) {
        
        LiteYukonUser passwordResetUser = passwordResetService.findUserFromPasswordKey(k);
        if (passwordResetUser == null) {
            return "redirect:/login.jsp";
        }
        
        // Check to see if the user's password is expired.  
        // If their password is not expired the user should only be able to 
        // hit this request if they have password recovery enabled.
        if (!authService.isPasswordExpired(passwordResetUser)) {
            globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
        }
        
        /* Login */
        Login login = new Login();
        login.setUserId(passwordResetUser.getUserID());
        login.setUsername(passwordResetUser.getUsername());
        login.setUserGroupName(userGroupDao.getLiteUserGroup(passwordResetUser.getUserGroupId()).getUserGroupName());
        
        model.addAttribute("k", k);
        model.addAttribute("login", login);
        model.addAttribute("passwordPolicy", passwordPolicyService.getPasswordPolicy(passwordResetUser));
        
        return "changePassword.jsp";
    }
    
    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    public String submitChangePassword(@ModelAttribute Login login, BindingResult result,
                                       FlashScope flashScope, String k, ModelMap model) {
        // Check to see if the supplied userId matches up with the hex key.  I'm not sure if this is really necessary.  
        // It might be overkill.
        LiteYukonUser suppliedPasswordResetUser = userDao.getLiteYukonUser(login.getUserId());
        LiteYukonUser passwordResetUser = passwordResetService.findUserFromPasswordKey(k);
        if (passwordResetUser == null || !passwordResetUser.equals(suppliedPasswordResetUser)) {
            return "redirect:/login.jsp";
        }
        
        // Check to see if the user's password is expired.  
        // If their password is not expired the user should only be able to 
        // hit this request if they have password recovery enabled.
        if (!authService.isPasswordExpired(passwordResetUser)) {
            globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
        }
        
        // Validate login change.
        LoginPasswordValidator passwordValidator = loginValidatorFactory.getPasswordValidator(passwordResetUser);
        LoginUsernameValidator usernameValidator = loginValidatorFactory.getUsernameValidator(passwordResetUser);
        
        passwordValidator.validate(login, result);
        usernameValidator.validate(login, result);
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("k", k);
            model.addAttribute("passwordPolicy", passwordPolicyService.getPasswordPolicy(passwordResetUser));
            return "changePassword.jsp";
        }
        
        // Update the user's password to their new supplied password.
        authService.setPassword(passwordResetUser, login.getPassword1(), passwordResetUser);
        passwordResetService.invalidatePasswordKey(k);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.login.passwordChange.successful"));
        
        return "redirect:/login.jsp";
    }
    
    @RequestMapping(value="/check-password", method=RequestMethod.POST)
    public @ResponseBody Map<String, Object> checkPassword(HttpServletResponse resp, 
            int userId, String password) {
        LiteYukonUser user = userDao.getLiteYukonUser(userId);
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroupByUserId(userId);
        
        // Check to see if the user's password is expired.  
        // If their password is not expired the user should only be able to 
        // hit this request if they have password recovery enabled.
        if (!authService.isPasswordExpired(user)) {
            globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
        }
        
        // Login validator is not appropriate for checking the password.  I wish it was
        // but we need information on specific rules not met check password policies.
        Set<PasswordPolicyError> errantPasswordPolicies = 
                passwordPolicyService.getPasswordPolicyErrors(password, user, userGroup);
        // Separate out the validations.
        Set<PasswordPolicyError> validPasswordPolicies = new HashSet<>(Arrays.asList(PasswordPolicyError.values()));
        validPasswordPolicies.removeAll(errantPasswordPolicies); 
        
        // Check policy rules
        Set<PolicyRule> validPolicyRules = passwordPolicyService.getValidPolicyRules(password, user, userGroup);
        Set<PolicyRule> errantPolicyRules = new HashSet<>(Arrays.asList(PolicyRule.values()));
        errantPolicyRules.removeAll(validPolicyRules);
        
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(4);
        result.put("policy_errors", errantPasswordPolicies);
        result.put("rule_errors", errantPolicyRules);
        result.put("policy_validations", validPasswordPolicies);
        result.put("rule_validations", validPolicyRules);
        
        // All Password Policies MUST be met to be a valid password
        if (errantPasswordPolicies.size() == 0) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        return result;
    }
    
    /**
     * Expires the password and user is forced to re-login after changing his password
     */
    @RequestMapping(value = "/expirePassword", method = RequestMethod.POST)
    public void expirePassword(HttpServletRequest req) {
        loginService.invalidateSession(req, "Password has been changed.");
    }

    /**
     * This method sets the forgottenPasswordField with the errorCode supplied 
     * and rebuilds the model for rejecting the request.
     */
    private void forgottenPasswordFieldError(ForgottenPassword forgottenPassword, FlashScope flashScope, ModelMap model, 
                                             HttpServletRequest request, String errorCode) {
        
        flashScope.setError(new YukonMessageSourceResolvable(errorCode));
        setupModelMap(model, request);
        model.addAttribute("forgottenPassword", forgottenPassword);
    }
    
    /**
     * Sets up the need information for the view to be rendered
     */
    private void setupModelMap(ModelMap model, HttpServletRequest request) {
        boolean captchaEnabled = globalSettingDao.getBoolean(GlobalSettingType.ENABLE_CAPTCHAS);
        
        model.addAttribute("captchaSiteKey", captchaService.getSiteKey());
        model.addAttribute("captchaEnabled", captchaEnabled);
        model.addAttribute("locale", RequestContextUtils.getLocale(request).getLanguage());
    }
    
}
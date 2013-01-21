package com.cannontech.web.login;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.validator.YukonValidationUtils;
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
import com.cannontech.web.stars.dr.operator.model.LoginBackingBean;
import com.cannontech.web.stars.dr.operator.validator.LoginPasswordValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginUsernameValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginValidatorFactory;
import com.cannontech.web.stars.service.PasswordResetService;
import com.cannontech.web.util.YukonUserContextResolver;

@Controller
public class PasswordResetController {

    @Autowired private AuthenticationService authenticationService;
    @Autowired private CaptchaService captchaService;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private LoginValidatorFactory loginValidatorFactory;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private YukonUserContextResolver yukonUserContextResolver;
    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private GlobalSettingDao globalSettingDao;

    @RequestMapping(value = "/forgottenPassword", method = RequestMethod.GET)
    public String newForgottenPassword(ModelMap model, HttpServletRequest request) throws Exception {
        globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
        
        setupModelMap(model, request);
        model.addAttribute("forgottenPassword", new ForgottenPassword());
        
        return "forgottenPassword.jsp";
    }

    @RequestMapping(value = "/forgottenPassword", method = RequestMethod.POST)
    public String forgottenPasswordRequest(ModelMap model, FlashScope flashScope, HttpServletRequest request, 
                                           @ModelAttribute ForgottenPassword forgottenPassword, 
                                           String recaptcha_challenge_field, String recaptcha_response_field)
    throws Exception {
        globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
        
        // Process Captcha
        Captcha captcha = new Captcha(request.getRemoteAddr(), recaptcha_challenge_field, recaptcha_response_field);
        CaptchaResponse captchaResponse = captchaService.checkCaptcha(captcha);

        // The Captcha failed.  return the user the forgotten password page
        if (captchaResponse.isError()) {
            forgottenPasswordFieldError(forgottenPassword, flashScope, model, request, captchaResponse.getError().getFormatKey()); 
            return "forgottenPassword.jsp";
        }

        // Getting the need password reset information.
        PasswordResetInfo passwordResetInfo = passwordResetService.getPasswordResetInfo(forgottenPassword.getForgottenPasswordField());

        // Validate the request.
        if (!passwordResetInfo.isPasswordResetInfoValid()) {
            forgottenPasswordFieldError(forgottenPassword, flashScope, model, request, "yukon.web.modules.login.forgottenPassword.invalidProvidedInformation");
            return "forgottenPassword.jsp";
        }

        // Are we allowed to set this password?
        UserAuthenticationInfo userAuthenticationInfo =
                yukonUserDao.getUserAuthenticationInfo(passwordResetInfo.getUser().getUserID());
        if (!authenticationService.supportsPasswordSet(userAuthenticationInfo.getAuthenticationCategory())) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.login.passwordChange.passwordChangeNotSupported"));
            return "redirect:login.jsp";
        }

        String passwordResetUrl = passwordResetService.getPasswordResetUrl(passwordResetInfo.getUser().getUsername(), request);
        YukonUserContext passwordResetUserContext = yukonUserContextResolver.resolveContext(passwordResetInfo.getUser(), request);

        // Send out the forgotten password email
        try {
            passwordResetService.sendPasswordResetEmail(passwordResetUrl, passwordResetInfo.getContact(), passwordResetUserContext);
        } catch (NotFoundException e) {
            forgottenPasswordFieldError(forgottenPassword, flashScope, model, request, "yukon.web.modules.login.forgottenPassword.emailNotFound"); 
            return "forgottenPassword.jsp";
        } catch (EmailException e) {
            forgottenPasswordFieldError(forgottenPassword, flashScope, model, request, "yukon.web.modules.login.forgottenPassword.emailConnectionIssues"); 
            return "forgottenPassword.jsp";
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.login.forgottenPassword.forgottenPasswordEmailSent"));
        return "redirect:/login.jsp";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePassword(ModelMap model, FlashScope flashScope, String k) {

        LiteYukonUser passwordResetUser = passwordResetService.findUserFromPasswordKey(k);
        if (passwordResetUser == null) {
            return "redirect:/login.jsp";
        }
        
        // Check to see if the user's password is expired.  If their password is not expired the user should only be able to 
        // hit this request if they have password recovery enabled.
        if (!authenticationService.isPasswordExpired(passwordResetUser)) {
            globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
        }
        
        /* LoginBackingBean */
        LoginBackingBean loginBackingBean = new LoginBackingBean();
        loginBackingBean.setUserId(passwordResetUser.getUserID());
        loginBackingBean.setUsername(passwordResetUser.getUsername());
        loginBackingBean.setUserGroupName(userGroupDao.getLiteUserGroup(passwordResetUser.getUserGroupId()).getUserGroupName());
        
        model.addAttribute("k", k);
        model.addAttribute("loginBackingBean", loginBackingBean);
        model.addAttribute("passwordPolicy", passwordPolicyService.getPasswordPolicy(passwordResetUser));
        
        return "changePassword.jsp";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String submitChangePassword(@ModelAttribute LoginBackingBean loginBackingBean, BindingResult bindingResult, FlashScope flashScope, String k, ModelMap model) {
        // Check to see if the supplied userId matches up with the hex key.  I'm not sure if this is really necessary.  It might be overkill.
        LiteYukonUser suppliedPasswordResetUser = yukonUserDao.getLiteYukonUser(loginBackingBean.getUserId());
        LiteYukonUser passwordResetUser = passwordResetService.findUserFromPasswordKey(k);
        if(passwordResetUser == null || !passwordResetUser.equals(suppliedPasswordResetUser)) {
            return "redirect:/login.jsp";
        }

        // Check to see if the user's password is expired.  If their password is not expired the user should only be able to 
        // hit this request if they have password recovery enabled.
        if (!authenticationService.isPasswordExpired(passwordResetUser)) {
            globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
        }

        // Validate login change.
        LoginPasswordValidator passwordValidator = loginValidatorFactory.getPasswordValidator(passwordResetUser);
        LoginUsernameValidator usernameValidator = loginValidatorFactory.getUsernameValidator(passwordResetUser);

        passwordValidator.validate(loginBackingBean, bindingResult);
        usernameValidator.validate(loginBackingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("k", k);
            model.addAttribute("passwordPolicy", passwordPolicyService.getPasswordPolicy(passwordResetUser));
            return "changePassword.jsp";
        }

        // Update the user's password to their new supplied password.
        authenticationService.setPassword(passwordResetUser, loginBackingBean.getPassword1());
        passwordResetService.invalidatePasswordKey(k);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.login.passwordChange.successful"));
        return "redirect:/login.jsp";
    }
    
    @RequestMapping(value="/checkPassword", method=RequestMethod.POST)
    public @ResponseBody JSONObject checkPassword(@ModelAttribute LoginBackingBean loginBackingBean, BindingResult bindingResult, 
    																	FlashScope flashScope, String k,  ModelMap model,
    																	HttpServletResponse response,  HttpServletRequest request) {

        // Check to see if the supplied userId matches up with the hex key.  I'm not sure if this is really necessary.  It might be overkill.
    	LiteYukonUser suppliedPasswordResetUser = yukonUserDao.getLiteYukonUser(loginBackingBean.getUserId());
    	LiteYukonUser passwordResetUser = passwordResetService.findUserFromPasswordKey(k);
    	LiteUserGroup liteUserGroup = userGroupDao.getLiteUserGroupByUserGroupName(loginBackingBean.getUserGroupName());

        // Check to see if the user's password is expired.  If their password is not expired the user should only be able to 
        // hit this request if they have password recovery enabled.
        if (!authenticationService.isPasswordExpired(passwordResetUser)) {
            globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);
        }

    	//login validator is not appropriate for checking the password.  I wish it was
        //but we need information on specific rules not met
        //check password policies
        Set<PasswordPolicyError> errantPasswordPolicies = passwordPolicyService.getPasswordPolicyErrors(loginBackingBean.getPassword1(), passwordResetUser, liteUserGroup);
        Set<PasswordPolicyError> validPasswordPolicies = new HashSet<PasswordPolicyError>(Arrays.asList(PasswordPolicyError.values()));
        validPasswordPolicies.removeAll(errantPasswordPolicies); //separate out the validations
        
        //check policy rules
        Set<PolicyRule> validPolicyRules = passwordPolicyService.getValidPolicyRules(loginBackingBean.getPassword1(), passwordResetUser, liteUserGroup);
        Set<PolicyRule> errantPolicyRules = new HashSet<PolicyRule>(Arrays.asList(PolicyRule.values()));
        errantPolicyRules.removeAll(validPolicyRules);

        JSONObject result = new JSONObject();
        result.put("policy_errors", errantPasswordPolicies);
        result.put("rule_errors", errantPolicyRules);
        result.put("policy_validations", validPasswordPolicies);
        result.put("rule_validations", validPolicyRules);
        
        //All Password Policies MUST be met to be a valid password
        if (errantPasswordPolicies.size() == 0) {
            /* tell the browser this password is good*/
            response.setStatus(HttpServletResponse.SC_OK);
        } else if (passwordResetUser == null || !passwordResetUser.equals(suppliedPasswordResetUser)) {
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            /* tell the browser there was a problem */
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        return result;
    }

    /**
     * This method sets the forgottenPasswordField with the errorCode supplied and rebuilds the model for rejecting the request.
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
        
        model.addAttribute("captchaPublicKey", captchaService.getPublicKey());
        model.addAttribute("captchaEnabled", captchaEnabled);
        model.addAttribute("locale", RequestContextUtils.getLocale(request));
    }
}
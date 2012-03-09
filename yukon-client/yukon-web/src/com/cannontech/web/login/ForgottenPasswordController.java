package com.cannontech.web.login;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.login.model.PasswordResetInfo;
import com.cannontech.stars.core.login.service.ForgottenPasswordService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.captcha.model.Captcha;
import com.cannontech.web.common.captcha.model.CaptchaResponse;
import com.cannontech.web.common.captcha.service.CaptchaService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.stars.dr.operator.model.LoginBackingBean;
import com.cannontech.web.stars.dr.operator.validator.LoginValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginValidatorFactory;
import com.cannontech.web.util.YukonUserContextResolver;
import com.google.common.collect.MapMaker;

@Controller
public class ForgottenPasswordController {

    @Autowired private AuthenticationService authenticationService;
    @Autowired private CaptchaService captchaService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private ForgottenPasswordService forgottenPasswordService;
    @Autowired private LoginValidatorFactory loginValidatorFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private YukonUserContextResolver yukonUserContextResolver;

    private Map<UUID, LiteYukonUser> keyToUserMap = new MapMaker().concurrencyLevel(1).expireAfterWrite(1, TimeUnit.HOURS).makeMap();
    
    @RequestMapping(value = "/forgottenPassword", method = RequestMethod.GET)
    public String newForgottenPassword(ModelMap model, HttpServletRequest request) throws Exception {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ENABLE_PASSWORD_RECOVERY, null);
        boolean captchaEnabled = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ENABLE_CAPTCHAS, null);
        
        model.addAttribute("captchaPublicKey", captchaService.getPublicKey());
        model.addAttribute("captchaEnabled", captchaEnabled);
        model.addAttribute("locale", RequestContextUtils.getLocale(request));
        
        return "forgottenPassword.jsp";
    }
    
    @RequestMapping(value = "/forgottenPassword", method = RequestMethod.POST)
    public String forgottenPasswordRequest(ModelMap model, FlashScope flashScope, HttpServletRequest request, 
                                           String recaptcha_challenge_field, String recaptcha_response_field, String forgottenPasswordField)
    throws Exception {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ENABLE_PASSWORD_RECOVERY, null);
        
        // Process Captcha
        Captcha captcha = new Captcha(request.getRemoteAddr(), recaptcha_challenge_field, recaptcha_response_field);
        CaptchaResponse captchaResponse = captchaService.checkCaptcha(captcha);

        // The Captcha failed.  return the user the forgotten password page
        if (captchaResponse.isError()) {
            flashScope.setError(captchaResponse.getErrorMessageSourceResolvable());
            return "redirect:forgottenPassword";
        }

        // Getting the need password reset information.
        PasswordResetInfo passwordResetInfo = forgottenPasswordService.getPasswordResetInfo(forgottenPasswordField);
        if (!passwordResetInfo.isPasswordResetInfoValid()) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.login.forgottenPassword.invalidProvidedInformation", forgottenPasswordField));
            return "redirect:forgottenPassword";
        }
        
        // Are we allowed to set this password.
        if (!authenticationService.supportsPasswordChange(passwordResetInfo.getUser().getAuthType())) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.login.passwordChange.passwordChangeNotSupported"));
            return "redirect:login.jsp";
        }

        // Add the entry to the key map and send an email out to the user.
        UUID passwordResetKey = UUID.randomUUID();
        keyToUserMap.put(passwordResetKey, passwordResetInfo.getUser());
        String forgottenPasswordResetUrl = getForgottenPasswordResetUrl(passwordResetKey, request);
        YukonUserContext passwordResetUserContext = yukonUserContextResolver.resolveContext(passwordResetInfo.getUser(), request);

        try {
            forgottenPasswordService.sendPasswordResetEmail(forgottenPasswordResetUrl, passwordResetInfo.getContact(), passwordResetUserContext);
        } catch (NotFoundException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.login.forgottenPassword.emailNotFound"));
            return "redirect:login.jsp";
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.login.forgottenPassword.forgottenPasswordEmailSent"));
        return "redirect:/login.jsp";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePassword(ModelMap model, FlashScope flashScope, String k)
    throws Exception {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ENABLE_PASSWORD_RECOVERY, null);
        LiteYukonUser passwordResetUser = keyToUserMap.get(UUID.fromString(k));
        if (passwordResetUser == null) {
            return "redirect:/login.jsp";
        }
        
        /* LoginBackingBean */
        LoginBackingBean loginBackingBean = new LoginBackingBean();
        loginBackingBean.setUserId(passwordResetUser.getUserID());
        loginBackingBean.setAuthType(passwordResetUser.getAuthType());
        loginBackingBean.setUsername(passwordResetUser.getUsername());
        
        model.addAttribute("k", k);
        model.addAttribute("loginBackingBean", loginBackingBean);
        return "changePassword.jsp";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String submitChangePassword(@ModelAttribute LoginBackingBean loginBackingBean, BindingResult bindingResult, FlashScope flashScope, String k, ModelMap model)
    throws Exception {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ENABLE_PASSWORD_RECOVERY, null);
        
        // Check to see if the supplied userId matches up with the hex key.  I'm not sure if this is really necessary.  It might be overkill.
        LiteYukonUser suppliedPasswordResetUser = yukonUserDao.getLiteYukonUser(loginBackingBean.getUserId());
        LiteYukonUser passwordResetUser  = keyToUserMap.get(UUID.fromString(k));
        if(passwordResetUser == null || !passwordResetUser.equals(suppliedPasswordResetUser)) {
            return "redirect:/login.jsp";
        }

        // Validate login change.
        LoginValidator loginValidator = loginValidatorFactory.getLoginValidator(null);
        loginValidator.validate(loginBackingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("k", k);
            return "changePassword.jsp";
        }

        // Update the user's password to their new supplied password.
        authenticationService.setPassword(passwordResetUser, loginBackingBean.getPassword1());
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.login.passwordChange.successful"));
        return "redirect:/login.jsp";
    }

    /**
     * This method creates the url need to reset a user's password.  
     */
    private String getForgottenPasswordResetUrl(UUID passwordResetKey, HttpServletRequest request) {
        StringBuilder url = new StringBuilder();
        url.append(request.getScheme()+"://");
        
        // Get the external base url.
        StringBuilder defaultYukonExternalUrl = new StringBuilder();
        defaultYukonExternalUrl.append(request.getServerName());
        // We don't need to added 80 as a port since it is used by default
        if (request.getServerPort() != 80) {
            defaultYukonExternalUrl.append(":"+request.getServerPort());
        }
        String baseurl = configurationSource.getString(MasterConfigStringKeysEnum.YUKON_EXTERNAL_URL, defaultYukonExternalUrl.toString());
        url.append(baseurl);

        url.append(request.getServletPath());
        url.append("/changePassword?k="+passwordResetKey);
        
        return url.toString();
    }
}
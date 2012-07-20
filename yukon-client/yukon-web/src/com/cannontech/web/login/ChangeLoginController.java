package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.core.login.service.PasswordResetService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

@Controller
public class ChangeLoginController {
    public static final String LOGIN_CHANGE_MESSAGE_PARAM = "loginChangeMsg";
    public static final String LOGIN_CHANGE_SUCCESS_PARAM = "success";
    
    @Autowired private AuthenticationService authenticationService;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private YukonUserDao yukonUserDao;
    
    @RequestMapping(value = "/changelogin", method = RequestMethod.GET)
    public String view(String redirectUrl, HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = yukonUserContext.getYukonUser();
        map.addAttribute("user", user);

        boolean disablePasswordChange = !authenticationService.supportsPasswordSet(user.getAuthType());
        map.addAttribute("disablePasswordChange", disablePasswordChange);
        
        map.addAttribute("redirectUrl", redirectUrl);
        
        return "changeLogin.jsp";
    }
    
    @RequestMapping(value = "/changelogin/updatepassword", method = RequestMethod.POST)
    public ModelAndView updatePassword(String oldPassword, String newPassword, String confirm, String redirectUrl, HttpServletRequest request) 
    throws Exception {
        final YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        final LiteYukonUser user = yukonUserContext.getYukonUser();
        
        systemEventLogService.loginPasswordChangeAttemptedByConsumer(user);
        
        final AuthType type = user.getAuthType();
        rolePropertyDao.verifyProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_PASSWORD, user);
        
        boolean isValidPassword = false;
        boolean success = false;
        long retrySeconds = 0;
        try {
            isValidPassword = isValidPassword(user.getUsername(), oldPassword);
        } catch (AuthenticationThrottleException e){
            retrySeconds = e.getThrottleSeconds();
        } catch (PasswordExpiredException e) {
            CTILogger.debug("The password for "+user.getUsername()+" is expired.", e);
            String passwordResetUrl = passwordResetService.getPasswordResetUrl(user.getUsername(), request);
            return new ModelAndView("redirect:"+passwordResetUrl);
        }
        ChangeLoginMessage loginMsg = validatePassword(oldPassword, newPassword, confirm, user, type, isValidPassword);
        
        if (loginMsg.equals(ChangeLoginMessage.LOGIN_PASSWORD_CHANGED)) {
            authenticationService.setPassword(user, newPassword);
            success = true;
        }
        
        return sendRedirect(request, redirectUrl, loginMsg, retrySeconds, success);
    }

    /**
     * This method validates the password and makes sure it follows the password policy if one exists.
     */
    private ChangeLoginMessage validatePassword(String oldPassword, String newPassword, String confirmPassword, final LiteYukonUser user,
                                               final AuthType type, boolean isValidPassword) {
        
        boolean supportsPasswordChange = authenticationService.supportsPasswordSet(type);
        boolean hasRequiredFields = hasRequiredFields(oldPassword, newPassword, confirmPassword);

        if (!isValidPassword) {
            return ChangeLoginMessage.INVALID_CREDENTIALS_PASSWORD_CHANGE;
        } else if (!supportsPasswordChange) {
            return ChangeLoginMessage.PASSWORD_CHANGE_NOT_SUPPORTED;
        } else if (!hasRequiredFields) {
            return ChangeLoginMessage.REQUIRED_FIELDS_MISSING;
        } else if (!newPassword.equals(confirmPassword)) {
            return ChangeLoginMessage.NO_PASSWORDMATCH;
        } else {
            // Check the password against the password policy.
            PasswordPolicyError passwordPolicyError = passwordPolicyService.checkPasswordPolicy(newPassword, user);
            if (passwordPolicyError != null) {
                return ChangeLoginMessage.valueOf(passwordPolicyError.name());
            }
        }
        
        return ChangeLoginMessage.LOGIN_PASSWORD_CHANGED;
    }
    
    @RequestMapping(value = "/changelogin/updateusername", method = RequestMethod.POST)
    public ModelAndView updateUsername(String username, String oldPassword, String redirectUrl, HttpServletRequest request) 
    throws Exception {

        final YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        final LiteYukonUser user = yukonUserContext.getYukonUser();

        systemEventLogService.loginUsernameChangeAttemptedByConsumer(user, username);
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_USERNAME, user);
        boolean isValidPassword = false;
        boolean success = false;
        long retrySeconds = 0;
        try {
            isValidPassword = isValidPassword(user.getUsername(), oldPassword);
        } catch (AuthenticationThrottleException e){
            retrySeconds = e.getThrottleSeconds();
        }        
        final boolean hasRequiredFields = hasRequiredFields(username, oldPassword);
        
        ChangeLoginMessage loginMsg;
        
        if (!isValidPassword) {
            loginMsg = ChangeLoginMessage.INVALID_CREDENTIALS_USERNAME_CHANGE;
        }
        else if (!hasRequiredFields) {
            loginMsg = ChangeLoginMessage.REQUIRED_FIELDS_MISSING;
        }
        else {
            try {
                saveUsernameChange(request, user, username);
                loginMsg = ChangeLoginMessage.LOGIN_USERNAME_CHANGED;
                success = true;
            } catch (NotAuthorizedException e) {
                loginMsg = ChangeLoginMessage.USER_EXISTS;
            }
        }
        
        return sendRedirect(request, redirectUrl, loginMsg, retrySeconds, success);
    }
    
    private boolean hasRequiredFields(String... fields) {
        for (final String field : fields) {
            if ("".equals(field)) return false;
        }
        return true;
    }
    
    private boolean isValidPassword(String username, String password) 
    throws AuthenticationThrottleException, PasswordExpiredException {
        try {
            authenticationService.login(username, password);
            return true;
        } catch (AuthenticationThrottleException e) {
            throw e;
        } catch (BadAuthenticationException e) {
            return false;
        }
    }
    
    private void saveUsernameChange(HttpServletRequest request, LiteYukonUser user, String username) 
        throws NotAuthorizedException {
        
        user.setUsername(username);
        yukonUserDao.changeUsername(user, user.getUserID(), username);

        HttpSession session = request.getSession(false);
        session.setAttribute(LoginController.YUKON_USER, user);
    }
    
    private ModelAndView sendRedirect(HttpServletRequest request, String redirectUrl, ChangeLoginMessage loginMessage,
                                long retrySeconds, boolean success) {
        String safeRedirectUrl = ServletUtil.createSafeRedirectUrl(request, redirectUrl);
        if (loginMessage != null) {
            StringBuilder msgParams = new StringBuilder().append("?")
                                                         .append(LOGIN_CHANGE_MESSAGE_PARAM)
                                                         .append("=")
                                                         .append(loginMessage.name())
                                                         .append("&")
                                                         .append(LOGIN_CHANGE_SUCCESS_PARAM)
                                                         .append("=")
                                                         .append(success);
            
            if (retrySeconds > 0) {
                msgParams.append("&")
                          .append(LoginController.AUTH_RETRY_SECONDS_PARAM)
                          .append("=")
                          .append(retrySeconds);
            }
            
            safeRedirectUrl += msgParams.toString();
        }
        
        return new ModelAndView("redirect:"+safeRedirectUrl);
    }
}
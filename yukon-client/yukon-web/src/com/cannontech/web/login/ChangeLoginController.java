package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.service.PasswordResetService;

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
    public String view(String redirectUrl, HttpServletRequest request, ModelMap model) {
        
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = yukonUserContext.getYukonUser();
        model.addAttribute("user", user);

        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
        boolean disablePasswordChange =
                !authenticationService.supportsPasswordSet(userAuthenticationInfo.getAuthenticationCategory());
        model.addAttribute("disablePasswordChange", disablePasswordChange);

        model.addAttribute("redirectUrl", redirectUrl);
        
        return "changeLogin.jsp";
    }
    
    @RequestMapping(value = "/changelogin/updatepassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String confirm, String redirectUrl,
            HttpServletRequest request) throws Exception {
        final YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        final LiteYukonUser user = yukonUserContext.getYukonUser();
        
        systemEventLogService.loginPasswordChangeAttempted(user, EventSource.CONSUMER);

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
            String passwordResetUrl = passwordResetService.getPasswordResetUrl(user.getUsername(), request, false);
            return "redirect:" + passwordResetUrl;
        }
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
        AuthenticationCategory authenticationCategory = userAuthenticationInfo.getAuthenticationCategory();
        ChangeLoginMessage loginMsg = validatePassword(newPassword, confirm, user, authenticationCategory, isValidPassword);
        
        if (loginMsg.equals(ChangeLoginMessage.LOGIN_PASSWORD_CHANGED)) {
            authenticationService.setPassword(user, newPassword, user);
            success = true;
        }
        
        return sendRedirect(redirectUrl, loginMsg, retrySeconds, success);
    }

    /**
     * This method validates the password and makes sure it follows the password policy if one exists.
     */
    private ChangeLoginMessage validatePassword(String newPassword, String confirmPassword, final LiteYukonUser user,
                                               AuthenticationCategory authenticationCategory, boolean isValidPassword) {
        boolean supportsPasswordChange = authenticationService.supportsPasswordSet(authenticationCategory);

        if (!isValidPassword) {
            return ChangeLoginMessage.INVALID_CREDENTIALS_PASSWORD_CHANGE;
        } else if (!supportsPasswordChange) {
            return ChangeLoginMessage.PASSWORD_CHANGE_NOT_SUPPORTED;
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
    public String updateUsername(String username, String oldPassword, String redirectUrl, HttpServletRequest request) 
            throws Exception {
        final YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        final LiteYukonUser user = yukonUserContext.getYukonUser();

        systemEventLogService.loginUsernameChangeAttempted(user, username, EventSource.CONSUMER);
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_USERNAME, user);
        boolean isValidPassword = false;
        boolean success = false;
        long retrySeconds = 0;
        try {
            isValidPassword = isValidPassword(user.getUsername(), oldPassword);
        } catch (AuthenticationThrottleException e){
            retrySeconds = e.getThrottleSeconds();
        }        
        
        ChangeLoginMessage loginMsg;
        
        if (!isValidPassword) {
            loginMsg = ChangeLoginMessage.INVALID_CREDENTIALS_USERNAME_CHANGE;
        } else if (StringUtils.isBlank(username)) {
            loginMsg = ChangeLoginMessage.REQUIRED_FIELDS_MISSING;
        } else {
            try {
                saveUsernameChange(request, user, username);
                loginMsg = ChangeLoginMessage.LOGIN_USERNAME_CHANGED;
                success = true;
            } catch (NotAuthorizedException e) {
                loginMsg = ChangeLoginMessage.USER_EXISTS;
            }
        }
        
        return sendRedirect(redirectUrl, loginMsg, retrySeconds, success);
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

    private String sendRedirect(String redirectUrl, ChangeLoginMessage loginMessage, long retrySeconds,
            boolean success) {
        if (loginMessage != null) {
            redirectUrl += "?" + LOGIN_CHANGE_MESSAGE_PARAM + "=" + loginMessage.name() + "&"
                    + LOGIN_CHANGE_SUCCESS_PARAM + "=" + success;
            if (retrySeconds > 0) {
                redirectUrl += "&" + LoginController.AUTH_RETRY_SECONDS_PARAM + "=" + retrySeconds;
            }
        }
        
        return "redirect:" + redirectUrl;
    }
}

package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.constants.LoginController;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

@Controller
public class ChangeLoginController {
    public static final String LOGIN_CHANGE_MESSAGE_PARAM = "loginChangeMsg";
    public static final String LOGIN_CHANGE_SUCCESS_PARAM = "success";
    private YukonUserDao yukonUserDao;
    private RolePropertyDao rolePropertyDao;
    private AuthenticationService authenticationService;
    
    @RequestMapping(value = "/changelogin", method = RequestMethod.GET)
    public String view(String redirectUrl, HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = yukonUserContext.getYukonUser();
        map.addAttribute("user", user);
        
        boolean disablePasswordChange = !authenticationService.supportsPasswordChange(user.getAuthType());
        map.addAttribute("disablePasswordChange", disablePasswordChange);
        
        map.addAttribute("redirectUrl", redirectUrl);
        
        return "changeLogin.jsp";
    }
    
    @RequestMapping(value = "/changelogin/updatepassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, 
            String confirm, String redirectUrl, HttpServletRequest request) 
                throws Exception {
        final YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        final LiteYukonUser user = yukonUserContext.getYukonUser();
        final AuthType type = user.getAuthType();
        rolePropertyDao.verifyProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_PASSWORD, user);
        
        boolean isValidPassword = false;
        boolean success = false;
        long retrySeconds = 0;
        try {
            isValidPassword = isValidPassword(user.getUsername(), oldPassword);
        } catch (AuthenticationThrottleException e){
            retrySeconds = e.getThrottleSeconds();
        }
        boolean supportsPasswordChange = authenticationService.supportsPasswordChange(type);
        boolean hasRequiredFields = hasRequiredFields(oldPassword, newPassword, confirm);

        ChangeLoginMessage loginMsg;
        
        if (!isValidPassword) {
            loginMsg = ChangeLoginMessage.INVALID_CREDENTIALS_PASSWORD_CHANGE;
        }
        else if (!supportsPasswordChange) {
            loginMsg = ChangeLoginMessage.PASSWORD_CHANGE_NOT_SUPPORTED;
        }
        else if (!hasRequiredFields) {
            loginMsg = ChangeLoginMessage.REQUIRED_FIELDS_MISSING;
        }
        else if (!newPassword.equals(confirm)) {
            loginMsg = ChangeLoginMessage.NO_PASSWORDMATCH;
        }
        else {
            authenticationService.changePassword(user, oldPassword, newPassword);
            loginMsg = ChangeLoginMessage.LOGIN_PASSWORD_CHANGED;
            success = true;
        }
        
        return sendRedirect(request, redirectUrl, loginMsg, retrySeconds, success);
    }
    
    @RequestMapping(value = "/changelogin/updateusername", method = RequestMethod.POST)
    public String updateUsername(String username, String oldPassword,
            String redirectUrl, HttpServletRequest request) throws Exception {
        
        final YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        final LiteYukonUser user = yukonUserContext.getYukonUser();
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
        throws AuthenticationThrottleException {
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
        yukonUserDao.changeUsername(user.getUserID(), username);

        HttpSession session = request.getSession(false);
        session.setAttribute(LoginController.YUKON_USER, user);
    }
    
    private String sendRedirect(HttpServletRequest request, String redirectUrl, ChangeLoginMessage loginMessage,
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
        
        String redirect = "redirect:" + safeRedirectUrl; 
        return redirect;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    @Autowired
    public void setAuthenticationService(
            AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
    	this.rolePropertyDao = rolePropertyDao;
    }
}

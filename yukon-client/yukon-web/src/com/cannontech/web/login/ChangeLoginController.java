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
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

@Controller
public class ChangeLoginController {
    public static final String LOGIN_ERROR_PARAM = "loginError";
    private YukonUserDao yukonUserDao; 
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
        
        final boolean isValidPassword = isValidPassword(user.getUsername(), oldPassword);
        boolean supportsPasswordChange = authenticationService.supportsPasswordChange(type);
        boolean hasRequiredFields = hasRequiredFields(oldPassword, newPassword, confirm);

        ChangeLoginError loginError;
        
        if (!isValidPassword) {
            loginError = ChangeLoginError.INVALID_CREDENTIALS;
        }
        else if (!supportsPasswordChange) {
            loginError =  ChangeLoginError.PASSWORD_CHANGE_NOT_SUPPORTED;
        }
        else if (!hasRequiredFields) {
            loginError = ChangeLoginError.REQUIRED_FIELDS_MISSING;
        }
        else if (!newPassword.equals(confirm)) {
            loginError = ChangeLoginError.NO_PASSWORDMATCH;
        }
        else {
            authenticationService.changePassword(user, oldPassword, newPassword);
            loginError = ChangeLoginError.NONE;
        }
        
        return sendRedirect(request, redirectUrl, loginError);
    }
    
    @RequestMapping(value = "/changelogin/updateusername", method = RequestMethod.POST)
    public String updateUsername(String username, String oldPassword,
            String redirectUrl, HttpServletRequest request) throws Exception {
        
        final YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        final LiteYukonUser user = yukonUserContext.getYukonUser();
        
        final boolean isValidPassword = isValidPassword(user.getUsername(), oldPassword);
        final boolean hasRequiredFields = hasRequiredFields(username, oldPassword);
        
        ChangeLoginError loginError;
        
        if (!isValidPassword) {
            loginError = ChangeLoginError.INVALID_CREDENTIALS;
        }
        else if (!hasRequiredFields) {
            loginError = ChangeLoginError.REQUIRED_FIELDS_MISSING;
        }
        else {
            try {
                saveUsernameChange(request, user, username);
                loginError = ChangeLoginError.NONE;
            } catch (NotAuthorizedException e) {
                loginError = ChangeLoginError.USER_EXISTS;
            }
        }
        
        return sendRedirect(request, redirectUrl, loginError);
    }
    
    private boolean hasRequiredFields(String... fields) {
        for (final String field : fields) {
            if ("".equals(field)) return false;
        }
        return true;
    }
    
    private boolean isValidPassword(String username, String password) {
        try {
            authenticationService.login(username, password);
            return true;
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
    
    private String sendRedirect(HttpServletRequest request, String redirectUrl, ChangeLoginError loginError) {
        String safeRedirectUrl = ServletUtil.createSafeRedirectUrl(request, redirectUrl);
        if (loginError != null) {
            String errorParam = new StringBuilder()
                .append("?")
                .append(LOGIN_ERROR_PARAM)
                .append("=")
                .append(loginError.name())
                .toString();
            
            safeRedirectUrl += errorParam;
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

}

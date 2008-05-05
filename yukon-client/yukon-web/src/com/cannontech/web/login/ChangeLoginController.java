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
@RequestMapping("/changelogin")
public class ChangeLoginController {
    public static final String LOGIN_ERROR_PARAM = "loginError";
    private YukonUserDao yukonUserDao; 
    private AuthenticationService authenticationService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String view(String redirectUrl, HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser user = yukonUserContext.getYukonUser();
        map.addAttribute("user", user);
        
        boolean disablePasswordChange = !authenticationService.supportsPasswordChange(user.getAuthType());
        map.addAttribute("disablePasswordChange", disablePasswordChange);
        
        map.addAttribute("redirectUrl", redirectUrl);
        
        return "changeLogin.jsp";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String update(int userId, String username, String oldPassword, 
            String newPassword, String confirm, String redirectUrl, 
            HttpServletRequest request, ModelMap map) throws Exception {
        
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        LiteYukonUser loggedInUser = yukonUserContext.getYukonUser();
        if (loggedInUser.getUserID() != userId) {
            throw new NotAuthorizedException("Not authorized for userid: " + userId);
        }
        
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        AuthType type = user.getAuthType();
        
        final boolean supportsPasswordChange = authenticationService.supportsPasswordChange(type);
        final boolean hasRequiredFields = hasRequiredFields(username, oldPassword);
        final boolean isUsernameChange = isUsernameChange(user, username);
        final boolean isPasswordChange = hasRequiredFields(newPassword, confirm);
        
        if (!hasRequiredFields) {
            return sendRedirect(request, redirectUrl, LoginError.REQUIRED_FIELDS_MISSING);
        }
        
        // Check for valid credentials before making any changes 
        boolean success = isValidPassword(user.getUsername(), oldPassword);
        if (!success) return sendRedirect(request, redirectUrl, LoginError.INVALID_CREDENTIALS);
        
        if (isUsernameChange) {
            try {
                saveUsernameChange(request, user, username);
            } catch (NotAuthorizedException e) {
                return sendRedirect(request, redirectUrl, LoginError.USER_EXISTS);
            }
            
            if (!isPasswordChange) return sendRedirect(request, redirectUrl, LoginError.NONE);
        }
        
        if (isPasswordChange) {
            LoginError loginError = null;
            if (!supportsPasswordChange) loginError = LoginError.PASSWORD_CHANGE_NOT_SUPPORTED;
            if (!newPassword.equals(confirm)) loginError = LoginError.NO_PASSWORDMATCH;

            if (loginError != null) return sendRedirect(request, redirectUrl, loginError);

            authenticationService.changePassword(user, oldPassword, newPassword);
            return sendRedirect(request, redirectUrl, LoginError.NONE);
        }

        return sendRedirect(request, redirectUrl, LoginError.NO_CHANGE);
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

    private boolean isUsernameChange(LiteYukonUser user, String newUsername) {
        String oldUsername = user.getUsername();
        boolean result = !oldUsername.equals(newUsername);
        return result;
    }
    
    private void saveUsernameChange(HttpServletRequest request, LiteYukonUser user, String username) 
        throws NotAuthorizedException {
        
        user.setUsername(username);
        yukonUserDao.changeUsername(user.getUserID(), username);

        HttpSession session = request.getSession(false);
        session.setAttribute(LoginController.YUKON_USER, user);
    }
    
    private String sendRedirect(HttpServletRequest request, String redirectUrl, LoginError loginError) {
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

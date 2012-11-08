package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.web.stars.service.PasswordResetService;

/**
 * Interceptor used for authenticating remote requests.  Will not create a session.
 */
public class RemoteRequestInterceptor extends HandlerInterceptorAdapter {
    
    private Logger log = YukonLogManager.getLogger(RemoteRequestInterceptor.class);

	private AuthenticationService authenticationService;
	@Autowired private PasswordResetService passwordResetService;
	private UserChecker userChecker;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		String username = ServletRequestUtils.getStringParameter(request, LoginController.USERNAME);
        String password = ServletRequestUtils.getStringParameter(request, LoginController.PASSWORD);
        
        if (username == null || password == null) return false;
		
		// Try to login using the username and password - if no exception is thrown, this is a valid user, proceed
        LiteYukonUser yukonUser = null;
        try {
            yukonUser = authenticationService.login(username, password);
        } catch (PasswordExpiredException e) {
            log.debug("The password for "+username+" is expired.", e);
            String passwordResetUrl = passwordResetService.getPasswordResetUrl(yukonUser.getUsername(), request);
            response.sendRedirect(passwordResetUrl);
            return false;
        }

		userChecker.verify(yukonUser);
		
		return true;
	}
	
	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	
	public void setUserChecker(UserChecker userChecker) {
		this.userChecker = userChecker;
	}
}
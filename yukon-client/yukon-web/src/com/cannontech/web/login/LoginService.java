package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface LoginService {
	
	public static final String LOGIN_WEB_ACTIVITY_ACTION = com.cannontech.database.data.activity.ActivityLogActions.LOGIN_WEB_ACTIVITY_ACTION;

	/**
	 * This method logs a user into yukon, adding the user login to the session.
	 * 
	 * @throws AuthenticationThrottleException 
	 * @throws PasswordExpiredException
	 * @throws BadAuthenticationException 
	 */
    public void login(HttpServletRequest request, String username, String password) 
    throws AuthenticationThrottleException, PasswordExpiredException, BadAuthenticationException;
    
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
    public void invalidateSession(HttpServletRequest request, String reason);
    
    public void createSession(HttpServletRequest request, LiteYukonUser user);
    
    public void clientLogin(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
    public void outboundVoiceLogin(HttpServletRequest request, HttpServletResponse response) throws Exception;
        
    public LiteYukonUser internalLogin(HttpServletRequest request, HttpSession session, String username, boolean saveCurrentUser);

    public void updateActiveUsers(int userID);

    boolean isUserActive(int userID);

    public void releaseInactiveUserFromActiveUsersCache(int userID);
    
}

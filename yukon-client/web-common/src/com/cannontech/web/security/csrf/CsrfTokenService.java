package com.cannontech.web.security.csrf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface CsrfTokenService {
    /**
     * Gets the CSRF token from the session. If no token exists, this will create and insert the token in the session
     */
    String getTokenForSession(HttpSession session);

    String getTokenFromRequest(HttpServletRequest request);

    /**
     * Checks the request and verifies it contains a valid CSRF token
     * Note: ignores any ajax or GET requests
     * 
     * Throws a SecurityException if the valid token is not found.
     */
    void validateToken(HttpServletRequest request);
}

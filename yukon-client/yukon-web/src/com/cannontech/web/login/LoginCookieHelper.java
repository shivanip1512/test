package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginCookieHelper {

    void setRememberMeCookie(HttpServletRequest request, HttpServletResponse response,
                                      String username, String password);

    /**
     * @return username/password for remember me cookie. If cookie is not valid, null is returned.
     */
    UserPasswordHolder readRememberMeCookie(HttpServletRequest request);
    
}

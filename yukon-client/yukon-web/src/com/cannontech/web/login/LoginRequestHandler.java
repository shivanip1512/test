package com.cannontech.web.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginRequestHandler {

    /**
     * The LoginRequestHandler is responsible for determining a successful login based on 
     * a set of credentials found in the request.  If the LoginRequestHandler determines 
     * that the request is valid, the handler is responsible initializing the session.
     * @see com.cannontech.web.login.LoginService.createSession()
     * @param request
     * @param response
     * @return true - if the request contains information for a successful login.
     * @throws IOException
     */
    public boolean handleLoginRequest(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException;
    
}

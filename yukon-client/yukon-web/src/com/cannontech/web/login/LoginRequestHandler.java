package com.cannontech.web.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public interface LoginRequestHandler {

    /**
     * The LoginRequestHandler is responsible for determining a successful login based on 
     * a set of credentials found in the request.
     * @param request
     * @return true - if the request contains information for a successful login.
     * @throws IOException
     */
    public boolean handleLoginRequest(HttpServletRequest request) throws IOException, ServletException;
    
}

package com.cannontech.web.login.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.constants.LoginController;
import com.cannontech.web.login.AbstractLoginRequestHandler;

public class DefaultParamLoginRequestHandler extends AbstractLoginRequestHandler {
    
    @Override
    public boolean handleLoginRequest(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException {
        
        String username = ServletRequestUtils.getStringParameter(request, LoginController.USERNAME);
        String password = ServletRequestUtils.getStringParameter(request, LoginController.PASSWORD);
        
        if (username == null || password == null) return false;
        
        boolean success = loginService.login(request, username, password);
        return success;
    }

}

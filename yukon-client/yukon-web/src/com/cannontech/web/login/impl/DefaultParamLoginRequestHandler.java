package com.cannontech.web.login.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.constants.LoginController;
import com.cannontech.web.login.LoginRequestHandler;
import com.cannontech.web.login.LoginService;

public class DefaultParamLoginRequestHandler implements LoginRequestHandler {
    private LoginService loginService;
    
    @Override
    public boolean handleLoginRequest(final HttpServletRequest request) throws IOException, ServletException {
        String username = ServletRequestUtils.getRequiredStringParameter(request, LoginController.USERNAME);
        String password = ServletRequestUtils.getRequiredStringParameter(request, LoginController.PASSWORD);
        
        boolean success = loginService.login(request, username, password);
        return success;
    }
    
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

}

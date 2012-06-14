package com.cannontech.web.login.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.web.login.AbstractLoginRequestHandler;

public class DefaultParamLoginRequestHandler extends AbstractLoginRequestHandler {
    
    private Logger log = YukonLogManager.getLogger(DefaultParamLoginRequestHandler.class);
    
    @Override
    public boolean handleLoginRequest(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException {
        
        String username = ServletRequestUtils.getStringParameter(request, LoginController.USERNAME);
        String password = ServletRequestUtils.getStringParameter(request, LoginController.PASSWORD);
        
        if (username == null || password == null) return false;
        
        try {
            loginService.login(request, username, password);
            
            log.info("Proceeding with request after successful Param login");
            return true;
        } catch (AuthenticationThrottleException e) {
            log.error("AuthenticationThrottleException: " + e.getThrottleSeconds(), e);
        } catch (BadAuthenticationException e) {
            log.error(e);
        } catch (PasswordExpiredException e) {
            log.debug("The password for "+username+" is expired.");
        }
        return false;
    }
}
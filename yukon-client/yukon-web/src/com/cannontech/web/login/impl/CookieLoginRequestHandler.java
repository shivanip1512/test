package com.cannontech.web.login.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.LoginController;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.login.AbstractLoginRequestHandler;
import com.cannontech.web.login.LoginCookieHelper;
import com.cannontech.web.login.UserPasswordHolder;

public class CookieLoginRequestHandler extends AbstractLoginRequestHandler {
    private LoginCookieHelper loginCookieHelper;
    private Logger log = YukonLogManager.getLogger(CookieLoginRequestHandler.class);

    @Override
    public boolean handleLoginRequest(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {

        Cookie rememberMeCookie = ServletUtil.getCookie(request, LoginController.REMEMBER_ME_COOKIE);
        if (rememberMeCookie != null) {
            try {
                String encryptedValue = rememberMeCookie.getValue();
                UserPasswordHolder holder = loginCookieHelper.decodeCookieValue(encryptedValue);

                boolean success = loginService.login(request, holder.getUsername(), holder.getPassword());
                if (success) {
                    log.info("Proceeding with request after successful Remember Me login");
                    return true;
                }
                
                log.info("Remember Me login failed");
            } catch (GeneralSecurityException e) {
                log.error("Unable to decrypt cookie value", e);
            }

            //cookie login failed, remove cookie.
            ServletUtil.deleteAllCookies(request, response);
        }

        return false;
    }

    public void setLoginCookieHelper(LoginCookieHelper loginCookieHelper) {
        this.loginCookieHelper = loginCookieHelper;
    }

}

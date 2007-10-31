package com.cannontech.web.login;

import java.security.GeneralSecurityException;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.core.authentication.service.CryptoService;

public class YukonLoginController extends MultiActionController {
    private static final Random rand = new Random();
    private static final String delimiter = ":";
    private LoginService loginService;
    private CryptoService cryptoService;

    public ModelAndView view(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Boolean forceLogin = ServletRequestUtils.getBooleanParameter(request, "force");
        final String redirectedFrom = ServletRequestUtils.getStringParameter(request, LoginController.REDIRECTED_FROM);

        if (forceLogin != null && forceLogin.booleanValue() == true) {
            mav.setViewName("login.jsp");
           return mav;
        }

        final Cookie cookie = getCookie(request, LoginController.REMEMBER_ME_COOKIE);
        if (cookie != null) {
            return this.cookieLogin(request, response, cookie, redirectedFrom);
        }

        mav.setViewName("login.jsp");
        mav.addObject("redirectedfrom", redirectedFrom);
        return mav;
    }

    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String username = ServletRequestUtils.getRequiredStringParameter(request, LoginController.USERNAME);
        final String password = ServletRequestUtils.getRequiredStringParameter(request, LoginController.PASSWORD);
        final String redirectedFrom = ServletRequestUtils.getStringParameter(request, LoginController.REDIRECTED_FROM);
        
        String rememberme = ServletRequestUtils.getStringParameter(request, "rememberme");
        if (rememberme != null) {
            this.createCookie(request, response, username, password);
        }
        
        loginService.login(request, response, username, password, redirectedFrom);
        return null;
    }

    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        loginService.logout(request, response);
        return null;
    }

    public ModelAndView clientLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        loginService.clientLogin(request, response);
        return null;
    }

    public ModelAndView outboundVoiceLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        loginService.outboundVoiceLogin(request, response);
        return null;
    }

    public ModelAndView inboundVoiceLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        loginService.inboundVoiceLogin(request, response);
        return null;
    }

    private ModelAndView cookieLogin(final HttpServletRequest request, final HttpServletResponse response, 
            final Cookie cookie, final String redirectedFrom) throws Exception {
        
        try {
            String cryptValue = cookie.getValue();
            String value = cryptoService.decrypt(cryptValue);

            String[] split = value.split(delimiter);
            if (split.length < 2) {
                return new ModelAndView("login.jsp?force=true");
            }

            String username = split[0];
            String password = split[1];

            loginService.login(request, response, username, password, redirectedFrom);
        } catch (GeneralSecurityException e) {
            CTILogger.warn(e);
            return new ModelAndView("login.jsp?force=true");
        }

        return null;
    }

    private void createCookie(final HttpServletRequest request, final HttpServletResponse response, 
            final String username, final String password) {
        
        try {
            String value = createValue(username, password);
            String cryptValue = cryptoService.encrypt(value);
            
            Cookie cookie = new Cookie(LoginController.REMEMBER_ME_COOKIE, cryptValue);
            cookie.setMaxAge(Integer.MAX_VALUE);
            cookie.setPath("/" + request.getContextPath());
            response.addCookie(cookie);
        } catch (GeneralSecurityException e) {
            CTILogger.warn(e);
        }    
    }
    
    private Cookie getCookie(final HttpServletRequest request, final String cookieName) {
        final Cookie[] cookieArray = request.getCookies();
        if (cookieArray == null) return null;

        for (final Cookie cookie : cookieArray) {
            if (cookie.getName().equals(cookieName)) {
                return cookie;
            }
        }
        return null;
    }
    
    private String createValue(final String username, final String password) {
        final String randomString = Long.toString(Math.abs(rand.nextLong()), 36);
        final StringBuilder sb = new StringBuilder();
        sb.append(username);
        sb.append(delimiter);
        sb.append(password);
        sb.append(delimiter);
        sb.append(randomString);
        return sb.toString();
    }

    public void setCryptoService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

}

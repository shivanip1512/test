package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.constants.LoginController;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.login.access.UrlAccessChecker;

public class YukonLoginController extends MultiActionController {
    private LoginService loginService;
    private AuthDao authDao;
    private LoginCookieHelper loginCookieHelper;
    private UrlAccessChecker urlAccessChecker;
    
    public ModelAndView view(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        mav.setViewName("login.jsp");
        return mav;
    }

    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String username = ServletRequestUtils.getRequiredStringParameter(request, LoginController.USERNAME);
        final String password = ServletRequestUtils.getRequiredStringParameter(request, LoginController.PASSWORD);
        final Boolean createRememberMeCookie = ServletRequestUtils.getBooleanParameter(request, "rememberme", false);
        final String redirectedFrom = ServletRequestUtils.getStringParameter(request, LoginController.REDIRECTED_FROM);

        String redirect;

        boolean success = false;
        long retrySeconds = 0;
        try {
            success = loginService.login(request, username, password);
        } catch (AuthenticationThrottleException e) {
            retrySeconds = e.getThrottleSeconds();
        }
        if (success) {
            LiteYukonUser user = ServletUtil.getYukonUser(request);

            if (createRememberMeCookie) {
                String value = loginCookieHelper.createCookieValue(username, password);
                ServletUtil.createCookie(request, response, LoginController.REMEMBER_ME_COOKIE, value);
            }

            if (redirectedFrom != null && !redirectedFrom.equals("")) {
                redirect = redirectedFrom;
            } else {
                String homeUrl = authDao.getRolePropertyValue(user, WebClientRole.HOME_URL);
                redirect = ServletUtil.createSafeUrl(request, homeUrl);
            }
            
            boolean hasUrlAccess = urlAccessChecker.hasUrlAccess(redirect, user);
            if (!hasUrlAccess) {
                redirect = LoginController.LOGIN_URL + "?" + LoginController.INVALID_URL_ACCESS_PARAM;
            }
        } else {
            ServletUtil.deleteAllCookies(request, response);

            String referer = request.getHeader("Referer");
            referer = (referer != null) ? referer : LoginController.LOGIN_URL;

            redirect = ServletUtil.createSafeRedirectUrl(request, referer);
            redirect = appendParams(redirect, retrySeconds, redirectedFrom);
        }
        return new ModelAndView("redirect:" + redirect);
    }

    // appends params to the redirect url provided
    private String appendParams(String redirect, long retrySeconds, String redirectedFrom) {
        String result = redirect;
        //user sees either invalid login OR retry after xx seconds message
        if (retrySeconds > 0) {
            result = ServletUtil.tweakRequestURL(result, LoginController.AUTH_RETRY_SECONDS_PARAM, Long.toString(retrySeconds));
            result = ServletUtil.tweakRequestURL(result, LoginController.AUTH_FAILED_PARAM, null);
        } else {
            result = ServletUtil.tweakRequestURL(result, LoginController.AUTH_FAILED_PARAM, "true");
            result = ServletUtil.tweakRequestURL(result, LoginController.AUTH_RETRY_SECONDS_PARAM, null);
        }
        if (!StringUtils.isBlank(redirectedFrom)) {
            result = ServletUtil.tweakRequestURL(result, LoginController.REDIRECTED_FROM, redirectedFrom);
        } else {
            result = ServletUtil.tweakRequestURL(result, LoginController.REDIRECTED_FROM, null);
        }
        return result;
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

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }
    
    public void setLoginCookieHelper(LoginCookieHelper loginCookieHelper) {
        this.loginCookieHelper = loginCookieHelper;
    }

    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    public void setUrlAccessChecker(UrlAccessChecker urlAccessChecker) {
        this.urlAccessChecker = urlAccessChecker;
    }
    
}

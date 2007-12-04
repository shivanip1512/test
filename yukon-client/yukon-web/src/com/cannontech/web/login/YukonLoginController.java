package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.constants.LoginController;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.util.ServletUtil;

public class YukonLoginController extends MultiActionController {
    private LoginService loginService;
    private AuthDao authDao;
    private LoginCookieHelper loginCookieHelper;
    
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

        boolean success = loginService.login(request, username, password);
        if (success) {
            if (createRememberMeCookie) {
                String value = loginCookieHelper.createCookieValue(username, password);
                ServletUtil.createCookie(request, response, LoginController.REMEMBER_ME_COOKIE, value);
            }

            if (redirectedFrom != null && !redirectedFrom.equals("")) {
                redirect = redirectedFrom;
            } else {
                LiteYukonUser user = ServletUtil.getYukonUser(request);
                String homeUrl = authDao.getRolePropertyValue(user, WebClientRole.HOME_URL);
                redirect = ServletUtil.createSafeUrl(request, homeUrl);
            }
        } else {
            ServletUtil.deleteAllCookies(request, response);
            redirect = ServletUtil.createSafeUrl(request, LoginController.INVALID_URI);
        }
        return new ModelAndView("redirect:" + redirect);
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

}

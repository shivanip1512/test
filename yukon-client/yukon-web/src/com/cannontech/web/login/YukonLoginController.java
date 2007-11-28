package com.cannontech.web.login;

import java.net.URLDecoder;

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

    public ModelAndView view(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String redirectedFrom = ServletRequestUtils.getStringParameter(request, LoginController.REDIRECTED_FROM);
        final ModelAndView mav = new ModelAndView();
        mav.setViewName("login.jsp");
        mav.addObject("redirectedfrom", redirectedFrom);
        return mav;
    }

    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String username = ServletRequestUtils.getRequiredStringParameter(request, LoginController.USERNAME);
        final String password = ServletRequestUtils.getRequiredStringParameter(request, LoginController.PASSWORD);
        final Boolean createRememberMeCookie = ServletRequestUtils.getBooleanParameter(request, "rememberme", false);
        final String redirectedFrom = ServletRequestUtils.getStringParameter(request, LoginController.REDIRECTED_FROM);

        boolean success = loginService.login(request, response, username, password, createRememberMeCookie);
        if (!success) return null;
      
        String redirect;
        if (redirectedFrom != null && !redirectedFrom.equals("")) {
            redirect = URLDecoder.decode(redirectedFrom, "UTF-8"); 
        } else {
            LiteYukonUser user = ServletUtil.getYukonUser(request);
            String homeUrl = authDao.getRolePropertyValue(user, WebClientRole.HOME_URL);
            redirect = ServletUtil.createSafeUrl(request, homeUrl);
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
    
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

}

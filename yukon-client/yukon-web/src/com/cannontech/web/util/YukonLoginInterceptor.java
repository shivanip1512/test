package com.cannontech.web.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cannontech.common.constants.LoginController;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public class YukonLoginInterceptor extends  HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            LiteYukonUser yukonUser = ServletUtil.getYukonUser(session);
            if (yukonUser != null) {
                // we have a valid login
                return true;
            }
        }
        

        
        // do a redirect (copied from CheckLoginTag)
        String redirectURL = "/login.jsp";
        
        // I wish I know what the following was all about!
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {       
            for(int i = 0; i < cookies.length; i++) {
                Cookie c = cookies[i];
                if(c.getName().equalsIgnoreCase(LoginController.LOGIN_URL_COOKIE)) {
                    redirectURL = c.getValue();
                    break;
                }
            }
        }

        response.sendRedirect(redirectURL);
        return false;
    }

}

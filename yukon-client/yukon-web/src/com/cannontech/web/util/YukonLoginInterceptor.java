package com.cannontech.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cannontech.common.constants.LoginController;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public class YukonLoginInterceptor extends  HandlerInterceptorAdapter {
    private static final String paramName = LoginController.REDIRECTED_FROM;
    private static final String loginURL = "/login.jsp";
    
    @Override
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
        
        String url = request.getRequestURL().toString();
        String urlParams = request.getQueryString();
        String navUrl = url + ((urlParams != null) ? "?" + urlParams : "");
        
        String redirectURL = loginURL + "?" + paramName + "=" + navUrl;
        response.sendRedirect(redirectURL);
        return false;
    }

}

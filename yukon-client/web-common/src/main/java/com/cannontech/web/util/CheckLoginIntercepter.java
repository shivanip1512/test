package com.cannontech.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public class CheckLoginIntercepter extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader("Cookies");
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request.getSession());
        if (yukonUser == null) {
            throw new RuntimeException("Not logged in");
        } else {
            return true;
        }
    }

}

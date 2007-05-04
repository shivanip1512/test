package com.cannontech.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cannontech.web.taglib.CheckLoginTag;

public class YukonLoginInterceptor extends  HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        return CheckLoginTag.checkLogin(request, response);
    }

}

package com.cannontech.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class WebSecurityInterceptor extends HandlerInterceptorAdapter {
    private WebSecurityAnnotationProcessor annotationProcessor;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
            HttpServletResponse response, Object handler) throws Exception {

        annotationProcessor.process(handler);
        return true;
    }

    @Autowired
    public void setAnnotationProcessor(
            WebSecurityAnnotationProcessor annotationProcessor) {
        this.annotationProcessor = annotationProcessor;
    }
    
}

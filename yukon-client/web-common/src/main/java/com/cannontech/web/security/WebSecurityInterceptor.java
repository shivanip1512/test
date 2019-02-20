package com.cannontech.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.IgnoreCsrfCheck;
import com.cannontech.web.security.csrf.CsrfTokenService;

public class WebSecurityInterceptor extends HandlerInterceptorAdapter {
    private static final String INVALID_CSRF_TOKEN = "invalidCsrfToken";
    private static final Logger log = YukonLogManager.getLogger(WebSecurityInterceptor.class);

    private WebSecurityAnnotationProcessor annotationProcessor;

    @Autowired private CsrfTokenService csrfTokenService;
    @Autowired private ConfigurationSource configSource;

    @Override
    public boolean preHandle(HttpServletRequest request, 
            HttpServletResponse response, Object handler) throws Exception {
        boolean ignoreCsrf = false;
        boolean isDevelopmentMode = configSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE, false);
        
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            annotationProcessor.processClass(getClass(method.getBean()));
            annotationProcessor.processMethod(method.getMethod());
            ignoreCsrf = AnnotationUtils.findAnnotation(method.getMethod(), IgnoreCsrfCheck.class) != null;
        } else {
            annotationProcessor.processClass(getClass(handler));
            ignoreCsrf = AnnotationUtils.findAnnotation(getClass(handler), IgnoreCsrfCheck.class) != null;
        }

        if (request.getRequestURI().contains("/soap") || request.getRequestURI().contains("/multispeak")
            || (isDevelopmentMode && request.getRequestURI().contains("/updater/update"))) {
            ignoreCsrf = true;
        }
        if (!ignoreCsrf) {
            try {
                csrfTokenService.validateToken(request);
            } catch (SecurityException se) {
                log.error("Invalid CSRF token :", se);
                String redirect =
                    ServletUtil.createSafeRedirectUrl(request, "/login.jsp?" + INVALID_CSRF_TOKEN + "=true");
                response.sendRedirect(redirect);
                HttpSession session = request.getSession(false);
                session.invalidate();
                return false;
            }
        }

        return true;
    }

    @Autowired
    public void setAnnotationProcessor(
            WebSecurityAnnotationProcessor annotationProcessor) {
        this.annotationProcessor = annotationProcessor;
    }

    private Class<?> getClass(Object bean) {
        if (isProxy(bean)) {
            return AopUtils.getTargetClass(bean);
        }
        return bean.getClass();
    }

    private boolean isProxy(Object bean) {
        return AopUtils.isAopProxy(bean)
                || AopUtils.isCglibProxy(bean)
                ||  AopUtils.isJdkDynamicProxy(bean);
    }

}

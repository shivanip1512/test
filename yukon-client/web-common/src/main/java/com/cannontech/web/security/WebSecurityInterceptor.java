package com.cannontech.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.IgnoreCsrfCheck;
import com.cannontech.web.security.csrf.CsrfTokenService;
import com.cannontech.web.widget.support.WidgetMultiActionController;

public class WebSecurityInterceptor extends HandlerInterceptorAdapter {
    private static final String INVALID_CSRF_TOKEN = "invalidCsrfToken";

    private WebSecurityAnnotationProcessor annotationProcessor;

    @Autowired private CsrfTokenService csrfTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, 
            HttpServletResponse response, Object handler) throws Exception {
        boolean ignoreCsrf = false;
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            annotationProcessor.processClass(getClass(method.getBean()));
            annotationProcessor.processMethod(method.getMethod());
            ignoreCsrf = AnnotationUtils.findAnnotation(method.getMethod(), IgnoreCsrfCheck.class) != null;
        } else {
            annotationProcessor.processClass(getClass(handler));
            ignoreCsrf = AnnotationUtils.findAnnotation(getClass(handler), IgnoreCsrfCheck.class) != null;
        }

        if(request.getRequestURI().contains("/soap")) {
            ignoreCsrf = true;
        }
        if (!ignoreCsrf) {
            try {
                csrfTokenService.validateToken(request);
            } catch (SecurityException se) {
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

        if (bean instanceof WidgetMultiActionController) {
            WidgetMultiActionController controller = (WidgetMultiActionController) bean;
            Object widgetController = controller.getWidgetController();
            return widgetController.getClass();
        }

        return bean.getClass();
    }

    private boolean isProxy(Object bean) {
        return AopUtils.isAopProxy(bean)
                || AopUtils.isCglibProxy(bean)
                ||  AopUtils.isJdkDynamicProxy(bean);
    }

}

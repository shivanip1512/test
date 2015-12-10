package com.cannontech.web.widget.support;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cannontech.web.taglib.MessageScopeHelper;
import com.cannontech.web.taglib.MessageScopeHelper.ScopeHolder;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WidgetInterceptor extends HandlerInterceptorAdapter {

    private ObjectMapper jsonObjectMapper = new ObjectMapper();

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // we want to use the request parameters if widgetParameters is not set
        // widgetParameters will be set whenever we come from the widget.tag, but
        // will not be set when doing a refresh or some other Ajax action
        Map<String, String> widgetParameters = (Map<String, String>) request.getAttribute("widgetParameters");
        if (widgetParameters == null) {
            Map<String,String> reqParams = new HashMap<String, String>();
            Enumeration parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String name = (String) parameterNames.nextElement();
                String value = StringEscapeUtils.escapeXml(request.getParameter(name));
                reqParams.put(name, value);
            }

            request.setAttribute("widgetParameters", reqParams);
        }
        return true;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Map<String, String> existingParams = (Map<String, String>) request.getAttribute("widgetParameters");

        response.addHeader("X-JSON", jsonObjectMapper.writeValueAsString(existingParams));

        ScopeHolder scope = createScope(handler, existingParams);
        MessageScopeHelper.forRequest(request).pushScope(scope);
    }

    public ScopeHolder createScope(Object handler, Map<String, String> existingParams) {
        Object target;
        if(handler instanceof WidgetMultiActionController) {
            target = ((WidgetMultiActionController) handler).getWidgetController();
        } else {
            target = handler;
        }
        
        String className = ((HandlerMethod)target).getBean().getClass().getSimpleName();
        String beanName = existingParams.get("shortName");
        ScopeHolder scope = MessageScopeHelper.MessageScope.createScope(beanName, "widgets." + beanName, "widgetClasses." + className);
        return scope;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        Map<String, String> existingParams = (Map<String, String>) request.getAttribute("widgetParameters");
        ScopeHolder scope = createScope(handler, existingParams);
        MessageScopeHelper.forRequest(request).popMatchingScope(scope);
        
    }

}
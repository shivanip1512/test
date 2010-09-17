package com.cannontech.web.widget.support;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class WidgetInterceptor extends HandlerInterceptorAdapter {

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
                String value = request.getParameter(name);
                reqParams.put(name, value);
            }

            request.setAttribute("widgetParameters", reqParams);
        }
        return true;
    }
    
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Map<String, String> existingParams = (Map<String, String>) request.getAttribute("widgetParameters");
        JSONObject object = new JSONObject(existingParams);
        response.addHeader("X-JSON", object.toString());
    }

}

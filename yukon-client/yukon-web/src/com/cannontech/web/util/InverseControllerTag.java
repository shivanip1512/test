package com.cannontech.web.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class InverseControllerTag extends SimpleTagSupport {
    private Controller controller;
    
    @SuppressWarnings("unchecked")
    @Override
    public void doTag() throws JspException, IOException {
        PageContext context = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        HttpServletResponse response = (HttpServletResponse) context.getResponse();
        
        try {
            ModelAndView view = controller.handleRequest(request, response);
            if (view != null) {
                exposeModelAsRequestAttributes(view.getModel(), request);
            }
        } catch (Exception e) {
            throw new JspException("Exception encountered while executing controller",e);
        }
        
    }
    
    public void setControllerName(String controller) {
        try {
            PageContext context = (PageContext) getJspContext();
            WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(context.getServletContext());
            this.controller = (Controller) applicationContext.getBean(controller, Controller.class);
        } catch (Exception e) {
            throw new RuntimeException("Unknown controller class", e);
        }
    }
    
    /**
     * Expose the model objects in the given map as request attributes.
     * Names will be taken from the model Map.
     * This method is suitable for all resources reachable by {@link javax.servlet.RequestDispatcher}.
     * 
     * Copied from org.springframework.web.servlet.view.AbstractView 
     * @param model Map of model objects to expose
     * @param request current HTTP request
     */
    protected <K,V> void exposeModelAsRequestAttributes(Map<K,V> model, HttpServletRequest request) throws Exception {
        Iterator<Map.Entry<K,V>> it = model.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K,V> entry = it.next();
            if (!(entry.getKey() instanceof String)) {
                throw new IllegalArgumentException(
                        "Invalid key [" + entry.getKey() + "] in model Map: only Strings allowed as model keys");
            }
            String modelName = (String) entry.getKey();
            Object modelValue = entry.getValue();
            if (modelValue != null) {
                request.setAttribute(modelName, modelValue);
            }
            else {
                request.removeAttribute(modelName);
            }
        }
    }


}

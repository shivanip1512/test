package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.util.ServletUtil;

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
                ServletUtil.exposeModelAsRequestAttributes(view.getModel(), request);
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


}

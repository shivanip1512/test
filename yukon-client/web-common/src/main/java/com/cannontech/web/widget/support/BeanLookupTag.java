package com.cannontech.web.widget.support;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class BeanLookupTag extends SimpleTagSupport {
    private String variableName;
    private Object bean;

    @Override
    public void doTag() throws JspException, IOException {
        super.doTag();
        getJspContext().setAttribute(variableName, bean);
    }
    
    public void setBean(String beanName) {
        try {
            PageContext context = (PageContext) getJspContext();
            WebApplicationContext applicationContext = 
                WebApplicationContextUtils.getRequiredWebApplicationContext(context.getServletContext());
            bean = applicationContext.getBean(beanName);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load bean: " + beanName, e);
        }
    }
    
    public void setVar(String variableName) {
        this.variableName = variableName;
    }

}

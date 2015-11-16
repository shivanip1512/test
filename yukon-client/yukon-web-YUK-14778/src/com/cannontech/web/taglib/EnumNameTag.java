package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class EnumNameTag extends SimpleTagSupport {
    private Object value;
    private String var;
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        Object output = value;
        if (value instanceof Enum) {
            Enum<?> enumValue = (Enum<?>) value;
            output = enumValue.name();
        }

        if (var != null) {
            getJspContext().setAttribute(var, output);
        } else {
            getJspContext().getOut().print(output);
        }
    }

}
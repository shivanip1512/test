package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class UniqueIdentifierTag extends SimpleTagSupport {
    private String var = null;
    private String prefix = "";
    
    @Override
    public void doTag() throws JspException, IOException {
        JspContext jspContext = getJspContext();
        String key = UniqueIdentifierTag.class.getName() + ".last";
        AtomicInteger last = (AtomicInteger) jspContext.getAttribute(key, PageContext.REQUEST_SCOPE);
        if (last == null) {
            last = new AtomicInteger(0);
            jspContext.setAttribute(key, last, PageContext.REQUEST_SCOPE);
        }
        int current = last.incrementAndGet();
        String output = prefix + current;
        
        if (var == null) {
            jspContext.getOut().print(output);
        } else {
            jspContext.setAttribute(var, output);
        }
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}

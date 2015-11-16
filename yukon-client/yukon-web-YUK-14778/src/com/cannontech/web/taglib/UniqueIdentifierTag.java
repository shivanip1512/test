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
    private static final int SCOPE = PageContext.SESSION_SCOPE;
    
    @Override
    public void doTag() throws JspException, IOException {
        JspContext jspContext = getJspContext();
        String output = generateIdentifier(jspContext, prefix);
        
        if (var == null) {
            jspContext.getOut().print(output);
        } else {
            jspContext.setAttribute(var, output);
        }
    }

    public static String generateIdentifier(JspContext context, String prefix) {
        String key = UniqueIdentifierTag.class.getName() + ".last";
        AtomicInteger last = (AtomicInteger) context.getAttribute(key, SCOPE);
        if (last == null) {
            last = new AtomicInteger(0);
            context.setAttribute(key, last, SCOPE);
        }
        int current = last.incrementAndGet();
        String output = prefix + current;
        return output;
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

package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.web.security.csrf.CsrfTokenService;

@Configurable(value="csrfTokenTagPrototype", autowire=Autowire.BY_NAME)
public class CsrfTokenTag extends YukonTagSupport {
    
    @Autowired private CsrfTokenService csrfTokenService;
    
    private String var;

    @Override
    public void doTag() throws JspException, IOException {
        HttpServletRequest request = getRequest();

        String token = csrfTokenService.getTokenForSession(request.getSession());

        JspWriter out = getJspContext().getOut();
        if (var == null) {
            out.print("<input type=\"hidden\" id=\"ajax-csrf-token\" name=\""+CsrfTokenService.REQUEST_CSRF_TOKEN+"\" value=\""+token+"\">");
        } else {
            getJspContext().setAttribute(var, token);
        }
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
}

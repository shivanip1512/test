package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.util.TagUtils;

import com.cannontech.core.roleproperties.enums.CsrfTokenMode;
import com.cannontech.web.security.csrf.CsrfTokenService;

@Configurable("getCsrfTokenTagPrototype")
public class GetCsrfTokenTag extends YukonTagSupport {
    private CsrfTokenService tokenService;
    
    @Override
    public void doTag() throws JspException, IOException {
        HttpServletRequest request = getRequest();
        String token = tokenService.getToken(request);
        CsrfTokenMode mode = tokenService.getTokenMode(request);
        
        getJspContext().setAttribute("token", token, TagUtils.getScope(TagUtils.SCOPE_PAGE));
        getJspContext().setAttribute("mode", mode, TagUtils.getScope(TagUtils.SCOPE_PAGE));
    }
    
    public void setTokenService(CsrfTokenService tokenService) {
        this.tokenService = tokenService;
    }
}

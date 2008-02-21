package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.taglib.YukonTagSupport;

public class YukonUserTag extends YukonTagSupport {
    private String var;
    private boolean isVarSet = false;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (!isVarSet) throw new JspException("var must be set");
        
        LiteYukonUser user = getYukonUser();

        JspContext context = getJspContext();
        context.setAttribute(var, user);
    }
    
    public void setVar(final String var) {
        this.var = var;
        this.isVarSet = true;
    }
    
}

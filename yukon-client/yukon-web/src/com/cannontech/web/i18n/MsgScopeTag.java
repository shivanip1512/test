package com.cannontech.web.i18n;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.web.taglib.MessageScopeHelper;
import com.cannontech.web.taglib.YukonTagSupport;

public class MsgScopeTag extends YukonTagSupport {
    private String paths = null;
    
    @Override
    public void doTag() throws JspException, IOException {
        String[] tokens = StringUtils.splitPreserveAllTokens(paths, ",");
        MessageScopeHelper.forRequest(getRequest()).pushScope(tokens);
        try {
            getJspBody().invoke(null);
        } finally {
            MessageScopeHelper.forRequest(getRequest()).popScope();
        }
        
    }
    
    public void setPaths(String paths) {
        this.paths = paths;
    }
    
}

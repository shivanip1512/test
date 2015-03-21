package com.cannontech.web.taglib;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.util.TagUtils;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;

@Configurable(value="getBooleanConfigParamTagPrototype", autowire=Autowire.BY_NAME)
public class GetBooleanConfigParamTag extends YukonTagSupport {
    
    @Autowired private ConfigurationSource configSource;
    
    private String param;
    private String var;
    private String scope = TagUtils.SCOPE_PAGE;
    
    @Override
    public void doTag() throws IOException {
        
        if (StringUtils.isBlank(param)) {
            throw new IllegalArgumentException("value is required");
        }
        
        MasterConfigBoolean type = MasterConfigBoolean.valueOf(param);
        boolean result = configSource.getBoolean(type);
        
        if (StringUtils.isBlank(var)) {
            getJspContext().getOut().print(result);
        } else {
            getJspContext().setAttribute(var, result, TagUtils.getScope(scope));
        }
    }

    public void setParam(String param) {
        this.param = param;
    }
    
    /**
     * Set PageContext attribute name under which to expose
     * a variable that contains the color.
     * @see #setScope
     * @see javax.servlet.jsp.PageContext#setAttribute
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /**
     * Set the scope to export the variable to.
     * Default is SCOPE_PAGE ("page").
     * @see #setVar
     * @see org.springframework.web.util.TagUtils#SCOPE_PAGE
     * @see javax.servlet.jsp.PageContext#setAttribute
     */
    public void setScope(String scope) {
        this.scope = scope;
    }
}
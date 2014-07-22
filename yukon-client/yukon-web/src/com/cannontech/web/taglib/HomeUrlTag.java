package com.cannontech.web.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.util.ServletUtil;

@Configurable("homeUrlTagPrototype")
public class HomeUrlTag extends YukonTagSupport {
    
    private String var;
    private boolean htmlEscape;
    private int scope;
    
    private static final String REQUEST = "request";
    private static final String SESSION = "session";
    private static final String APPLICATION = "application";
   
    private RolePropertyDao rolePropertyDao;
    
    public HomeUrlTag() {
        init();
    }

    private void init() {
        var = null;
        htmlEscape = false;
        setScope("");        
    }
    

    @Override
    public void doTag() throws JspException, IOException {

        String homeUrl = getRolePropertyDao().getPropertyStringValue(YukonRoleProperty.HOME_URL, getYukonUser());
        // get the safeHomeUrl
        homeUrl = ServletUtil.createSafeUrl(getRequest(), homeUrl);
             
        if (htmlEscape) {
            homeUrl = StringEscapeUtils.escapeHtml4(homeUrl);
        }

        // store or print the output
        if (var != null) {
            getPageContext().setAttribute(var, homeUrl, scope);
        } else {
            try {
                getPageContext().getOut().print(homeUrl);
            } catch (java.io.IOException ex) {
                throw new JspTagException(ex.toString(), ex);
            }
        }
    }
    
    public void setScope(String scope) {
        this.scope = getScope(scope);
    }

    public static int getScope(String scope) {
        int ret = PageContext.PAGE_SCOPE; // default

        if (REQUEST.equalsIgnoreCase(scope)) {
            ret = PageContext.REQUEST_SCOPE;
        } else if (SESSION.equalsIgnoreCase(scope)) {
            ret = PageContext.SESSION_SCOPE;
        } else if (APPLICATION.equalsIgnoreCase(scope)) {
            ret = PageContext.APPLICATION_SCOPE;
        }

        return ret;
    }
    
    public RolePropertyDao getRolePropertyDao() {
        return rolePropertyDao;
    }

    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
}

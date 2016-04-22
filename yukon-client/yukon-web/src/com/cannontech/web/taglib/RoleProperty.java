package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;

import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;

/**
 * Writes out the value of a role property for the current user.
 */
public class RoleProperty extends javax.servlet.jsp.tagext.BodyTagSupport {
    private int propertyid;
    private boolean useId = true;
    private String property;
    private String defaultvalue = "";
    public String format = null;
    public String var = null;

    @Override
    public int doEndTag() throws JspException {
        property = "";
        propertyid = 0;
        defaultvalue = "";
        format = "";
        return EVAL_PAGE;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            LiteYukonUser user = (LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
            if (user != null) {
                int propId;
                if (useId) {
                    propId = propertyid;
                } else {
                    propId = YukonRoleProperty.valueOf(property).getPropertyId();
                }
                String text =
                    YukonSpringHook.getBean(RolePropertyDao.class).getPropertyStringValue(
                        YukonRoleProperty.getForId(propId), user);
                String fmat = getFormat();
                if (fmat != null) {
                    if (fmat.equalsIgnoreCase(ServletUtil.FORMAT_UPPER)) {
                        text = text.toUpperCase();
                    } else if (fmat.equalsIgnoreCase(ServletUtil.FORMAT_LOWER)) {
                        text = text.toLowerCase();
                    } else if (fmat.equalsIgnoreCase(ServletUtil.FORMAT_CAPITAL)) {
                        text = ServletUtil.capitalize(text);
                    } else if (fmat.equalsIgnoreCase(ServletUtil.FORMAT_ALL_CAPITAL)) {
                        text = ServletUtil.capitalizeAll(text);
                    } else if (fmat.equalsIgnoreCase(ServletUtil.FORMAT_ADD_ARTICLE)) {
                        text = ServletUtil.addArticle(text);
                    }
                }
                if (var != null) {
                    pageContext.setAttribute(var, text);
                } else {
                    pageContext.getOut().write(text);
                }
            }
        } catch (java.io.IOException e) {
            throw new JspException(e.getMessage());
        } catch (UserNotInRoleException e) {
            throw new JspException(e.getMessage());
        }

        return SKIP_BODY;
    }

    public int getPropertyid() {
        return propertyid;
    }

    public void setPropertyid(int propertyid) {
        this.propertyid = propertyid;
        useId = true;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDefaultvalue() {
        return defaultvalue;
    }

    public void setDefaultvalue(String string) {
        defaultvalue = string;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
        useId = false;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getVar() {
        return var;
    }
}

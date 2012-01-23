package com.cannontech.web.taglib;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.springframework.web.util.TagUtils;

import com.google.common.collect.Lists;

public class ListTag extends YukonTagSupport {
    protected String var;
    protected String scope = TagUtils.SCOPE_PAGE;
    protected List<Object> items = Lists.newArrayList();

    @Override
    public void doTag() throws JspException, IOException {
        // Process nested tags.
        StringWriter bodyWriter = new StringWriter();
        if (getJspBody() != null) {
            getJspBody().invoke(bodyWriter);
        }

        getJspContext().setAttribute(var, items, TagUtils.getScope(scope));
    }

    public void addItem(Object item) {
        items.add(item);
    }

    /**
     * Set PageContext attribute name under which to expose
     * a variable that contains the resolved message.
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

package com.cannontech.web.taglib;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ListItemTag extends SimpleTagSupport {
    private Object value;
    
    @Override
    public void doTag() throws JspException, IOException {
        JspTag parent = findAncestorWithClass(this, ListTag.class);
        if (parent == null) {
            throw new JspTagException("ListItemTag must be used within a ListTag");
        }
        ListTag parentListTag = (ListTag) parent;

        Object theValue = value;
        if (theValue == null) {
            StringWriter bodyWriter = new StringWriter();
            if (getJspBody() != null) {
                getJspBody().invoke(bodyWriter);
                theValue = bodyWriter.toString().trim();
            } else {
                theValue = "";
            }
        }

        parentListTag.addItem(theValue);
    }

    public void setValue(Object value) {
        this.value = value;
    }
}

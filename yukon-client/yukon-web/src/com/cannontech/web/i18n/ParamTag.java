package com.cannontech.web.i18n;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.taglibs.standard.tag.common.core.ParamParent;

public class ParamTag extends SimpleTagSupport {
    private String name;
    private String value;
    
    @Override
    public void doTag() throws JspException, IOException {
        JspTag t = findAncestorWithClass(this, ParamParent.class);
        if (t == null) {
            throw new JspTagException("ParamTag must be used within a tag that implements ParamParent.class");
        }
        ParamParent parent = (ParamParent) t;

        // take no action for null or empty names
        String theName = this.name;
        String theValue;
        if (value != null) {
            theValue = value;
        } else {
            StringWriter bodyWriter = new StringWriter();
            if (getJspBody() != null) {
                getJspBody().invoke(bodyWriter);
                theValue = bodyWriter.toString().trim();
            } else {
                theValue = "";
            }
        }

        // send the parameter to the appropriate ancestor
        String enc = "UTF-8";
        parent.addParameter(URLEncoder.encode(theName, enc), URLEncoder.encode(theValue, enc));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

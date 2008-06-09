package com.cannontech.web.i18n;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.taglibs.standard.tag.common.core.ParamParent;

public class MapParamTag extends SimpleTagSupport {
    private Map<String, String> value;

    @Override
    public void doTag() throws JspException, IOException {
        JspTag t = findAncestorWithClass(this, ParamParent.class);
        if (t == null) {
            throw new JspTagException("MapParamTag must be used within a tag that implements ParamParent.class");
        }
        ParamParent parent = (ParamParent) t;

        for (Map.Entry<String, String> entry : value.entrySet()) {
            // take no action for null or empty names
            String theName = entry.getKey();
            String theValue = entry.getValue();
            if (StringUtils.isBlank(theName)) {
                continue;
            }

            // send the parameter to the appropriate ancestor
            String enc = "UTF-8";
            parent.addParameter(URLEncoder.encode(theName, enc), URLEncoder.encode(theValue, enc));
        }
    }

    public Map<String, String> getValue() {
        return value;
    }

    public void setValue(Map<String, String> value) {
        this.value = value;
    }
}

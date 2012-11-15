package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class IncludeCssTag extends SimpleTagSupport {
    private String link;

    public void doTag() throws JspException {
        StandardPageTag spTag = StandardPageTag.find(getJspContext());
        if (spTag != null) {
            spTag.addCSSFile(getLink());
        }
        
        return;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    
}

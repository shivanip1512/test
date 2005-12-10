package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class IncludeCssTag extends TagSupport {
    private String link;

    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }
    public int doEndTag() throws JspException {
        StandardPageTag tag = (StandardPageTag) TagSupport.findAncestorWithClass(this, StandardPageTag.class);
        tag.addCSSFile(getLink());
        return EVAL_PAGE;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    
}

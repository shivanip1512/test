package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class StandardMenuTag extends TagSupport {
    private String module;
    public int doEndTag() throws JspException {
        StandardPageTag tag = 
            (StandardPageTag) TagSupport.findAncestorWithClass(this,
                                                               StandardPageTag.class);
        tag.setModule(getModule());
        return EVAL_PAGE;
    }
    public String getModule() {
        return module;
    }
    public void setModule(String module) {
        this.module = module;
    }
}

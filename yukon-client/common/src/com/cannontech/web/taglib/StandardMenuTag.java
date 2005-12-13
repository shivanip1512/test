package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class StandardMenuTag extends TagSupport {
    public int doEndTag() throws JspException {
        StandardPageTag tag = 
            (StandardPageTag) TagSupport.findAncestorWithClass(this,
                                                               StandardPageTag.class);
        tag.setShowMenu(true);
        return EVAL_PAGE;
    }
}

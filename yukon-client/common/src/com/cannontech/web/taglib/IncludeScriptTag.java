package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class IncludeScriptTag extends TagSupport {
    private String link;

    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }
    public int doEndTag() throws JspException {
        StandardPageTag spTag = (StandardPageTag) TagSupport.findAncestorWithClass(this, StandardPageTag.class);
        if (spTag != null) {
            spTag.addScriptFile(getLink());
            return EVAL_PAGE;
        }
        
        OutputHeadContentTag ohcTag = (OutputHeadContentTag) TagSupport.findAncestorWithClass(this, OutputHeadContentTag.class);
        if (ohcTag != null) {
            ohcTag.addScriptFile(getLink());
            return EVAL_PAGE;
        }
        
        throw new JspException("includeScript tag is only supported within standardPage and outputHeadContent tags");
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    
}

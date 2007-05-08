package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class IncludeCssTag extends TagSupport {
    private String link;

    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }
    public int doEndTag() throws JspException {
        // check if in OHCTag first because StandardPageTag will always be found
        OutputHeadContentTag ohcTag = (OutputHeadContentTag) TagSupport.findAncestorWithClass(this, OutputHeadContentTag.class);
        if (ohcTag != null) {
            ohcTag.addCSSFile(getLink());
            return EVAL_PAGE;
        }
        
        StandardPageTag spTag = StandardPageTag.find(pageContext);
        if (spTag != null) {
            spTag.addCSSFile(getLink());
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

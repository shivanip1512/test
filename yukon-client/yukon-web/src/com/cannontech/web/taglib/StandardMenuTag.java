package com.cannontech.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class StandardMenuTag extends TagSupport {
    
    private String menuSelection = null;

    public String getMenuSelection() {
        return menuSelection;
    }

    public void setMenuSelection(String menuSelection) {
        this.menuSelection = menuSelection;
    }

    public int doEndTag() throws JspException {
        StandardPageTag tag = 
            (StandardPageTag) TagSupport.findAncestorWithClass(this,
                                                               StandardPageTag.class);
        tag.setShowMenu(true);
        tag.setMenuSelection(menuSelection);
        
        return EVAL_PAGE;
    }
}

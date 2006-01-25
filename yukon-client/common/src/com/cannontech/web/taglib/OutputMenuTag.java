package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.web.menu.MenuFeatureSet;
import com.cannontech.web.menu.MenuRenderer;
import com.cannontech.web.menu.ModuleBase;
import com.cannontech.web.menu.StandardMenuRenderer;

/**
 * This tag uses one of the MenuRenderer classes to output the menu
 * that was configured with the StandardMenuTag within the StandardPageTag.
 */
public class OutputMenuTag extends TagSupport {
    
    public int doEndTag() throws JspException {
        
        Boolean showMenu = (Boolean) pageContext.getAttribute(StandardPageTag.CTI_SHOW_MENU, 
                                                              PageContext.REQUEST_SCOPE);
        
        ModuleBase moduleBase = (ModuleBase) pageContext.getAttribute(StandardPageTag.CTI_MODULE_BASE, 
                                                                      PageContext.REQUEST_SCOPE);
        
        MenuRenderer menuRenderer = 
            new StandardMenuRenderer((HttpServletRequest) pageContext.getRequest(),
                                     moduleBase);
        String breadCrumbs = 
            (String) pageContext.getAttribute(StandardPageTag.CTI_BREADCRUMBS, 
                                              PageContext.REQUEST_SCOPE);
        menuRenderer.setBreadCrumb(breadCrumbs);
        if (showMenu.booleanValue()) {
            try {
                menuRenderer.renderMenu(pageContext.getOut());
            } catch (IOException e) {
                throw new JspException("Unable to render standard menu", e);
            }
        }
        return EVAL_PAGE;
    }
    
}

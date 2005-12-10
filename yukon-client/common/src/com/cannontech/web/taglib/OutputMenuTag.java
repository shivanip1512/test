package com.cannontech.web.taglib;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import com.cannontech.web.menu.CommonMenuBuilder;
import com.cannontech.web.menu.CommonMenuException;
import com.cannontech.web.menu.MenuBuilder;
import com.cannontech.web.menu.MenuRenderer;
import com.cannontech.web.menu.ModuleMenuBase;
import com.cannontech.web.menu.StandardMenuRenderer;

/**
 * This tag uses one of the MenuRenderer classes to output the menu
 * that was configured with the StandardMenuTag within the StandardPageTag.
 */
public class OutputMenuTag extends TagSupport {
    private boolean debugMode = true;

    public int doEndTag() throws JspException {
        try {
            MenuBuilder menuBuilder = getMenuBuilder();
            
            String moduleName = (String) pageContext.getAttribute(StandardPageTag.CTI_MENU_MODULE, 
                                                         PageContext.REQUEST_SCOPE);
            
            if (moduleName != null) {
                ModuleMenuBase menuBase = menuBuilder.getMenuBase(moduleName);
                MenuRenderer menuRenderer = 
                    new StandardMenuRenderer((HttpServletRequest) pageContext.getRequest(),
                                             menuBase);
                String breadCrumbs = 
                    (String) pageContext.getAttribute(StandardPageTag.CTI_BREADCRUMBS, 
                                                      PageContext.REQUEST_SCOPE);
                menuRenderer.setBreadCrumb(breadCrumbs);
                menuRenderer.renderMenu(pageContext.getOut());
            }
        } catch (Exception e) {
            throw new JspException("Couldn't output Standard Menu", e);
        }
        return EVAL_PAGE;
    }

    private MenuBuilder getMenuBuilder() throws MalformedURLException, CommonMenuException {
        CommonMenuBuilder menuBuilder = 
            (CommonMenuBuilder) pageContext.getAttribute("ctiMenuBuilder",
                                                         PageContext.APPLICATION_SCOPE);
        if (menuBuilder == null || debugMode) {
            URL menuConfigFile = pageContext.getServletContext().getResource("/WEB-INF/menu_structure.xml");
            menuBuilder = new CommonMenuBuilder(menuConfigFile);
            pageContext.setAttribute("ctiMenuBuilder",
                                     menuBuilder,
                                     PageContext.APPLICATION_SCOPE);
        }
        return menuBuilder;
    }

}

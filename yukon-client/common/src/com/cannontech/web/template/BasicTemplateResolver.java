package com.cannontech.web.template;

import javax.servlet.jsp.PageContext;

import com.cannontech.web.menu.ModuleBase;
import com.cannontech.web.taglib.StandardPageTag;

public class BasicTemplateResolver implements TemplateReslover {

    public String getTemplatePage(ModuleBase moduleBase, PageContext pageContext) {
        if (StandardPageTag.SKIN_PURPLE.equals(moduleBase.getSkin())) {
            return "/WEB-INF/layout/purple.jsp";
        } else {
            return "/WEB-INF/layout/standard.jsp";
        }
    }

}

package com.cannontech.web.template;

import javax.servlet.jsp.PageContext;

import com.cannontech.web.menu.ModuleBase;

public class BasicTemplateResolver implements TemplateReslover {

    public String getTemplatePage(ModuleBase moduleBase, PageContext pageContext) {
        return "/WEB-INF/layout/" + moduleBase.getSkin() + ".jsp";
    }

}

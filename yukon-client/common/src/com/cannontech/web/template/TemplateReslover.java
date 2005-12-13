package com.cannontech.web.template;

import javax.servlet.jsp.PageContext;

import com.cannontech.web.menu.ModuleBase;

public interface TemplateReslover {
    public String getTemplatePage(ModuleBase moduleBase, PageContext pageContext);

}

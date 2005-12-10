package com.cannontech.web.template;

import javax.servlet.jsp.PageContext;

public interface TemplateReslover {
    public String getTemplatePage(String skin, PageContext pageContext);

}

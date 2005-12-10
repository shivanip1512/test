package com.cannontech.web.template;

import javax.servlet.jsp.PageContext;

public class BasicTemplateResolver implements TemplateReslover {

    public String getTemplatePage(String skin, PageContext pageContext) {
        return "/WEB-INF/layout/standard.jsp";
    }

}

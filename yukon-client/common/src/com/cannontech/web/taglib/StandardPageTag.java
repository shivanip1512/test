package com.cannontech.web.taglib;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.web.template.BasicTemplateResolver;
import com.cannontech.web.template.TemplateReslover;

/**
 * This is the tag that should wrap all pages created at CTI. It handles
 * CSS includes, the main menu, doctypes, bread crumbs, customization per
 * module, layout customization via role property, and the page title.
 */
public class StandardPageTag extends BodyTagSupport {
    public static final String CTI_MAIN_CONTENT = "ctiMainContent";
    public static final String CTI_CSS_FILES = "ctiCssFiles";
    public static final String CTI_DOCTYPE_LEVEL = "ctiDoctypeLevel";
    public static final String CTI_MENU_MODULE = "ctiMenuModule";
    public static final String CTI_BREADCRUMBS = "ctiBreadCrumbs";
    
    public static final String HTML_TRANSITIONAL = "transitional";
    public static final String HTML_STRICT = "strict";
    public static final String[] ALLOWED_HTML_LEVELS = {HTML_TRANSITIONAL, HTML_STRICT};
    
    private String title = "";
    private String skin = "standard";
    private String htmlLevel = HTML_TRANSITIONAL;
    private List cssFiles = new ArrayList();
    private String menuModule = null;
    private String breadCrumbData;
    
    public void cleanup() {
        title = "";
        skin = "standard";
        htmlLevel = HTML_TRANSITIONAL;
        menuModule = null;
        super.release();
    }
    
    public int doStartTag() throws JspException {
        cssFiles = new ArrayList(4);
        return EVAL_BODY_BUFFERED;
    }
    
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
    
    public int doEndTag() throws JspException {
        try {
            pageContext.setAttribute(CTI_CSS_FILES, cssFiles, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_MAIN_CONTENT, getBodyContent(), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_DOCTYPE_LEVEL, getHtmlLevel(), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_MENU_MODULE, getMenuModule(), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_BREADCRUMBS, getBreadCrumb(), PageContext.REQUEST_SCOPE);
            // use a RoleProperty to find the page to use here
            TemplateReslover resolver = new BasicTemplateResolver();
            String wrapperPage = resolver.getTemplatePage(getSkin(), pageContext);
            // Now use the RequestDispatcher to process the wrapper page (the wrapper page
            // has a tag to include the content in the middle of itself). We don't use
            // pageContext.include() here because that flushes the output which makes error
            // handling very difficult.
            //pageContext.include("/WebConfig/custom/standard.jsp");
            RequestDispatcher requestDispatcher = pageContext.getServletContext().getRequestDispatcher(wrapperPage);
            requestDispatcher.forward(pageContext.getRequest(), pageContext.getResponse());
        } catch (Exception e) {
            throw new JspException("Can't build standard page.", e);
        }
        cleanup();
        return EVAL_PAGE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        pageContext.setAttribute("ctiPageTitle", title, PageContext.REQUEST_SCOPE);
    }
    
    public void addCSSFile(String path) {
        cssFiles.add(path);
    }

    public String getHtmlLevel() {
        return htmlLevel;
    }

    public void setHtmlLevel(String htmlLevel) {
        this.htmlLevel = htmlLevel;
    }

    private Object getMenuModule() {
        return menuModule;
    }
    
    public void setModule(String module) {
        this.menuModule = module;
    }

    public void setBreadCrumbs(String breadCrumbData) {
        this.breadCrumbData = breadCrumbData;
    }
    
    public String getBreadCrumb() {
        return breadCrumbData;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }
    
}

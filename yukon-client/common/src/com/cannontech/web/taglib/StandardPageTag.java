package com.cannontech.web.taglib;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.web.menu.CommonMenuException;
import com.cannontech.web.menu.CommonModuleBuilder;
import com.cannontech.web.menu.ModuleBase;
import com.cannontech.web.menu.ModuleBuilder;
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
    public static final String CTI_MODULE_BASE = "ctiMenuModule";
    public static final String CTI_BREADCRUMBS = "ctiBreadCrumbs";
    
    public static final String HTML_QUIRKS = "quirks";
    public static final String HTML_TRANSITIONAL = "transitional";
    public static final String HTML_STRICT = "strict";
    public static final String[] ALLOWED_HTML_LEVELS = {HTML_QUIRKS, HTML_TRANSITIONAL, HTML_STRICT};
    
    public static final String SKIN_STANDARD = "standard";
    public static final String SKIN_PURPLE = "purple";
    public static final String[] ALLOWED_SKIN_VALUES     = {SKIN_STANDARD, SKIN_PURPLE};
    
    private String title = "";
    private String htmlLevel = HTML_TRANSITIONAL;
    private List cssFiles = new ArrayList();
    private String module = "";
    private String breadCrumbData = "";
    private boolean debugMode = true;
    private boolean showMenu = false;
    
    public void cleanup() {
        title = "";
        htmlLevel = HTML_TRANSITIONAL;
        module = "";
        breadCrumbData = "";
        //super.release();
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
            // get ModuleBase for this page
            ModuleBuilder menuBuilder = getModuleBuilder();
            
            ModuleBase moduleBase = menuBuilder.getModuleBase(getModule());
            
            pageContext.setAttribute(CTI_CSS_FILES, cssFiles, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_MAIN_CONTENT, getBodyContent(), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_DOCTYPE_LEVEL, getHtmlLevel(), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_MODULE_BASE, moduleBase, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_BREADCRUMBS, getBreadCrumb(), PageContext.REQUEST_SCOPE);

            TemplateReslover resolver = new BasicTemplateResolver();
            String wrapperPage = resolver.getTemplatePage(moduleBase, pageContext);
            // Now use the RequestDispatcher to process the wrapper page (the wrapper page
            // has a tag to include the content in the middle of itself). We don't use
            // pageContext.include() here because that flushes the output which makes error
            // handling very difficult.
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

    private String getModule() {
        return module;
    }
    
    public void setModule(String module) {
        this.module = module;
    }

    public void setBreadCrumbs(String breadCrumbData) {
        this.breadCrumbData = breadCrumbData;
    }
    
    public String getBreadCrumb() {
        return breadCrumbData;
    }

    private ModuleBuilder getModuleBuilder() throws MalformedURLException, CommonMenuException {
        CommonModuleBuilder menuBuilder = 
            (CommonModuleBuilder) pageContext.getAttribute("ctiMenuBuilder",
                                                         PageContext.APPLICATION_SCOPE);
        if (menuBuilder == null || debugMode ) {
            URL menuConfigFile = pageContext.getServletContext().getResource("/WEB-INF/module_config.xml");
            menuBuilder = new CommonModuleBuilder(menuConfigFile);
            pageContext.setAttribute("ctiMenuBuilder",
                                     menuBuilder,
                                     PageContext.APPLICATION_SCOPE);
        }
        return menuBuilder;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    
}

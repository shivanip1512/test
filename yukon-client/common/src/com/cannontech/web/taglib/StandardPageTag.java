package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cannontech.common.version.VersionTools;
import com.cannontech.database.data.lite.LiteYukonUser;
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
    public static final String CTI_PAGE_TITLE = "ctiPageTitle";
    public static final String CTI_YUKON_VERSION = "ctiYukonVersion";
    public static final String CTI_MAIN_CONTENT = "ctiMainContent";
    public static final String CTI_CSS_FILES = "ctiCssFiles";
    public static final String CTI_SCRIPT_FILES = "ctiScriptFiles";
    public static final String CTI_DOCTYPE_LEVEL = "ctiDoctypeLevel";
    public static final String CTI_MODULE_BASE = "ctiMenuModule";
    public static final String CTI_MODULE_NAME = "ctiModuleName";
    public static final String CTI_BREADCRUMBS = "ctiBreadCrumbs";
    public static final String CTI_SHOW_MENU = "ctiShowMenu";
    public static final String CTI_MENU_SELECTION = "ctiMenuSelection";
    public static final String CTI_STANDARD_PAGE_INSTANCE = "ctiStandardPageInstance";
    
    public static final String HTML_QUIRKS = "quirks";
    public static final String HTML_TRANSITIONAL = "transitional";
    public static final String HTML_STRICT = "strict";
    public static final String[] ALLOWED_HTML_LEVELS = {HTML_QUIRKS, HTML_TRANSITIONAL, HTML_STRICT};
    
    private String title = "";
    private String htmlLevel = HTML_TRANSITIONAL;
    private List<String> cssFiles;
    private List<String> scriptFiles;
    private String module = "";
    private String breadCrumbData = "";
    private boolean debugMode = false;
    private boolean showMenu = false;
    private String menuSelection = null;
    private boolean skipPage;
    
    public int doStartTag() throws JspException {
        // check if the user is logged in
        skipPage = false;
        if (!CheckLoginTag.checkLogin(pageContext)) {
            // the checkLogin calls sets a redirect
            skipPage = true;
            return SKIP_BODY;
        }
        
        // check if authorized for this page
        ModuleBuilder moduleBuilder = getModuleBuilder();
        
        ModuleBase moduleBase = moduleBuilder.getModuleBase(getModule());
        LiteYukonUser user = 
            (LiteYukonUser) pageContext.getSession().getAttribute("YUKON_USER");
        if (!moduleBase.isUserAuthorized(user)) {
            HttpServletResponse response = 
                (HttpServletResponse) pageContext.getResponse();
            try {
                response.sendRedirect("/home");
                skipPage = true;
                return SKIP_BODY;
            } catch (IOException e) {
                throw new JspException("Not authorized and can't redirect");
            }
        }
        
        cssFiles = new ArrayList<String>();
        scriptFiles = new ArrayList<String>();
        
        pageContext.setAttribute(CTI_STANDARD_PAGE_INSTANCE, this, PageContext.REQUEST_SCOPE);
        
        return EVAL_BODY_BUFFERED;
    }
    
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
    
    public int doEndTag() throws JspException {
        try {
            if (skipPage) {
                return SKIP_PAGE;
            }
            // get ModuleBase for this page
            ModuleBuilder moduleBuilder = getModuleBuilder();
            
            ModuleBase moduleBase = moduleBuilder.getModuleBase(getModule());
            
            pageContext.setAttribute(CTI_PAGE_TITLE, getTitle(), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_YUKON_VERSION, VersionTools.getYUKON_VERSION(), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_MODULE_NAME, getModule(), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_CSS_FILES, cssFiles, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_SCRIPT_FILES, scriptFiles, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_MAIN_CONTENT, getBodyContent(), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_DOCTYPE_LEVEL, getHtmlLevel(), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_MODULE_BASE, moduleBase, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_BREADCRUMBS, getBreadCrumb(), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_SHOW_MENU, new Boolean(isShowMenu()), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(CTI_MENU_SELECTION, menuSelection, PageContext.REQUEST_SCOPE);
            
            TemplateReslover resolver = new BasicTemplateResolver();
            String wrapperPage = resolver.getTemplatePage(moduleBase, pageContext);
            try {
                // Now use the RequestDispatcher to process the wrapper page (the wrapper page
                // has a tag to include the content in the middle of itself). We don't use
                // pageContext.include() here because that flushes the output which makes error
                // handling very difficult.
                RequestDispatcher requestDispatcher = pageContext.getServletContext().getRequestDispatcher(wrapperPage);
                requestDispatcher.forward(pageContext.getRequest(), pageContext.getResponse());
                return EVAL_PAGE;
            } catch (Exception e) {
                throw new JspException("Can't build standard page.", e);
            } 
        }finally {
            cleanup();
        }
    }
    
    public static StandardPageTag find(JspContext context) {
        Object attribute = context.getAttribute(CTI_STANDARD_PAGE_INSTANCE, PageContext.REQUEST_SCOPE);
        return (StandardPageTag) attribute;
    }
    
    private void cleanup() {
        title = "";
        htmlLevel = HTML_TRANSITIONAL;
        module = "";
        breadCrumbData = "";
        showMenu = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public void addCSSFile(String path) {
        cssFiles.add(path);
    }

    public void addScriptFile(String link) {
        scriptFiles.add(link);
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

    private ModuleBuilder getModuleBuilder() throws JspException {
        try {
            final String menuBuilderName = "ctiMenuBuilder";
            CommonModuleBuilder menuBuilder = 
                (CommonModuleBuilder) pageContext.getAttribute(menuBuilderName,
                                                             PageContext.APPLICATION_SCOPE);
            if (menuBuilder == null || debugMode ) {
                ApplicationContext applicationContext = 
                    WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
                menuBuilder = (CommonModuleBuilder) applicationContext.getBean("menuBuilder");
                pageContext.setAttribute(menuBuilderName,
                                         menuBuilder,
                                         PageContext.APPLICATION_SCOPE);
            }
            return menuBuilder;
        } catch (Exception e) {
            throw new JspException(e);
        }
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }
    
    public void setMenuSelection(String menuSelection) {
        this.menuSelection = menuSelection;
    }


    
}

package com.cannontech.web.taglib;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.cannontech.web.taglib.MessageScopeHelper.MessageScope;


/**
 * This is the tag that should wrap all pages created for Cannon. It handles
 * CSS includes, the main menu, doctypes, bread crumbs, customization per
 * module, layout customization via role property, and the page title.
 */
public class StandardPageTag extends BodyTagSupport {
    public static final String STANDARD_PAGE_INFO_ATTR = StandardPageTag.class.getName() + ".stdPageInfo";
    public static final String MAIN_CONTENT_ATTR = StandardPageTag.class.getName() + ".mainContent";
    public static final String STANDARD_PAGE_INSTANCE_ATTR = StandardPageTag.class.getName() + ".standardPageInstance";
    
    private String title = "";
    private HtmlLevel htmlLevel = HtmlLevel.transitional;
    private List<String> cssFiles;
    private List<String> scriptFiles;
    private String module = "";
    private String page = "";
    private String breadCrumbData = null;
    private boolean showMenu = false;
    private String menuSelection = null;
    private boolean skipPage;
    private StandardPageInfo model;
    
    @Override
    public int doStartTag() throws JspException {
        skipPage = false;

        cssFiles = new ArrayList<String>();
        scriptFiles = new ArrayList<String>();
        model = new StandardPageInfo();
        
        model.setTitle(getTitle());
        model.setCssFiles(cssFiles);
        model.setScriptFiles(scriptFiles);
        model.setHtmlLevel(getHtmlLevel());
        model.setModuleName(getModule());
        model.setPageName(getPage());
        
        pageContext.setAttribute(STANDARD_PAGE_INFO_ATTR, model, PageContext.REQUEST_SCOPE);
        
        pageContext.setAttribute(STANDARD_PAGE_INSTANCE_ATTR, this, PageContext.REQUEST_SCOPE);
        
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        
        // push the scope of paths (list - page name split on the period)
        // 
        MessageScope messageScope = MessageScopeHelper.forRequest(request);
        
        if (StringUtils.isNotBlank(model.getPageName())) {
            String baseSearchPath = "modules." + model.getModuleName();
            
            String[] pageNameParts = StringUtils.split(model.getPageName(), ".");
            String[] finalPaths = new String[pageNameParts.length];

            for (int i = 0; i < pageNameParts.length; i++) {
                baseSearchPath = baseSearchPath + "." + pageNameParts[i];
                finalPaths[finalPaths.length - i - 1] = baseSearchPath;
            }
            
            messageScope.pushScope(finalPaths);
        }
        // the above scope won't be popped so that it is available for the layout
        
        return EVAL_BODY_BUFFERED;
    }
    
    @Override
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
    
    @Override
    public int doEndTag() throws JspException {
        try {
            if (skipPage) {
                return SKIP_PAGE;
            }
            
            model.setShowMenu(isShowMenu());
            model.setMenuSelection(menuSelection);
            model.setBreadCrumbs(getBreadCrumb());
            
            // the following could really be a field and replace all the other fields...
            pageContext.setAttribute(MAIN_CONTENT_ATTR, getBodyContent(), PageContext.REQUEST_SCOPE);

            try {
                // set the content type
                pageContext.getResponse().setContentType("text/html;charset=UTF-8");
                // Now use the RequestDispatcher to process the wrapper page (the wrapper page
                // has a tag to include the content in the middle of itself). We don't use
                // pageContext.include() here because that flushes the output which makes error
                // handling very difficult.
                RequestDispatcher requestDispatcher = pageContext.getServletContext().getRequestDispatcher("/spring/layout/");
                
                //forward the contents to the LayoutController
                requestDispatcher.forward(pageContext.getRequest(), pageContext.getResponse());
                return EVAL_PAGE;
            } catch (Exception e) {
                throw new JspException("Can't build standard page.", e);
            } 
        } finally {
            cleanup();
        }
    }
    
    //reference to the instance of this standard page tag
    public static StandardPageTag find(JspContext context) {
        Object attribute = context.getAttribute(STANDARD_PAGE_INSTANCE_ATTR, PageContext.REQUEST_SCOPE);
        return (StandardPageTag) attribute;
    }
    
    public static BodyContent getBodyContent(ServletRequest request) {
        BodyContent result = (BodyContent) request.getAttribute(MAIN_CONTENT_ATTR);
        return result;
    }
    
    public static StandardPageInfo getStandardPageInfo(ServletRequest request) {
        StandardPageInfo result = (StandardPageInfo) request.getAttribute(STANDARD_PAGE_INFO_ATTR);
        return result;
    }
    
    private void cleanup() {
        
        // we have no need for the body anymore
        setBodyContent(null);
        
        title = "";
        htmlLevel = HtmlLevel.transitional;
        module = "";
        breadCrumbData = null;
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
        return htmlLevel.toString();
    }

    public void setHtmlLevel(String htmlLevel) {
        this.htmlLevel = HtmlLevel.valueOf(htmlLevel);
    }

    private String getModule() {
        return module;
    }
    
    public void setModule(String module) {
        this.module = module;
    }

    public String getPage() {
        return page;
    }
    
    public void setPage(String page) {
        this.page = page;
    }
    
    public void setBreadCrumbs(String breadCrumbData) {
        this.breadCrumbData = breadCrumbData;
    }
    
    public String getBreadCrumb() {
        return breadCrumbData;
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

    public List<String> getCssFiles() {
        return cssFiles;
    }

    public List<String> getScriptFiles() {
        return scriptFiles;
    }


    
}

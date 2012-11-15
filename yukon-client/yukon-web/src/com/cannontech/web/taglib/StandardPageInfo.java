package com.cannontech.web.taglib;

import java.util.List;
import java.util.Map;

import com.cannontech.web.PageEditMode;
import com.google.common.collect.Maps;

public class StandardPageInfo {

    private String breadCrumbs;
    private List<String> cssFiles;
    private String htmlLevel;
    private String menuSelection;
    private String moduleName;
    private PageEditMode pageEditMode;
    private List<String> scriptFiles;
    private boolean showMenu;
    private String title;
    private String pageName;
    private String servletPath;
    private Map<String, String> layoutParts = Maps.newHashMap();

    public String getBreadCrumbs() {
        return breadCrumbs;
    }

    public List<String> getCssFiles() {
        return cssFiles;
    }

    public String getHtmlLevel() {
        return htmlLevel;
    }

    public String getMenuSelection() {
        return menuSelection;
    }

    public String getModuleName() {
        return moduleName;
    }

    public PageEditMode getPageEditMode() {
		return pageEditMode;
	}
    
    public List<String> getScriptFiles() {
        return scriptFiles;
    }

    public String getTitle() {
        return title;
    }

    public boolean isShowMenu() {
        return showMenu;
    }
    
    public String getPageName() {
        return pageName;
    }

    public String getServletPath() {
        return servletPath;
    }

    public void setBreadCrumbs(String breadCrumbs) {
        this.breadCrumbs = breadCrumbs;
    }

    public void setCssFiles(List<String> cssFiles) {
        this.cssFiles = cssFiles;
    }

    public void setHtmlLevel(String htmlLevel) {
        this.htmlLevel = htmlLevel;
    }

    public void setMenuSelection(String menuSelection) {
        this.menuSelection = menuSelection;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setPageEditMode(PageEditMode pageEditMode) {
		this.pageEditMode = pageEditMode;
	}
    
    public void setScriptFiles(List<String> scriptFiles) {
        this.scriptFiles = scriptFiles;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }
    
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public Map<String, String> getLayoutParts() {
        return layoutParts;
    }
    
    public void addLayoutPart(String name, String contents) {
        layoutParts.put(name, contents);
    }
}

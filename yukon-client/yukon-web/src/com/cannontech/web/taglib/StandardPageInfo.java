package com.cannontech.web.taglib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.web.PageEditMode;

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
    private Map<String, String> layoutParts = new HashMap<>();

    public String getBreadCrumbs() {
        return breadCrumbs;
    }

    public void setBreadCrumbs(String breadCrumbs) {
        this.breadCrumbs = breadCrumbs;
    }

    public List<String> getCssFiles() {
        return cssFiles;
    }

    public void setCssFiles(List<String> cssFiles) {
        this.cssFiles = cssFiles;
    }

    public String getHtmlLevel() {
        return htmlLevel;
    }

    public void setHtmlLevel(String htmlLevel) {
        this.htmlLevel = htmlLevel;
    }

    public String getMenuSelection() {
        return menuSelection;
    }

    public void setMenuSelection(String menuSelection) {
        this.menuSelection = menuSelection;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public PageEditMode getPageEditMode() {
        return pageEditMode;
    }

    public void setPageEditMode(PageEditMode pageEditMode) {
        this.pageEditMode = pageEditMode;
    }

    public List<String> getScriptFiles() {
        return scriptFiles;
    }

    public void setScriptFiles(List<String> scriptFiles) {
        this.scriptFiles = scriptFiles;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getServletPath() {
        return servletPath;
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

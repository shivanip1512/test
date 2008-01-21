package com.cannontech.web.taglib;

import java.util.List;

public class StandardPageInfo {

    private String breadCrumbs;
    private List<String> cssFiles;
    private String htmlLevel;
    private String menuSelection;
    private String moduleName;
    private List<String> scriptFiles;
    private boolean showMenu;
    private String title;

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

    public List<String> getScriptFiles() {
        return scriptFiles;
    }

    public String getTitle() {
        return title;
    }

    public boolean isShowMenu() {
        return showMenu;
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

    public void setScriptFiles(List<String> scriptFiles) {
        this.scriptFiles = scriptFiles;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

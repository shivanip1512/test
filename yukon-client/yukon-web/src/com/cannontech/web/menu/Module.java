package com.cannontech.web.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * A representation of a module from module_config.xml.
 * 
 * An instance of this class corresponds to a &lt;module&gt; element in module_config.xml.
 */
public class Module {
    private String moduleName;
    private MenuBase portalLinks;
    private Map<String, PageInfo> pageInfos = Maps.newHashMap();
    private List<String> cssFiles = new ArrayList<String>(2);
    private List<String> scriptFiles = new ArrayList<String>(2);
    private LayoutSkinEnum skin;
    private MenuBase menuBase;
    
    public Module(String moduleName) {
        this.moduleName = moduleName;
    }
    
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setPortalLinks(MenuBase portalLinks) {
        this.portalLinks = portalLinks;
    }
    
    public MenuBase getPortalLinks() {
        return portalLinks;
    }
    
    public MenuBase getMenuBase() {
        return menuBase;
    }

    public void setMenuBase(MenuBase menuBase) {
        this.menuBase = menuBase;
    }

    public LayoutSkinEnum getSkin() {
        return skin;
    }

    public void setSkin(LayoutSkinEnum skin) {
        this.skin = skin;
    }

    public PageInfo getPageInfo(String pageName) {
        return pageInfos.get(pageName);
    }

    public void addPageInfo(PageInfo breadCrumb) {
        pageInfos.put(breadCrumb.getName(), breadCrumb);
    }

    public List<String> getCssFiles() {
        return cssFiles;
    }

    public void addCssFiles(String cssFile) {
        cssFiles.add(cssFile);
    }
    
    public List<String> getScriptFiles() {
        return scriptFiles;
    }
    
    public void addScriptFiles(String scriptFile) {
        scriptFiles.add(scriptFile);
    }
}

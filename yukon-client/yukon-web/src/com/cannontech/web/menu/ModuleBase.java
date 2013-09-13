package com.cannontech.web.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cannontech.web.menu.option.producer.SearchProducer;
import com.google.common.collect.Maps;

/**
 * Represents the logical base of the menu for a given module. The main components are
 * the top level options (which usually have sub option), the search path for the search
 * form, and the quicklinks that are used in the drop down. When actually building the
 * menu, the getValid*() methods should be used to ensure that the user only sees links
 * to content they have permission to see.
 */
public class ModuleBase {
	
    private String moduleName;
    private SearchProducer searchProducer;
    private MenuBase portalLinks;
    private Map<String, PageInfo> pageInfos = Maps.newHashMap();
    private List<String> cssFiles = new ArrayList<String>(2);
    private List<String> scriptFiles = new ArrayList<String>(2);
    private LayoutSkinEnum skin;
    private MenuBase menuBase;
    
    public ModuleBase(String moduleName) {
        this.moduleName = moduleName;
    }
    
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public SearchProducer getSearchProducer() {
		return searchProducer;
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

    public void setSearchProducer(SearchProducer searchProducer) {
		this.searchProducer = searchProducer;
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
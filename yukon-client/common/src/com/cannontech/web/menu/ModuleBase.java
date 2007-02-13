package com.cannontech.web.menu;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.iterators.FilterIterator;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.UserChecker;

/**
 * Represents the logical base of the menu for a given module. The main components are
 * the top level options (which usually have sub option), the search path for the search
 * form, and the quicklinks that are used in the drop down. When actually building the
 * menu, the getValid*() methods should be used to ensure that the user only sees links
 * to content they have permission to see.
 */
public class ModuleBase {
    private String moduleName;
    private String searchPath;
    private String searchFieldName;
    private String searchMethod;
    private List quickLinks;
    private List cssFiles = new LinkedList();
    private List scriptFiles = new LinkedList();
    private String skin;
    private MenuBase menuBase;
    private UserChecker authorizationChecker;
    
    public ModuleBase(String moduleName) {
        this.moduleName = moduleName;
    }
    
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getSearchPath() {
        return searchPath;
    }

    public void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }
    
    public List getQuickLinks() {
        return quickLinks;
    }
    
    public void setQuickLinks(List quickLinks) {
        this.quickLinks = quickLinks;
    }
    
    public Iterator getValidQuickLinks(LiteYukonUser user) {
        return new FilterIterator(quickLinks.iterator(), new CheckUserPredicate(user));
    }
    
    public boolean isUserAuthorized(LiteYukonUser user) {
        return authorizationChecker.check(user);
    }

    public MenuBase getMenuBase() {
        return menuBase;
    }

    public void setMenuBase(MenuBase menuBase) {
        this.menuBase = menuBase;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getSearchFieldName() {
        return searchFieldName;
    }

    public void setSearchFieldName(String searchFieldName) {
        this.searchFieldName = searchFieldName;
    }

    public String getSearchMethod() {
        return searchMethod;
    }

    public void setSearchMethod(String searchMethod) {
        this.searchMethod = searchMethod;
    }

    public List getCssFiles() {
        return cssFiles;
    }

    public void addCssFiles(String cssFile) {
        cssFiles.add(cssFile);
    }
    
    public List getScriptFiles() {
        return scriptFiles;
    }
    
    public void addScriptFiles(String scriptFile) {
        scriptFiles.add(scriptFile);
    }

    public void setModuleChecker(UserChecker authorizationChecker) {
        this.authorizationChecker = authorizationChecker;
    }
}

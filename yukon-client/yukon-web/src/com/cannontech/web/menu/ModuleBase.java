package com.cannontech.web.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.FilterIterator;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.web.menu.option.producer.MenuOptionProducer;

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
    private List<MenuOptionProducer> portalLinks;
    private List<String> cssFiles = new ArrayList<String>(2);
    private List<String> scriptFiles = new ArrayList<String>(2);
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
    
    public List<MenuOptionProducer> getPortalLinks() {
        return portalLinks;
    }
    
    public void setPortalLinks(List<MenuOptionProducer> portalLinks) {
        this.portalLinks = portalLinks;
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<MenuOptionProducer> getValidPortalLinks(YukonUserContext userContext) {
        return new FilterIterator(portalLinks.iterator(), new CheckUserPredicate(userContext));
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

    public void setModuleChecker(UserChecker authorizationChecker) {
        this.authorizationChecker = authorizationChecker;
    }
}

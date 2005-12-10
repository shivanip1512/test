package com.cannontech.web.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.FilterIterator;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Represents the logical base of the menu for a given module. The main components are
 * the top level options (which usually have sub option), the search path for the search
 * form, and the quicklinks that are used in the drop down. When actually building the
 * menu, the getValid*() methods should be used to ensure that the user only sees links
 * to content they have permission to see.
 */
public class ModuleMenuBase {
    private List topLevelOptions = new ArrayList();
    private String moduleName;
    private String searchPath;
    private List quickLinks;
    
    public ModuleMenuBase(String moduleName) {
        this.moduleName = moduleName;
    }
    
    public void addTopLevelOption(BaseMenuOption topLevelOption) {
        topLevelOptions.add(topLevelOption);
    }
    
    public List getTopLevelOptions() {
        // check role/property here
        return topLevelOptions;
    }

    public Iterator getValidTopLevelOptions(LiteYukonUser user) {
        return new FilterIterator(topLevelOptions.iterator(), new CheckUserPredicate(user));
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
}

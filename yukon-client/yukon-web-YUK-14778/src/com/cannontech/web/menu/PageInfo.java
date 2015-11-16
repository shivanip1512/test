package com.cannontech.web.menu;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.cannontech.user.checker.UserChecker;
import com.google.common.collect.Lists;

/**
 * A representation of basic configuration information for a web page.
 * 
 * An instance of this class corresponds to a &lt;page&gt; element in module_config.xml.
 */
public class PageInfo implements Comparable<PageInfo> {
    private String name;
    private String moduleName;
    private String linkExpression;
    private List<String> labelArgumentExpressions = Lists.newArrayList();
    private PageInfo parent;
    private PageTypeEnum pageType;
    private boolean renderMenu;
    private String menuSelection;   // <menu> exclusively used (do not use <cti:standardPageMenu> in the jsp) 
    private List<PageInfo> childPages = Collections.emptyList();
    private String detailInfoIncludePath;
    private UserChecker userChecker;
    private boolean navigationMenuRoot; //some node needs to have this set to true in order to have a left-hand menu
    private boolean contributeToMenu;
    private boolean hideSearch; //used on search results pages
    private boolean hideFavorite;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return String      : The link if defined, else null.
     */
    public String getLinkExpression() {
        return linkExpression;
    }
    public void setLinkExpression(String linkExpression) {
        this.linkExpression = linkExpression;
    }
    public List<String> getLabelArgumentExpressions() {
        return labelArgumentExpressions;
    }
    public void addLabelArgumentExpression(String expression) {
        labelArgumentExpressions.add(expression);
    }
    public PageInfo getParent() {
        return parent;
    }
    public void setParent(PageInfo parent) {
        this.parent = parent;
    }

    /**
     * @return String : This page's name preceded by all parent pages/modules' names.
     *                  To be used as the basis for i18n keys.
     */
    public String getFullName() {
        if (getParent() == null) {
            return getName();
        } else {
            return getParent().getFullName() + "." + getName();
        }
    }
    
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    
    public void setPageType(PageTypeEnum pageType) {
        this.pageType = pageType;
    }
    public PageTypeEnum getPageType() {
        return pageType;
    }
    
    public void setRenderMenu(boolean renderMenu) {
        this.renderMenu = renderMenu;
    }
    public boolean isRenderMenu() {
        return renderMenu;
    }
    
    public void setMenuSelection(String menuSelection) {
        this.menuSelection = menuSelection;
    }
    public String getMenuSelection() {
        return menuSelection;
    }
    
    @Override
    public String toString() {
        return getFullName();
    }
    
    @Override
    public int compareTo(PageInfo o) {
        CompareToBuilder builder = new CompareToBuilder();
        builder.append(getFullName(), o.getFullName());
        return builder.toComparison();
    }
    
    public boolean isShowContextualNavigation() {
        // someday I'd like more control over this, but this works for now
        return isNavigationMenuRoot() || (parent != null && parent.isShowContextualNavigation());
    }
    
    public PageInfo findContextualNavigationRoot() {
        if (isNavigationMenuRoot()) {
            return this;
        }
        if (parent != null) {
            return parent.findContextualNavigationRoot();
        }
        return null;
    }
    
    public void setChildPages(List<PageInfo> childPages) {
        this.childPages = childPages;
    }
    
    public List<PageInfo> getChildPages() {
        return childPages;
    }


    public void setDetailInfoIncludePath(String detailInfoIncludePath) {
        this.detailInfoIncludePath = detailInfoIncludePath;
    }

    public String getDetailInfoIncludePath() {
        return detailInfoIncludePath;
    }
    
    public void setUserChecker(UserChecker userChecker) {
            this.userChecker = userChecker;
    }
        
    public UserChecker getUserChecker() {
        return userChecker;
    }
    
    public void setNavigationMenuRoot(boolean navigationMenuRoot) {
        this.navigationMenuRoot = navigationMenuRoot;
    }
    
    public boolean isNavigationMenuRoot() {
        return navigationMenuRoot;
    }
    
    public void setContributeToMenu(boolean contributeToMenu) {
        this.contributeToMenu = contributeToMenu;
    }
    
    public boolean isContributeToMenu() {
        return contributeToMenu;
    }
    
    public void setHideSearch(boolean hideSearch) {
		this.hideSearch = hideSearch;
	}

    public boolean isHideSearch() {
		return hideSearch;
	}
    
    public void setHideFavorite(boolean hideFavorite) {
        this.hideFavorite = hideFavorite;
    }

    public boolean isHideFavorite() {
        return hideFavorite;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((moduleName == null) ? 0
                : moduleName.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PageInfo other = (PageInfo) obj;
        if (moduleName == null) {
            if (other.moduleName != null)
                return false;
        } else if (!moduleName.equals(other.moduleName))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (parent == null) {
            if (other.parent != null)
                return false;
        } else if (!parent.equals(other.parent))
            return false;
        return true;
    }
}

package com.cannontech.web.menu;

import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;

import com.google.common.collect.Lists;

public class PageInfo implements Comparable<PageInfo> {
    private String name;
    private String moduleName;
    private String linkExpression;
    private List<String> labelArgumentExpressions = Lists.newArrayList();
    private PageInfo parent;
    private PageTypeEnum pageType;
    private boolean renderMenu;
    private String menuSelection;
    
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
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
}

package com.cannontech.web.menu;

public enum PageTypeEnum {
    BLANK(true, false),
    BASIC(true, false),
    DETAIL(false, false),
    DETAIL_WITH_NAVIGATION(false, true),
    DETAIL_CHILD(false, false),
    DETAIL_CHILD_WITH_NAVIGATION(false, false),
    LANDING(true, false),
    ;
    
    private final boolean linkable;
    private final boolean navigationRoot;

    private PageTypeEnum(boolean linkable, boolean navigationRoot) {
        this.linkable = linkable;
        this.navigationRoot = navigationRoot;
    }
    
    public boolean isLinkable() {
        return linkable;
    }
    
    public boolean isNavigationRoot() {
        return navigationRoot;
    }
}

package com.cannontech.web.menu;

public enum LayoutSkinEnum {
    STANDARD("standard.jsp", false),
    LEFT_SIDE_MENU("leftSideMenu.jsp", true),   //used in redering the left side nav menus NOT Residential left side menu
    PURPLE("purple.jsp", false),
    INNER("inner.jsp", false),  // gives you headers without chrome - appropriate for iframes
    ;
    
    private final String viewName;
    private final boolean leftSideMenu;

    private LayoutSkinEnum(String viewName, boolean leftSideMenu) {
        this.viewName = viewName;
        this.leftSideMenu = leftSideMenu;
    }
    
    public String getViewName() {
        return viewName;
    }
    
    public boolean isLeftSideMenu() {
        return leftSideMenu;
    }
    
}

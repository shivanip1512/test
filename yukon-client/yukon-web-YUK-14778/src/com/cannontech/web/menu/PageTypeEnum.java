package com.cannontech.web.menu;

public enum PageTypeEnum {
    BLANK(true),
    BASIC(true),
    DETAIL(false),
    DETAIL_CHILD(false),
    LANDING(true),
    NONE(true),
    ;
    
    private final boolean linkable;

    private PageTypeEnum(boolean linkable) {
        this.linkable = linkable;
    }
    
    public boolean isLinkable() {
        return linkable;
    }
    
}

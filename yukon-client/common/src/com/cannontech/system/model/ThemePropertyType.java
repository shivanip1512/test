package com.cannontech.system.model;

public enum ThemePropertyType {
    
    PAGE_BACKGROUND("page-background", "#6e6d71"),
    PAGE_BACKGROUND_FONT_COLOR("page-background-font-color", "#FFF"),
    NAV_SHADOW("nav-shadow", "#5a595d"),
    PRIMARY_COLOR("primary-color", "#06c"),
    LOGIN_BACKGROUND_URL("login-background-url", "/WebConfig/yukon/layout/bg_mountain_1920_420_fade2.jpg"),
    LOGIN_FONT_COLOR("login-font-color", "#fff"),
    LOGIN_FONT_SHADOW("login-font-shadow", "1px 1px 2px rgba(0,0,0,0.5)"),
    TAGLINE_MARGIN("tagline-margin", "35px"),
    LOGO_URL("logo-url", "/WebConfig/yukon/layout/eaton_logo.png"),
    LOGO_LEFT("logo-left", "0px"),
    LOGO_TOP("logo-top", "17px"),
    LOGO_WIDTH("logo-width", "163px");

    private String varName;
    private String defaultValue;
    
    private ThemePropertyType(String varName, String defaultValue) {
        this.varName = varName;
        this.defaultValue = defaultValue;
    }
    
    public String getVarName() {
        return varName;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
}
package com.cannontech.web.admin.theme.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.web.input.type.ColorType;
import com.cannontech.web.input.type.ImageType;
import com.cannontech.web.input.type.InputType;
import com.cannontech.web.input.type.PixelType;

public enum ThemePropertyType implements DisplayableEnum {
    
    BUTTON_COLOR("primary-button-color", "#007bc1", new ColorType()),
    BUTTON_COLOR_BORDER("primary-button-color-border", "#007bc1", new ColorType()),
    BUTTON_COLOR_HOVER("primary-button-color-hover", "#4da3d4", new ColorType()),
    
    PAGE_BACKGROUND("page-background", "#727e84", new ColorType()),
    PAGE_BACKGROUND_FONT_COLOR("page-background-font-color", "#FFF", new ColorType()),
    PAGE_BACKGROUND_SHADOW("nav-shadow", "#727e84", new ColorType()),
    
    PRIMARY_COLOR("primary-color", "#007bc1", new ColorType()),
    VISITED_COLOR("visited-color", "#007bc1", new ColorType()),
    
    LOGIN_BACKGROUND("login-background-url", "-2", new ImageType("backgrounds")),
    LOGIN_FONT_COLOR("login-font-color", "#fff", new ColorType()),
    LOGIN_FONT_SHADOW("login-font-shadow", "727e84", new ColorType()),
    LOGIN_TAGLINE_MARGIN("tagline-margin", "35", new PixelType()),
    
    LOGO("logo-url", "-1", new ImageType("logos")),
    LOGO_LEFT("logo-left", "0", new PixelType()),
    LOGO_TOP("logo-top", "17", new PixelType()),
    LOGO_WIDTH("logo-width", "163", new PixelType());

    private String varName;
    private String defaultValue;
    private InputType<?> inputType;
    
    private ThemePropertyType(String varName, String defaultValue, InputType<?> inputType) {
        this.varName = varName;
        this.defaultValue = defaultValue;
        this.inputType = inputType;
    }
    
    public String getVarName() {
        return varName;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public InputType<?> getInputType() {
        return inputType;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.themePropertyType." + name();
    }
    
    public boolean isColor() {
        if (inputType instanceof ColorType) {
            return true;
        }
        
        return false;
    }
}
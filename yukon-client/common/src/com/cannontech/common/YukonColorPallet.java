package com.cannontech.common;

import com.cannontech.common.i18n.DisplayableEnum;

public enum YukonColorPallet implements DisplayableEnum {

    BLACK("#000000"),
    BLUE("#4d90fe"),
    GREEN("#009933"),
    GRAY("#7b8387"),
    SAGE("#b2c98d"),
    ORANGE("#ec971f"),
    PURPLE("#b779f4"),
    WINE("#ce8799"),
    SKY("#abd7e1"),
    TEAL("#00b2a9"),
    WHITE("#FFFFFF"),
    YELLOW("#f0cb2f");

    private YukonColorPallet(String hexValue) {
        this.hexValue = hexValue;
    }

    private final String hexValue;

    public String getHexValue() {
        return hexValue;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.color." + name();
    }

    public java.awt.Color getAwtColor() {
        return java.awt.Color.decode(this.hexValue);
    }

}

package com.cannontech.common.trend.model;

import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.i18n.DisplayableEnum;

public enum Color implements DisplayableEnum {

    BLACK(Colors.BLACK_ID),
    BLUE(Colors.BLUE_ID),
    CYAN(Colors.CYAN_ID),
    // Color picker require it to be "GREY" instead of "GRAY".
    GREY(Colors.GRAY_ID),
    GREEN(Colors.GREEN_ID),
    MAGENTA(Colors.MAGENTA_ID),
    ORANGE(Colors.ORANGE_ID),
    PINK(Colors.PINK_ID),
    RED(Colors.RED_ID),
    YELLOW(Colors.YELLOW_ID);

    private int colorId;
    private String baseKey = "yukon.web.modules.tools.trend.color.";

    private Color(int id) {
        this.colorId = id;
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

    public String getHexValue() {
        return Colors.colorPaletteToWeb(colorId);
    }

}

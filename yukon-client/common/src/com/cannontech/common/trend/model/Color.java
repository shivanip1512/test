package com.cannontech.common.trend.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.common.gui.util.Colors;

public enum Color implements DatabaseRepresentationSource, DisplayableEnum {

    BLACK(Colors.BLACK_ID),
    BLUE(Colors.BLUE_ID),
    CYAN(Colors.CYAN_ID),
    GRAY(Colors.GRAY_ID),
    GREEN(Colors.GREEN_ID),
    MAGENTA(Colors.MAGENTA_ID),
    ORANGE(Colors.ORANGE_ID),
    PINK(Colors.PINK_ID),
    RED(Colors.RED_ID),
    YELLOW(Colors.YELLOW_ID);

    private int colorId;

    private Color(int id) {
        this.colorId = id;
    }

    @Override
    public String getFormatKey() {
        return null;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return colorId;
    }

    public String getHexValue() {
        return Colors.colorPaletteToWeb(colorId);
    }

}

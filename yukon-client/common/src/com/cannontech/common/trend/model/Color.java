package com.cannontech.common.trend.model;

import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum Color implements DatabaseRepresentationSource, DisplayableEnum {

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

    private final static ImmutableMap<Integer, Color> lookupById;
    static {
        Builder<Integer, Color> dbBuilder = ImmutableMap.builder();
        for (Color color : values()) {
            dbBuilder.put(color.colorId, color);
        }
        lookupById = dbBuilder.build();
    }

    private Color(int id) {
        this.colorId = id;
    }

    public static Color getColor(int colorId) {
        return lookupById.get(colorId);
    }

    @Override
    public String getFormatKey() {
        return null;
    }

    public String getHexValue() {
        return Colors.colorPaletteToWeb(colorId);
    }

    @Override
    public Object getDatabaseRepresentation() {
        return colorId;
    }

}

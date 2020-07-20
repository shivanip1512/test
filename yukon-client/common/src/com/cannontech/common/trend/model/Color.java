package com.cannontech.common.trend.model;

import com.cannontech.common.YukonColorPallet;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum Color implements DatabaseRepresentationSource, DisplayableEnum {
    
    BLUE(Colors.BLUE_ID, YukonColorPallet.BLUE),
    RED(Colors.RED_ID, YukonColorPallet.WINE),
    GREEN(Colors.GREEN_ID, YukonColorPallet.GREEN),
    BLACK(Colors.BLACK_ID, YukonColorPallet.BLACK),
    SAGE(Colors.LIGHT_GREEN_ID, YukonColorPallet.SAGE),
    ORANGE(Colors.ORANGE_ID, YukonColorPallet.ORANGE),
    YELLOW(Colors.YELLOW_ID, YukonColorPallet.YELLOW),
    PURPLE(Colors.PURPLE_ID, YukonColorPallet.PURPLE),
    GRAY(Colors.GRAY_ID, YukonColorPallet.GRAY),
    TEAL(Colors.TEAL_ID, YukonColorPallet.TEAL),
    SKY(Colors.SKY_ID, YukonColorPallet.SKY);
    
    private YukonColorPallet color;

    private int colorId;

    private Color(int colorId, YukonColorPallet color) {
        this.colorId = colorId;
        this.color = color;
    }

    private static Color[] values = values();
    private final static ImmutableMap<Integer, Color> lookupById;
    private final static ImmutableMap<String, Color> lookupByHexColorValue;
    
    static {
        Builder<Integer, Color> dbBuilder = ImmutableMap.builder();
        for (Color color : values()) {
            dbBuilder.put(color.colorId, color);
        }
        lookupById = dbBuilder.build();
        
        Builder<String, Color> hexColorLookupBuilder = ImmutableMap.builder();
        for (Color color : values()) {
            hexColorLookupBuilder.put(color.getHexValue(), color);
        }
        lookupByHexColorValue = hexColorLookupBuilder.build();
    }

    private Color(int id) {
        this.colorId = id;
    }

    public static Color getColor(int colorId) {
        return lookupById.get(colorId);
    }
    
    public static Color getColorByHexValue(String hexValue) {
        return lookupByHexColorValue.get(hexValue);
    }

    @Override
    public String getFormatKey() {
        return this.color.getFormatKey();
    }

    public String getHexValue() {
        return this.color.getHexValue();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return colorId;
    }
    
    public static Color getNextDefaultColor(int index) {
        return values[index % values.length];
    }

}

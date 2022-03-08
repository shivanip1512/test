package com.cannontech.common;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

//The hex value for these colors is defined in colors.less file.
public enum YukonColorPalette implements DatabaseRepresentationSource, DisplayableEnum {

    BLACK(ColorId.BLACK_ID),
    BLUE(ColorId.BLUE_ID, true),
    BLUE_LIGHT(ColorId.BLUE_LIGHT_ID),
    BLUE_DARK(ColorId.BLUE_DARK_ID),
    EMERALD(ColorId.EMERALD_ID),
    GREEN(ColorId.GREEN_ID, true),
    GREEN_LIGHT(ColorId.GREEN_LIGHT_ID),
    GRAY(ColorId.GRAY_ID, true),
    GRAY_LIGHT( ColorId.GRAY_LIGHT_ID),
    NAVY(ColorId.NAVY_ID),
    ORANGE(ColorId.ORANGE_ID, true),
    PINK(ColorId.PINK_ID),
    PURPLE(ColorId.PURPLE_ID, true),
    RED(ColorId.RED_ID),
    RED_LIGHT(ColorId.RED_LIGHT_ID),
    SAGE(ColorId.SAGE_ID, true),
    SILVER(ColorId.SILVER_ID),
    SKY(ColorId.SKY_ID, true),
    TEAL(ColorId.TEAL_ID, true),
    WHITE(ColorId.WHITE_ID),
    WINE(ColorId.WINE_ID, true),
    YELLOW(ColorId.YELLOW_ID, true);

    private final int colorId;  //database id
    private boolean primary;
    
    private final static ImmutableMap<Integer, YukonColorPalette> lookupById;
    
    static {
        Builder<Integer, YukonColorPalette> dbBuilder = ImmutableMap.builder();
        for (YukonColorPalette color : values()) {
            dbBuilder.put(color.colorId, color);
        }
        lookupById = dbBuilder.build();
    }
    
    class ColorId {
        static final int GREEN_ID = 0;
        static final int RED_ID = 1;
        static final int WHITE_ID = 2;
        static final int YELLOW_ID = 3;
        static final int BLUE_ID = 4;
        static final int TEAL_ID = 5;
        static final int BLACK_ID = 6;
        static final int ORANGE_ID = 7;
        static final int SAGE_ID = 8;
        static final int GRAY_ID = 9;
        static final int PURPLE_ID = 10;
        static final int SKY_ID = 11;
        static final int WINE_ID = 12;
        static final int GRAY_LIGHT_ID = 13;
        static final int RED_LIGHT_ID = 14;
        static final int BLUE_LIGHT_ID = 15;
        static final int GREEN_LIGHT_ID = 16;
        static final int BLUE_DARK_ID = 17;
        static final int SILVER_ID = 18;
        static final int PINK_ID = 19;
        static final int NAVY_ID = 20;
        static final int EMERALD_ID = 21;
    };
    
    private YukonColorPalette(int colorId) {
        this.colorId = colorId;
        this.primary = false;
    }
    
    private YukonColorPalette(int colorId, boolean primary) {
        this.colorId = colorId;
        this.primary = primary;
    }
    
    
    public String getHexValue() {
        return ColorsFileReader.getHexColor(this);
    }

    public int getColorId() {
        return colorId;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.common.color." + name();
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return colorId;
    }

    public static YukonColorPalette getColor(int colorId) {
        return lookupById.get(colorId);
    }
    
    public static YukonColorPalette getColorByHexValue(String hexValue) {
        return ColorsFileReader.getYukonColor(hexValue);
    }
    
    public java.awt.Color getAwtColor() {
        return java.awt.Color.decode(this.getHexValue());
    }
    
    public boolean isPrimary() {
        return primary;
    }

    /** 
     * Returns next color in values, loops around to the beginning of the values if index > values.length
     */
    public static YukonColorPalette getNextColor(int index) {
        return values()[index % values().length];
    }
    
    /**
     * Get formatted text of the enum name. 
     * Only use this for desktop clients
     * BLACK -> Black
     * @deprecated use MessageSourceResolver.
     */
    public String toDefaultText() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }
}

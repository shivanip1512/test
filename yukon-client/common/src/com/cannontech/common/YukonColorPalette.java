package com.cannontech.common;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum YukonColorPalette implements DatabaseRepresentationSource, DisplayableEnum {

    BLACK("#000000", ColorId.BLACK_ID),
    BLUE("#0088f2", ColorId.BLUE_ID, true),
    BLUE_LIGHT("#4d8ec4", ColorId.BLUE_LIGHT_ID),
    GREEN("#2ca618", ColorId.GREEN_ID, true),
    GREEN_LIGHT("#74cc63", ColorId.GREEN_LIGHT_ID),
    GRAY("#7b8387", ColorId.GRAY_ID, true),
    GRAY_LIGHT("#d5d8da", ColorId.GRAY_LIGHT_ID),
    ORANGE("#e99012", ColorId.ORANGE_ID, true),
    PURPLE("#b779f4", ColorId.PURPLE_ID, true),
    RED("#c53637", ColorId.RED_ID),
    RED_LIGHT("#da7777", ColorId.RED_LIGHT_ID),
    SAGE("#b2c98d", ColorId.SAGE_ID, true),
    SKY("#abd7e1", ColorId.SKY_ID, true),
    TEAL("#00b2a9", ColorId.TEAL_ID, true),
    WHITE("#ffffff", ColorId.WHITE_ID),
    WINE("#ce8799", ColorId.WINE_ID, true),
    YELLOW("#f0cb2f", ColorId.YELLOW_ID, true);

    private final String hexValue;
    private final int colorId;  //database id
    private boolean primary;
    
    private final static ImmutableMap<Integer, YukonColorPalette> lookupById;
    private final static ImmutableMap<String, YukonColorPalette> lookupByHexColorValue;
    
    static {
        Builder<Integer, YukonColorPalette> dbBuilder = ImmutableMap.builder();
        for (YukonColorPalette color : values()) {
            dbBuilder.put(color.colorId, color);
        }
        lookupById = dbBuilder.build();
        
        Builder<String, YukonColorPalette> hexColorLookupBuilder = ImmutableMap.builder();
        for (YukonColorPalette color : values()) {
            hexColorLookupBuilder.put(color.getHexValue(), color);
        }
        lookupByHexColorValue = hexColorLookupBuilder.build();
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
    };
    
    private YukonColorPalette(String hexValue, int colorId) {
        this.hexValue = hexValue;
        this.colorId = colorId;
        this.primary = false;
    }
    
    private YukonColorPalette(String hexValue, int colorId, boolean primary) {
        this.hexValue = hexValue;
        this.colorId = colorId;
        this.primary = primary;
    }
    
    
    public String getHexValue() {
        return hexValue;
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
        return lookupByHexColorValue.get(hexValue);
    }
    
    public java.awt.Color getAwtColor() {
        return java.awt.Color.decode(this.hexValue);
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

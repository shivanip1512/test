package com.cannontech.common;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum YukonColorPalette implements DatabaseRepresentationSource, DisplayableEnum {

    // next colorId to use: 14
    BLACK("#000000", 6),
    BLUE("#4d90fe", 4),
    GREEN("#009933", 0),
    GRAY("#7b8387", 9),
    LIGHT_GRAY("#d5d8da", 13),
    ORANGE("#ec971f", 7),
    PURPLE("#b779f4", 10),
    RED("#d14836", 1),
    SAGE("#b2c98d", 8),
    SKY("#abd7e1", 11),
    TEAL("#00b2a9", 5),
    WHITE("#ffffff", 2),
    WINE("#ce8799", 12),
    YELLOW("#f0cb2f", 3);

    private final String hexValue;
    private final int colorId;  //database id

    
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
    
    private YukonColorPalette(String hexValue, int colorId) {
        this.hexValue = hexValue;
        this.colorId = colorId;
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

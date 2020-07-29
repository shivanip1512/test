package com.cannontech.common;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum YukonColorPallet implements DatabaseRepresentationSource, DisplayableEnum {

    BLACK("#000000", 6),
    BLUE("#4d90fe", 4),
    GREEN("#009933", 0),
    GRAY("#7b8387", 9),
    SAGE("#b2c98d", 8),
    ORANGE("#ec971f", 7),
    PURPLE("#b779f4", 10),
    WINE("#ce8799", 1),
    SKY("#abd7e1", 11),
    TEAL("#00b2a9", 5),
    WHITE("#FFFFFF", 2),
    YELLOW("#f0cb2f", 3);

    private final String hexValue;
    private final int colorId;

    
    private final static ImmutableMap<Integer, YukonColorPallet> lookupById;
    private final static ImmutableMap<String, YukonColorPallet> lookupByHexColorValue;
    
    static {
        Builder<Integer, YukonColorPallet> dbBuilder = ImmutableMap.builder();
        for (YukonColorPallet color : values()) {
            dbBuilder.put(color.colorId, color);
        }
        lookupById = dbBuilder.build();
        
        Builder<String, YukonColorPallet> hexColorLookupBuilder = ImmutableMap.builder();
        for (YukonColorPallet color : values()) {
            hexColorLookupBuilder.put(color.getHexValue(), color);
        }
        lookupByHexColorValue = hexColorLookupBuilder.build();
    }
    
    private YukonColorPallet(String hexValue, int colorId) {
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

    public static YukonColorPallet getColor(int colorId) {
        return lookupById.get(colorId);
    }
    
    public static YukonColorPallet getColorByHexValue(String hexValue) {
        return lookupByHexColorValue.get(hexValue);
    }
    
    public java.awt.Color getAwtColor() {
        return java.awt.Color.decode(this.hexValue);
    }
    
    /** 
     * Returns next color in values, loops around to the beginning of the values if index > values.length
     */
    public static YukonColorPallet getNextColor(int index) {
        return values()[index % values().length];
    }
    
    /**
     * Get formatted text of the enum name. 
     * Only use this for desktop clients
     * BLACK -> Black
     * @deprecated use MessageSourceResolver.
     */
    public String toDefaultText() {
        if (this == WINE) {
            return "Red";
        }
        return StringUtils.capitalize(this.name().toLowerCase());
    }
}

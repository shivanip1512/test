package com.cannontech.common.trend.model;

import java.util.Arrays;

import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.exception.TypeNotSupportedException;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Provides colors for graphs.
 */
public enum GraphColors {
    BLACK(YukonColorPalette.BLACK),
    BLUE(YukonColorPalette.BLUE),
    GRAY(YukonColorPalette.GRAY),
    GREEN(YukonColorPalette.GREEN),
    ORANGE(YukonColorPalette.ORANGE),
    PURPLE(YukonColorPalette.PURPLE),
    SAGE(YukonColorPalette.SAGE),
    SKY(YukonColorPalette.SKY),
    TEAL(YukonColorPalette.TEAL),
    WINE(YukonColorPalette.WINE),   // do not use RED in this enum, as it's text will conflict. Both use "Red".
    YELLOW(YukonColorPalette.YELLOW);
    
    private final YukonColorPalette yukonColor;
    private final static YukonColorPalette[] yukonColors;
    
    static {
        yukonColors = Arrays.stream(GraphColors.values())
                            .map(GraphColors::getYukonColor)
                            .toArray(YukonColorPalette[]::new);
    }

    private GraphColors(YukonColorPalette yukonColor) {
        this.yukonColor = yukonColor;
    }

    public YukonColorPalette getYukonColor() {
        return yukonColor;
    }

    public static YukonColorPalette[] getYukonColors() {
        return yukonColors;
    }

    public String getHexValue() {
        return getYukonColor().getHexValue();
    }

    public static GraphColors getNextDefaultColor(int index) {
        return GraphColors.values()[index % GraphColors.values().length];
    }

    /*
     * Returns GraphColors object for given string of color otherwise throw error message
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static GraphColors getGraphColor(String color) {
        try {
            return GraphColors.valueOf(color);
        } catch (IllegalArgumentException e) {
            throw new TypeNotSupportedException(color + " Graph Color is not valid.");
        }
    }

}
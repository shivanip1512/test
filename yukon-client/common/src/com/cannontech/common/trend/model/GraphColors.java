package com.cannontech.common.trend.model;

import java.util.Arrays;

import com.cannontech.common.YukonColorPalette;

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

    public static YukonColorPalette getNextDefaultColor(int index) {
        return GraphColors.values()[index % GraphColors.values().length].getYukonColor();
    } 
}
package com.cannontech.common.trend.model;

import java.util.Arrays;

import com.cannontech.common.YukonColorPallet;

/**
 * Provides colors for graphs.
 */
public enum GraphColors {
    WINE(YukonColorPallet.WINE),
    BLUE(YukonColorPallet.BLUE),
    GREEN(YukonColorPallet.GREEN),
    BLACK(YukonColorPallet.BLACK),
    SAGE(YukonColorPallet.SAGE),
    ORANGE(YukonColorPallet.ORANGE),
    SKY(YukonColorPallet.SKY),
    YELLOW(YukonColorPallet.YELLOW),
    PURPLE(YukonColorPallet.PURPLE),
    GRAY(YukonColorPallet.GRAY),
    TEAL(YukonColorPallet.TEAL);
    
    private final YukonColorPallet yukonColor;
    private final static YukonColorPallet[] yukonColors;
    
    static {
        yukonColors = Arrays.stream(GraphColors.values())
                            .map(GraphColors::getYukonColor)
                            .toArray(YukonColorPallet[]::new);
    }

    private GraphColors(YukonColorPallet yukonColor) {
        this.yukonColor = yukonColor;
    }

    public YukonColorPallet getYukonColor() {
        return yukonColor;
    }

    public static YukonColorPallet[] getYukonColors() {
        return yukonColors;
    }

    public String getHexValue() {
        return getYukonColor().getHexValue();
    }

    public static YukonColorPallet getNextDefaultColor(int index) {
        return GraphColors.values()[index % GraphColors.values().length].getYukonColor();
    } 
}
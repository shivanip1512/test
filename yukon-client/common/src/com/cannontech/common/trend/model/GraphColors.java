package com.cannontech.common.trend.model;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.common.i18n.DisplayableEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Provides colors for graphs.
 */
public enum GraphColors implements DisplayableEnum {
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
    private final static ImmutableMap<YukonColorPalette, GraphColors> lookupByYukonColor;

    static {
        yukonColors = Arrays.stream(GraphColors.values())
                .map(GraphColors::getYukonColor)
                .toArray(YukonColorPalette[]::new);
        Builder<YukonColorPalette, GraphColors> dbBuilder = ImmutableMap.builder();
        for (GraphColors color : values()) {
            dbBuilder.put(color.getYukonColor(), color);
        }
        lookupByYukonColor = dbBuilder.build();
    }

    /*
     * Return GraphColors for corresponding YukonColorPalette
     */
    public static GraphColors getByYukonColor(YukonColorPalette yukonColor) throws IllegalArgumentException {
        GraphColors graphColor = lookupByYukonColor.get(yukonColor);
        checkArgument(graphColor != null, yukonColor);
        return lookupByYukonColor.get(yukonColor);
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

    @Override
    public String getFormatKey() {
        return this.yukonColor.getFormatKey();
    }

}
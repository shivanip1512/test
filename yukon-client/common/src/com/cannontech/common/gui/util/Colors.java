package com.cannontech.common.gui.util;

import com.cannontech.common.YukonColorPallet;
import com.google.common.collect.ImmutableMap;

/**
 * Enumerates colors by integer and by string.
 * Provides static methods to obtain the java.awt.Color that the given
 * enumerated value represents.
 * Creation date: (10/4/00 1:54:49 PM)
 * 
 * @author: Aaron Lauinger
 */
public class Colors {

    /*
     * Add new colors to both the integer and string enumerations and
     * update the two private arrays that do the mapping.
     */

    public static final int GREEN_ID = 0;
    public static final int RED_ID = 1;
    public static final int WHITE_ID = 2;
    public static final int YELLOW_ID = 3;
    public static final int BLUE_ID = 4;
    public static final int TEAL_ID = 5;
    public static final int BLACK_ID = 6;
    public static final int ORANGE_ID = 7;
    public static final int LIGHT_GREEN_ID = 8;
    public static final int GRAY_ID = 9;
    public static final int PURPLE_ID = 10;
    public static final int SKY_ID = 11;

    public static final String GREEN_STR_ID = "Green";
    public static final String RED_STR_ID = "Red";
    public static final String WHITE_STR_ID = "White";
    public static final String YELLOW_STR_ID = "Yellow";
    public static final String BLUE_STR_ID = "Blue";
    public static final String TEAL_STR_ID = "Teal";
    public static final String BLACK_STR_ID = "Black";
    public static final String ORANGE_STR_ID = "Orange";
    public static final String LIGHT_GREEN_STR_ID = "Sage";
    public static final String GRAY_STR_ID = "Gray";
    public static final String PURPLE_STR_ID = "Purple";
    public static final String SKY_STR_ID = "Sky";
   
    /* Map builder for conversion to web color */
    private static ImmutableMap<Integer, String> colorPaletteToWeb = ImmutableMap.<Integer, String>builder()
            .put(Colors.BLACK_ID, YukonColorPallet.BLACK.getHexValue())
            .put(Colors.BLUE_ID, YukonColorPallet.BLUE.getHexValue())
            .put(Colors.TEAL_ID, YukonColorPallet.TEAL.getHexValue())
            .put(Colors.GRAY_ID, YukonColorPallet.GRAY.getHexValue())
            .put(Colors.GREEN_ID, YukonColorPallet.GREEN.getHexValue())
            .put(Colors.LIGHT_GREEN_ID, YukonColorPallet.SAGE.getHexValue())
            .put(Colors.ORANGE_ID, YukonColorPallet.ORANGE.getHexValue())
            .put(Colors.PURPLE_ID, YukonColorPallet.PURPLE.getHexValue())
            .put(Colors.RED_ID, YukonColorPallet.WINE.getHexValue())
            .put(Colors.YELLOW_ID, YukonColorPallet.YELLOW.getHexValue())
            .put(Colors.SKY_ID, YukonColorPallet.SKY.getHexValue())
            .build();

    public static String colorPaletteToWeb(int color) {
        String retval = colorPaletteToWeb.get(color);
        return retval == null ? "#FFFFFF" : retval;
    }

    // Mapping from integer enumerations into java.awt.Color
    private static final java.awt.Color[] IDToColorMapping = {
            YukonColorPallet.GREEN.getAwtColor(),
            YukonColorPallet.WINE.getAwtColor(),
            YukonColorPallet.WHITE.getAwtColor(),
            YukonColorPallet.YELLOW.getAwtColor(),
            YukonColorPallet.BLUE.getAwtColor(),
            YukonColorPallet.TEAL.getAwtColor(),
            YukonColorPallet.BLACK.getAwtColor(),
            YukonColorPallet.ORANGE.getAwtColor(),
            YukonColorPallet.SAGE.getAwtColor(),
            YukonColorPallet.GRAY.getAwtColor(),
            YukonColorPallet.PURPLE.getAwtColor(),
            YukonColorPallet.SKY.getAwtColor(),
    };

    // Mapping from string enumerations into java.awt.Color
    private static final Object[][] IDStrToColorMapping = {
            { GREEN_STR_ID, IDToColorMapping[GREEN_ID] },
            { RED_STR_ID, IDToColorMapping[RED_ID] },
            { WHITE_STR_ID, IDToColorMapping[WHITE_ID] },
            { YELLOW_STR_ID, IDToColorMapping[YELLOW_ID] },
            { BLUE_STR_ID, IDToColorMapping[BLUE_ID] },
            { TEAL_STR_ID, IDToColorMapping[TEAL_ID] },
            { BLACK_STR_ID, IDToColorMapping[BLACK_ID] },
            { ORANGE_STR_ID, IDToColorMapping[ORANGE_ID] },
            { LIGHT_GREEN_STR_ID, IDToColorMapping[LIGHT_GREEN_ID] },
            { GRAY_STR_ID, IDToColorMapping[GRAY_ID] },
            { PURPLE_STR_ID, IDToColorMapping[PURPLE_ID] },
            { SKY_STR_ID, IDToColorMapping[SKY_ID] }
    };

    /**
     * ColorFactory constructor comment.
     */
    private Colors() {
        super();
    }

    /**
     * Returns the java.awt.Color object is represented by the given color id.
     * Creation date: (10/4/00 2:07:05 PM)
     * 
     * @return java.awt.Color
     * @param colorID int
     */
    public final static java.awt.Color getColor(int colorID) {
        return IDToColorMapping[colorID];
    }

    /**
     * Returns the java.awt.Color object is represented by the given color id string
     * Creation date: (10/4/00 2:06:50 PM)
     * 
     * @return java.awt.Color
     * @param colorStrID java.lang.String
     */
    public static final java.awt.Color getColor(String colorStrID) {
        for (int i = 0; i < IDStrToColorMapping.length; i++)
            if (IDStrToColorMapping[i][0].equals(colorStrID))
                return (java.awt.Color) IDStrToColorMapping[i][1];

        return null;
    }

    /**
     * Insert the method's description here.
     * Creation date: (11/6/00 11:04:48 AM)
     * 
     * @return int
     * @param c java.awt.Color
     */
    public static final int getColorID(java.awt.Color c) {
        for (int i = 0; i < IDToColorMapping.length; i++) {
            if (IDToColorMapping[i].getRGB() == c.getRGB())
                return i;
        }

        return -1;
    }

    /**
     * Insert the method's description here.
     * Creation date: (11/6/00 12:44:01 PM)
     * 
     * @return java.awt.Color[]
     */
    public final static java.awt.Color[] getColors() {
        return IDToColorMapping;
    }

    /**
     * Insert the method's description here.
     * Creation date: (11/6/00 11:43:45 AM)
     * 
     * @return java.lang.String
     * @param c int
     */
    public static String getColorString(int c) {
        return (String) IDStrToColorMapping[c][0];
    }
}

package com.cannontech.graph;

import com.cannontech.common.YukonColorPallet;

/**
 * Provides colors for graphs and maintains state that
 * allow a user of this class to ask for valid graph colors.
 * A bit unsettling.
 * Creation date: (10/26/00 11:57:36 AM)
 * 
 * @author:
 */
public class GraphColors {
    
    private static final java.awt.Color lineColors[] = {
        YukonColorPallet.RED.getAwtColor(),
        YukonColorPallet.BLUE.getAwtColor(),
        YukonColorPallet.GREEN.getAwtColor(),
        YukonColorPallet.BLACK.getAwtColor(),
        YukonColorPallet.LIGHT_GREEN.getAwtColor(),
        YukonColorPallet.ORANGE.getAwtColor(),
        YukonColorPallet.SKY.getAwtColor(),
        YukonColorPallet.YELLOW.getAwtColor(),
        YukonColorPallet.PURPLE.getAwtColor(),
        YukonColorPallet.GRAY.getAwtColor(),
        YukonColorPallet.TEAL.getAwtColor(),
    };

    private static final int lineColorEnumeration[] = { 
        com.cannontech.common.gui.util.Colors.RED_ID,
        com.cannontech.common.gui.util.Colors.BLUE_ID,
        com.cannontech.common.gui.util.Colors.GREEN_ID,
        com.cannontech.common.gui.util.Colors.BLACK_ID,
        com.cannontech.common.gui.util.Colors.LIGHT_GREEN_ID,
        com.cannontech.common.gui.util.Colors.ORANGE_ID,
        com.cannontech.common.gui.util.Colors.SKY_ID,
        com.cannontech.common.gui.util.Colors.YELLOW_ID,
        com.cannontech.common.gui.util.Colors.PURPLE_ID,
        com.cannontech.common.gui.util.Colors.GRAY_ID,
        com.cannontech.common.gui.util.Colors.TEAL_ID,
    };

    // index into line colors array to determine line colors
    private int currentLineColor = 0;

    /**
     * GraphColors constructor comment.
     */
    public GraphColors() {
        super();
    }

    /**
     * Insert the method's description here.
     * Creation date: (10/26/00 1:08:52 PM)
     * 
     * @return java.awt.Color
     */
    public java.awt.Color[] getAvailableColors() {
        return lineColors;
    }

    /**
     * Insert the method's description here.
     * Creation date: (10/11/00 4:13:48 PM)
     * 
     * @return java.awt.Color
     */
    public synchronized java.awt.Color getNextLineColor() {
        java.awt.Color retVal = lineColors[currentLineColor++];

        if (currentLineColor == lineColors.length)
            currentLineColor = 0;

        return retVal;
    }

    /**
     * Insert the method's description here.
     * Creation date: (10/11/00 4:13:48 PM)
     * 
     * @return java.awt.Color
     */
    public synchronized int getNextLineColorID() {
        int retVal = lineColorEnumeration[currentLineColor++];

        if (currentLineColor == lineColorEnumeration.length)
            currentLineColor = 0;

        return retVal;
    }

    /**
     * Insert the method's description here.
     * Creation date: (10/11/00 4:14:54 PM)
     */
    public synchronized void resetCurrentLineColor() {
        currentLineColor = 0;
    }
}

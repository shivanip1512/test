package com.cannontech.graph;

/**
 * Provides colors for graphs and maintains state that
 * allow a user of this class to ask for valid graph colors.
 * A bit unsettling.
 * Creation date: (10/26/00 11:57:36 AM)
 * 
 * @author:
 */
public class GraphColors {
    private static final java.awt.Color lineColors[] = { java.awt.Color.red, java.awt.Color.blue, java.awt.Color.green,
        java.awt.Color.black, java.awt.Color.magenta, java.awt.Color.orange, java.awt.Color.yellow,
        java.awt.Color.pink, java.awt.Color.gray };

    private static final int lineColorEnumeration[] = { com.cannontech.common.gui.util.Colors.RED_ID,
        com.cannontech.common.gui.util.Colors.BLUE_ID, com.cannontech.common.gui.util.Colors.GREEN_ID,
        com.cannontech.common.gui.util.Colors.BLACK_ID, com.cannontech.common.gui.util.Colors.MAGENTA_ID,
        com.cannontech.common.gui.util.Colors.ORANGE_ID, com.cannontech.common.gui.util.Colors.YELLOW_ID,
        com.cannontech.common.gui.util.Colors.PINK_ID, com.cannontech.common.gui.util.Colors.GRAY_ID };

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

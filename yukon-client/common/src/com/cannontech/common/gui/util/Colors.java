package com.cannontech.common.gui.util;

import com.cannontech.common.YukonColorPallet;

/**
 * Provides static methods to obtain the java.awt.Color that the given
 * enumerated value represents.
 * TODO - delete this class and swap out all callers of this method
 */
public class Colors {

    /**
     * ColorFactory constructor comment.
     */
    private Colors() {
        super();
    }

    /**
     * Returns the java.awt.Color object is represented by the given color id.
     * @deprecated
     */
    public final static java.awt.Color getColor(int colorID) {
        return YukonColorPallet.getColor(colorID).getAwtColor();
    }
}

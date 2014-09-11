package com.cannontech.util;

import java.awt.Color;

/**
 * Util to convert colors to different formats.
 */
public class ColorUtil {
    private static final String _KEY = "0123456789ABCDEF";

    public static String getHex(int[] rgb) {
        StringBuffer sb = new StringBuffer();
        sb.append("#");
        sb.append(_KEY.substring((int) Math.floor(rgb[0] / 16), (int) Math.floor(rgb[0] / 16) + 1));
        sb.append(_KEY.substring(rgb[0] % 16, (rgb[0] % 16) + 1));
        sb.append(_KEY.substring((int) Math.floor(rgb[1] / 16), (int) Math.floor(rgb[1] / 16) + 1));
        sb.append(_KEY.substring(rgb[1] % 16, (rgb[1] % 16) + 1));
        sb.append(_KEY.substring((int) Math.floor(rgb[2] / 16), (int) Math.floor(rgb[2] / 16) + 1));
        sb.append(_KEY.substring(rgb[2] % 16, (rgb[2] % 16) + 1));
        return sb.toString();
    }

    /**
     * Convert Color object to HTML string rapresentation (#RRGGBB).
     *
     * @param c color to convert
     * @return html string rapresentation (#RRGGBB)
     */
    public static String getHTMLColor(Color c) {
        String colorR = "0" + Integer.toHexString(c.getRed());
        colorR = colorR.substring(colorR.length() - 2);
        String colorG = "0" + Integer.toHexString(c.getGreen());
        colorG = colorG.substring(colorG.length() - 2);
        String colorB = "0" + Integer.toHexString(c.getBlue());
        colorB = colorB.substring(colorB.length() - 2);
        String html_color = "#" + colorR + colorG + colorB;
        return html_color;
    }
}
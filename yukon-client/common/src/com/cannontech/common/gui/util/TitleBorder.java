package com.cannontech.common.gui.util;

/**
 * This type was created by Cannon Technologies Inc.
 */
public class TitleBorder extends javax.swing.border.TitledBorder {
/**
 * TitleBorder constructor comment.
 * @param title java.lang.String
 */
public TitleBorder() {
	super("");
}
/**
 * TitleBorder constructor comment.
 * @param title java.lang.String
 */
public TitleBorder(String title) {
	super(title);
}
/**
 * TitleBorder constructor comment.
 * @param border javax.swing.border.Border
 */
public TitleBorder(javax.swing.border.Border border) {
	super(border);
}
/**
 * TitleBorder constructor comment.
 * @param border javax.swing.border.Border
 * @param title java.lang.String
 */
public TitleBorder(javax.swing.border.Border border, String title) {
	super(border, title);
}
/**
 * TitleBorder constructor comment.
 * @param border javax.swing.border.Border
 * @param title java.lang.String
 * @param titleJustification int
 * @param titlePosition int
 */
public TitleBorder(javax.swing.border.Border border, String title, int titleJustification, int titlePosition) {
	super(border, title, titleJustification, titlePosition);
}
/**
 * TitleBorder constructor comment.
 * @param border javax.swing.border.Border
 * @param title java.lang.String
 * @param titleJustification int
 * @param titlePosition int
 * @param titleFont java.awt.Font
 */
public TitleBorder(javax.swing.border.Border border, String title, int titleJustification, int titlePosition, java.awt.Font titleFont) {
	super(border, title, titleJustification, titlePosition, titleFont);
}
/**
 * TitleBorder constructor comment.
 * @param border javax.swing.border.Border
 * @param title java.lang.String
 * @param titleJustification int
 * @param titlePosition int
 * @param titleFont java.awt.Font
 * @param titleColor java.awt.Color
 */
public TitleBorder(javax.swing.border.Border border, String title, int titleJustification, int titlePosition, java.awt.Font titleFont, java.awt.Color titleColor) {
	super(border, title, titleJustification, titlePosition, titleFont, titleColor);
}
}

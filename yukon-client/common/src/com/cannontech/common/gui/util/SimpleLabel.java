package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (3/28/00 11:38:51 AM)
 * @author: 
 */
public class SimpleLabel extends javax.swing.JComponent {
	private java.lang.String text = "";
	private java.awt.Color background;
	private java.awt.Color foreground;
	private java.awt.FontMetrics fontMetrics;
	private java.awt.Dimension size;
	private java.awt.Font fonts;
/**
 * SimpleLabel constructor comment.
 */
public SimpleLabel(String text, java.awt.Font font, java.awt.Color background, java.awt.Color foreground) {
	super();
	//setOpaque(true);
	setFont(font);
	fonts= font;
	setBackground(background);
	setForeground(foreground);
	
	this.background = background;
	this.foreground = foreground;

	fontMetrics = getFontMetrics(font);

	size = new java.awt.Dimension();
	setText(text);
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/00 11:39:19 AM)
 * @return java.lang.String
 */
public java.lang.String getText() {
	return text;
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/00 11:41:35 AM)
 * @param g java.awt.Graphics
 */
public void paint(java.awt.Graphics g)
{
    g.setFont(fonts);
    g.setColor(background);
    g.fillRect(0, 0, size.width, size.height);
    g.setColor(foreground);
    g.drawString(text, 0, size.height - 5);
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/00 11:39:19 AM)
 * @param newText java.lang.String
 */
public void setText(java.lang.String newText) {
	text = newText;
	size.width = fontMetrics.stringWidth(text);
	size.height = fontMetrics.getHeight();
	setPreferredSize(size);
}
}

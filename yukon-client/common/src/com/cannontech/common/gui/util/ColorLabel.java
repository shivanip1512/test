package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Color;
import java.awt.Graphics;
 
public class ColorLabel extends javax.swing.JComponent {
	private Color color = Color.red;
/**
 * Creates a ColorLabel which is red and has no size.
 */
public ColorLabel() {
	super();
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 */
public java.awt.Color getColor() {
	return color;
}
/**
 * This method was created in VisualAge.
 * @param g Graphics
 */
public void paint(Graphics g ) {
	super.paint(g);
	//Simply draw a rectangle the colors specified
	g.setColor( getColor() );
	g.fill3DRect(0, 0, getSize().width, getSize().height, true );
	
}
/**
 * This method was created in VisualAge.
 * @param newValue java.awt.Color
 */
public void setColor(java.awt.Color newValue) {
	this.color = newValue;
}
}

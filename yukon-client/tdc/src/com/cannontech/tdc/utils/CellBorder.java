package com.cannontech.tdc.utils;

/**
 * Insert the type's description here.
 * Creation date: (2/9/00 1:14:40 PM)
 * @author: 
 */
public class CellBorder implements javax.swing.border.Border {
/**
 * CellBorder constructor comment.
 */
public CellBorder() {
	super();
}
/**
 * getBorderInsets method comment.
 */
public java.awt.Insets getBorderInsets(java.awt.Component c) 
{
//	return null;
	return new java.awt.Insets(1,1,1,1 );
}
/**
 * isBorderOpaque method comment.
 */
public boolean isBorderOpaque() {
	return false;
}
/**
 * paintBorder method comment.
 *	draws a top line and a bottom line in a component
 */
public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int width, int height) 
{ 
	java.awt.Color oldColor = g.getColor();
	
	g.setColor(java.awt.Color.yellow);
	
	// top line
	g.drawLine( x, y, width, y );

	// bottom line
	g.drawLine( x, height-1, width, height-1 );
	
	g.setColor( oldColor );

}
}

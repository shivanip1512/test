package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Color;
import java.awt.Graphics;
 
public class LineLabel extends javax.swing.JComponent 
{
	private Color color = Color.black;
	private Color bgColor = Color.white;
	
	public static final int BOT_LEFT_UP_RIGHT = 0;
	public static final int UP_LEFT_BOT_RIGHT = 1;
	public static final int BOTTOM = 2;
	public static final int NO_LINE = 3;

	private int drawType = BOT_LEFT_UP_RIGHT;
/**
 * Creates a ColorLabel which is red and has no size.
 */
public LineLabel() {
	super();

	setBackground(bgColor);
}
/**
 * Creates a ColorLabel which is red and has no size.
 */
public LineLabel( int drawType_ ) 
{
	super();

	setBackground(bgColor);
	drawType = drawType_;
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
public void paint(Graphics g ) 
{
	super.paint(g);

	//Simply draw a line through the label with the given color
	g.setColor( getColor() );

	if( drawType == BOT_LEFT_UP_RIGHT )
		g.drawLine( 0, getSize().height, getSize().width, 0 );
	else if( drawType == BOTTOM )
		g.drawLine( 0, getSize().height-1, getSize().width, getSize().height-1 );
	else if( drawType == NO_LINE )
		return;
	else
		g.drawLine(0, 0, getSize().width, getSize().height );
	
}
/**
 * This method was created in VisualAge.
 * @param newValue java.awt.Color
 */
public void setColor(java.awt.Color newValue) {
	this.color = newValue;
}
}

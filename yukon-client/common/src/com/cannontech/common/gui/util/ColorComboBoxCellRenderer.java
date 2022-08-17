package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (1/19/00 1:38:38 PM)
 * @author: 
 */
public class ColorComboBoxCellRenderer extends javax.swing.JPanel implements javax.swing.ListCellRenderer {
	private java.awt.Color iconColor = null;
	private String boxString = null;
/**
 * ForegroundColorCellRenderer constructor comment.
 */
public ColorComboBoxCellRenderer() {
	super();
	setOpaque(true);
	setPreferredSize(new java.awt.Dimension(100, 18));
	setMinimumSize(new java.awt.Dimension(100, 18));
}
/**
 * Insert the method's description here.
 * Creation date: (1/19/00 3:11:02 PM)
 * @return java.awt.Color
 * @param colorString java.lang.String
 */
private java.awt.Color getColorFromString(String colorString) {
	java.awt.Color stringColor = null;
	if( colorString.equalsIgnoreCase("Green") )
		stringColor = java.awt.Color.green;
	else if( colorString.equalsIgnoreCase("Red") )
		stringColor = java.awt.Color.red;
	else if( colorString.equalsIgnoreCase("White") )
		stringColor = java.awt.Color.white;
	else if( colorString.equalsIgnoreCase("Yellow") )
		stringColor = java.awt.Color.yellow;
	else if( colorString.equalsIgnoreCase("Blue") )
		stringColor = java.awt.Color.blue;
	else if( colorString.equalsIgnoreCase("Cyan") )
		stringColor = java.awt.Color.cyan;
	else if( colorString.equalsIgnoreCase("Black") )
		stringColor = java.awt.Color.black;
	else if( colorString.equalsIgnoreCase("Orange") )
		stringColor = java.awt.Color.orange;
	else if( colorString.equalsIgnoreCase("Magenta") )
		stringColor = java.awt.Color.magenta;
	else if( colorString.equalsIgnoreCase("Gray") )
		stringColor = java.awt.Color.gray;
	else if( colorString.equalsIgnoreCase("Pink") )
		stringColor = java.awt.Color.pink;

	return stringColor;
}
/**
 * getListCellRendererComponent method comment.
 */
public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	iconColor = (getColorFromString((String)value));
	boxString = (String)value;
	return this;
}
/**
 * Insert the method's description here.
 * Creation date: (1/19/00 2:29:21 PM)
 * @param g java.awt.Graphics
 */
public void paint(java.awt.Graphics g) 
{
   //color
	g.setColor(iconColor);
	g.fillRect(0,0,((int)(.15*getWidth())), getHeight() - 1 );
	g.setColor(java.awt.Color.black);
	g.drawRect(0,0,(int)(.15*getWidth()),getHeight() - 1);
   
   //text
	g.drawString( boxString,
         ((int)(.15*getWidth())+5),
         ((int)(.5*getHeight() + .25*g.getFontMetrics().getHeight())));
}
}

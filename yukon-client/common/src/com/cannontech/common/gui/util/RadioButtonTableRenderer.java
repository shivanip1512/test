package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (11/9/00 5:29:43 PM)
 * @author: 
 */
public class RadioButtonTableRenderer extends javax.swing.JRadioButton implements javax.swing.table.TableCellRenderer
{
	private java.awt.Color foreGroundColor = null;
	private java.awt.Color backGroundColor = null;
	private java.awt.Font font = null;
	private javax.swing.border.Border border = null;
/**
 * CheckBoxTableRenderer constructor comment.
 */
public RadioButtonTableRenderer() 
{
	super();

	this.border = getBorder();
	this.setOpaque(true);
}
	/**
	 *  This method is sent to the renderer by the drawing table to
	 *  configure the renderer appropriately before drawing.  Return
	 *  the Component used for drawing.
	 *
	 * @param	table		the JTable that is asking the renderer to draw.
	 *				This parameter can be null.
	 * @param	value		the value of the cell to be rendered.  It is
	 *				up to the specific renderer to interpret
	 *				and draw the value.  eg. if value is the
	 *				String "true", it could be rendered as a
	 *				string or it could be rendered as a check
	 *				box that is checked.  null is a valid value.
	 * @param	isSelected	true is the cell is to be renderer with
	 *				selection highlighting
	 * @param	row	        the row index of the cell being drawn.  When
	 *				drawing the header the rowIndex is -1.
	 * @param	column	        the column index of the cell being drawn
	 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
{
	java.awt.Color cellForeground = foreGroundColor != null ? foreGroundColor : table.getForeground();
	java.awt.Color cellBackground = backGroundColor != null ? backGroundColor : table.getBackground();	
	setFont(font != null ? font : table.getFont() )	;


	super.setForeground(cellForeground);
	super.setBackground(cellBackground);


	//customize the components appearance
	setValue(value);

	return this;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:35:26 PM)
 * @param newBackGroundColor java.awt.Color
 */
public void setBackGroundColor(java.awt.Color newBackGroundColor) {
	backGroundColor = newBackGroundColor;
	super.setBackground(newBackGroundColor);
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:35:26 PM)
 * @param newFont java.awt.Font
 */
public void setFont(java.awt.Font newFont) {
	font = newFont;
	super.setFont(newFont);
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:35:26 PM)
 * @param newForeGroundColor java.awt.Color
 */
public void setForeGroundColor(java.awt.Color newForeGroundColor) {
	foreGroundColor = newForeGroundColor;
	super.setForeground(newForeGroundColor);
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:43:55 PM)
 * @param value java.lang.Object
 */
protected void setValue(Object value) 
{
	if( value instanceof Boolean )
	{
		setSelected( ((Boolean)value).booleanValue() );
	}
}
}

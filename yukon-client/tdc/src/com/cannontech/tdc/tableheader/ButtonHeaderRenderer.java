package com.cannontech.tdc.tableheader;

/**
 * Insert the type's description here.
 * Creation date: (4/4/00 2:04:21 PM)
 * @author: 
 * @Version: <version>
 */
public class ButtonHeaderRenderer extends javax.swing.JButton implements javax.swing.table.TableCellRenderer 
{
	private int pushedColumn = -1;	
/**
 * ButtonHeaderRenderer constructor comment.
 */
public ButtonHeaderRenderer() 
{
	super();

	setMargin( new java.awt.Insets( 0, 0, 0, 0) );
}
/**
 * ButtonHeaderRenderer constructor comment.
 * @param text java.lang.String
 */
public ButtonHeaderRenderer(String text) {
	super(text);
}
/**
 * ButtonHeaderRenderer constructor comment.
 * @param text java.lang.String
 * @param icon javax.swing.Icon
 */
public ButtonHeaderRenderer(String text, javax.swing.Icon icon) {
	super(text, icon);
}
/**
 * ButtonHeaderRenderer constructor comment.
 * @param icon javax.swing.Icon
 */
public ButtonHeaderRenderer(javax.swing.Icon icon) {
	super(icon);
}
/**
 * getTableCellRendererComponent method comment.
 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
{
	setText( (value == null) ? "" : value.toString() );

	boolean isPressed = (column == pushedColumn);
	getModel().setPressed( isPressed );
	getModel().setArmed( isPressed );
	
	return this;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/00 2:07:09 PM)
 * Version: <version>
 * @param col int
 */
public void setPressedColumn(int col) 
{
	pushedColumn = col;	
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/00 2:07:09 PM)
 * Version: <version>
 * @param col int
 */
public void setUnPressedColumn() 
{
	pushedColumn = -1;	
}
}

package com.cannontech.tdc.tableheader;

/**
 * Insert the type's description here.
 * Creation date: (4/4/00 12:42:24 PM)
 * @author: 
 * @Version: <version>
 */
public class LabelHeaderRenderer extends javax.swing.JLabel implements javax.swing.table.TableCellRenderer 
{
	
/**
 * LabelHeaderRenderer constructor comment.
 */
public LabelHeaderRenderer() 
{
	super();
}
/**
 * LabelHeaderRenderer constructor comment.
 * @param text java.lang.String
 */
public LabelHeaderRenderer(String text) {
	super(text);
}
/**
 * LabelHeaderRenderer constructor comment.
 * @param text java.lang.String
 * @param horizontalAlignment int
 */
public LabelHeaderRenderer(String text, int horizontalAlignment) {
	super(text, horizontalAlignment);
}
/**
 * LabelHeaderRenderer constructor comment.
 * @param text java.lang.String
 * @param icon javax.swing.Icon
 * @param horizontalAlignment int
 */
public LabelHeaderRenderer(String text, javax.swing.Icon icon, int horizontalAlignment) {
	super(text, icon, horizontalAlignment);
}
/**
 * LabelHeaderRenderer constructor comment.
 * @param image javax.swing.Icon
 */
public LabelHeaderRenderer(javax.swing.Icon image) {
	super(image);
}
/**
 * LabelHeaderRenderer constructor comment.
 * @param image javax.swing.Icon
 * @param horizontalAlignment int
 */
public LabelHeaderRenderer(javax.swing.Icon image, int horizontalAlignment) {
	super(image, horizontalAlignment);
}
/**
 * getTableCellRendererComponent method comment.
 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
{
	setText( (value == null ) ? "" : value.toString() );

	return this;
}
}

package com.cannontech.common.gui.dnd;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Insert the type's description here.
 * Creation date: (2/12/2002 6:08:05 PM)
 * @author: 
 */

public class DAndDJTableRendererWrapper implements TableCellRenderer 
{
	private TableCellRenderer realRenderer = null;
	private int dragOverRow = -1;

	//the border for the bottom line to paint
	private static final javax.swing.border.MatteBorder BOTTOM_BORDER = 
		javax.swing.BorderFactory.createMatteBorder( 0, 0, 2, 0, new java.awt.Color(125,50,180) );

/**
 * DragAndDropJListRenderer constructor comment.
 */
public DAndDJTableRendererWrapper() {
	super();
}
/**
 * Return a component that has been configured to display the specified
 * value. That component's <code>paint</code> method is then called to
 * "render" the cell.  If it is necessary to compute the dimensions
 * of a list because the list cells do not have a fixed size, this method
 * is called to generate a component on which <code>getPreferredSize</code>
 * can be invoked.
 */
public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected, 
			boolean hasFocus, int row, int column)
{
	java.awt.Component c = realRenderer.getTableCellRendererComponent(
					table, value, isSelected, 
					hasFocus, row, column);

	if( c instanceof DefaultTableCellRenderer )
	{	
		if( row == dragOverRow )
		{
			((DefaultTableCellRenderer)c).setBorder( BOTTOM_BORDER );
		}
		else
			((DefaultTableCellRenderer)c).setBorder( 
						javax.swing.BorderFactory.createEmptyBorder() );
	}
	
	return c;	
}
/**
 * Insert the method's description here.
 * Creation date: (2/13/2002 12:11:03 PM)
 * @return javax.swing.ListCellRenderer
 */
public TableCellRenderer getRealRenderer() {
	return realRenderer;
}
/**
 * Insert the method's description here.
 * Creation date: (2/13/2002 12:07:32 PM)
 * @param rowNumber int
 */
public void setDragOverRow(int rowNumber) 
{
	dragOverRow = rowNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (2/13/2002 12:11:03 PM)
 * @param newRealRenderer javax.swing.ListCellRenderer
 */
public void setRealRenderer(TableCellRenderer newRealRenderer) {
	realRenderer = newRealRenderer;
}
}

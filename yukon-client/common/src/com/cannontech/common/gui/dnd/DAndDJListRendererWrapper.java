package com.cannontech.common.gui.dnd;

/**
 * Insert the type's description here.
 * Creation date: (2/12/2002 6:08:05 PM)
 * @author: 
 */

public class DAndDJListRendererWrapper implements javax.swing.ListCellRenderer 
{
	private javax.swing.ListCellRenderer realRenderer = null;
	private int dragOverRow = -1;

	//the border for the bottom line to paint
	private static final javax.swing.border.MatteBorder BOTTOM_BORDER = 
		javax.swing.BorderFactory.createMatteBorder( 0, 0, 2, 0, new java.awt.Color(125,50,180) );

/**
 * DragAndDropJListRenderer constructor comment.
 */
public DAndDJListRendererWrapper() {
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
public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
{

	java.awt.Component c = realRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

	if( c instanceof javax.swing.JLabel )
	{	
		if( index == dragOverRow )
		{
			((javax.swing.JLabel)c).setBorder( BOTTOM_BORDER );
		}
		else
			((javax.swing.JLabel)c).setBorder( javax.swing.BorderFactory.createEmptyBorder() );
	}
	
	return c;	
}
/**
 * Insert the method's description here.
 * Creation date: (2/13/2002 12:11:03 PM)
 * @return javax.swing.ListCellRenderer
 */
public javax.swing.ListCellRenderer getRealRenderer() {
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
public void setRealRenderer(javax.swing.ListCellRenderer newRealRenderer) {
	realRenderer = newRealRenderer;
}
}

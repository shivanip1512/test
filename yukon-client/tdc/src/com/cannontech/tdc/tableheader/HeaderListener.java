package com.cannontech.tdc.tableheader;

/**
 * Insert the type's description here.
 * Creation date: (4/4/00 12:37:27 PM)
 * @author: 
 * @Version: <version>
 */
public class HeaderListener extends java.awt.event.MouseAdapter 
{
	javax.swing.table.JTableHeader header = null;
	javax.swing.table.TableCellRenderer renderer = null;	
	
/**
 * HeaderListener constructor comment.
 */
public HeaderListener() {
	super();
}
/**
 * HeaderListener constructor comment.
 */
public HeaderListener( javax.swing.table.JTableHeader header, javax.swing.table.TableCellRenderer renderer ) 
{	
	super();

	this.header = header;
	this.renderer = renderer;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/00 12:38:55 PM)
 * Version: <version>
 * @param e java.awt.event.MouseEvent
 */
public void mousePressed(java.awt.event.MouseEvent e) 
{

	int col = header.columnAtPoint( e.getPoint() );

	if( renderer instanceof TextFieldHeaderRenderer )
	{
		((TextFieldHeaderRenderer)renderer).setEditingColumn( col );
	}
	else if( renderer instanceof ButtonHeaderRenderer )
	{
		((ButtonHeaderRenderer)renderer).setPressedColumn( col );
	}

		
	header.repaint();
}
}

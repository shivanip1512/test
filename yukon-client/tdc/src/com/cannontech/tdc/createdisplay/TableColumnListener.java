package com.cannontech.tdc.createdisplay;

/**
 * Insert the type's description here.
 * Creation date: (1/27/00 9:24:38 AM)
 * @author: 
 */
public class TableColumnListener implements javax.swing.event.TableColumnModelListener 
{
	Object caller = null;
/**
 * TableColumnListener constructor comment.
 */
public TableColumnListener() {
	super();
}
/**
 * TableColumnListener constructor comment.
 */
public TableColumnListener( Object call ) {
	super();

	caller = call;
}
/**
 * columnAdded method comment.
 */
public void columnAdded(javax.swing.event.TableColumnModelEvent e) 
{
	if ( caller instanceof CreateBottomPanel )
		((CreateBottomPanel)caller).jTableModel_ColumnAdded( e );	
}
/**
 * columnMarginChanged method comment.
 */
public void columnMarginChanged(javax.swing.event.ChangeEvent e) 
{	
}
/**
 * columnMoved method comment.
 */
public void columnMoved(javax.swing.event.TableColumnModelEvent e) 
{
	if ( caller instanceof CreateBottomPanel )
		((CreateBottomPanel)caller).jTableModel_ColumnMoved( e );	
}
/**
 * columnRemoved method comment.
 */
public void columnRemoved(javax.swing.event.TableColumnModelEvent e) {}
/**
 * columnSelectionChanged method comment.
 */
public void columnSelectionChanged(javax.swing.event.ListSelectionEvent e) 
{
}
}

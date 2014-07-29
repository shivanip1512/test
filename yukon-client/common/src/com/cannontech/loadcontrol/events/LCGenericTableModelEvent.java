package com.cannontech.loadcontrol.events;

/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 9:26:27 AM)
 * @author: 
 */
public class LCGenericTableModelEvent extends javax.swing.event.TableModelEvent 
{
	public static final int TYPE_FILTER_CHANGE = 754;
	public static final int TYPE_CLEAR = 755;	
/**
 * StateTableModelEvent constructor comment.
 * @param source javax.swing.table.TableModel
 */
public LCGenericTableModelEvent(javax.swing.table.TableModel source) {
	super(source);
}
/**
 * StateTableModelEvent constructor comment.
 * @param source javax.swing.table.TableModel
 */
public LCGenericTableModelEvent(javax.swing.table.TableModel source, int type ) 
{
	// Use Integer.MAX_VALUE instead of getRowCount() in case rows were deleted. 
	super(source, 0, Integer.MAX_VALUE, ALL_COLUMNS, type);
}
}

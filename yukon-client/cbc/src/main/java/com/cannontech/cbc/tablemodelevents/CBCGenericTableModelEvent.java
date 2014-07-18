package com.cannontech.cbc.tablemodelevents;

/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 9:26:27 AM)
 * @author: 
 */
public class CBCGenericTableModelEvent extends javax.swing.event.TableModelEvent 
{
	public static final int SUBBUS_FILTER_CHANGE = 0;
	public static final int SUBBUS_AREA_CHANGE = 1;
	private int changeid = SUBBUS_FILTER_CHANGE;
/**
 * StateTableModelEvent constructor comment.
 * @param source javax.swing.table.TableModel
 */
public CBCGenericTableModelEvent(javax.swing.table.TableModel source) {
	super(source);
}
/**
 * StateTableModelEvent constructor comment.
 * @param source javax.swing.table.TableModel
 */
public CBCGenericTableModelEvent(javax.swing.table.TableModel source, int changeID ) 
{
	super(source);
	setChangeid( changeID );
}
/**
 * StateTableModelEvent constructor comment.
 * @param source javax.swing.table.TableModel
 * @param firstRow int
 * @param lastRow int
 */
private CBCGenericTableModelEvent(javax.swing.table.TableModel source, int firstRow, int lastRow) {
	super(source, firstRow, lastRow);
}
/**
 * StateTableModelEvent constructor comment.
 * @param source javax.swing.table.TableModel
 * @param firstRow int
 * @param lastRow int
 * @param column int
 */
private CBCGenericTableModelEvent(javax.swing.table.TableModel source, int firstRow, int lastRow, int column) {
	super(source, firstRow, lastRow, column);
}
/**
 * StateTableModelEvent constructor comment.
 * @param source javax.swing.table.TableModel
 * @param firstRow int
 * @param lastRow int
 * @param column int
 * @param type int
 */
private CBCGenericTableModelEvent(javax.swing.table.TableModel source, int firstRow, int lastRow, int column, int type) {
	super(source, firstRow, lastRow, column, type);
}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 2:38:24 PM)
 * @return int
 */
public int getChangeid() {
	return changeid;
}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 2:38:24 PM)
 * @param newChangeid int
 */
public void setChangeid(int newChangeid) {
	changeid = newChangeid;
}
}

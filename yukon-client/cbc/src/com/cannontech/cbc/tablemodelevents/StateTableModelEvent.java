package com.cannontech.cbc.tablemodelevents;

/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 9:26:27 AM)
 * @author: 
 */
public class StateTableModelEvent extends javax.swing.event.TableModelEvent 
{
	private com.cannontech.database.db.state.State[] states = null;
/**
 * StateTableModelEvent constructor comment.
 * @param source javax.swing.table.TableModel
 */
public StateTableModelEvent(javax.swing.table.TableModel source) {
	super(source);
}
/**
 * StateTableModelEvent constructor comment.
 * @param source javax.swing.table.TableModel
 * @param row int
 */
public StateTableModelEvent(javax.swing.table.TableModel source, int row) {
	super(source, row);
}
/**
 * StateTableModelEvent constructor comment.
 * @param source javax.swing.table.TableModel
 * @param firstRow int
 * @param lastRow int
 */
public StateTableModelEvent(javax.swing.table.TableModel source, int firstRow, int lastRow) {
	super(source, firstRow, lastRow);
}
/**
 * StateTableModelEvent constructor comment.
 * @param source javax.swing.table.TableModel
 * @param firstRow int
 * @param lastRow int
 * @param column int
 */
public StateTableModelEvent(javax.swing.table.TableModel source, int firstRow, int lastRow, int column) {
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
public StateTableModelEvent(javax.swing.table.TableModel source, int firstRow, int lastRow, int column, int type) {
	super(source, firstRow, lastRow, column, type);
}
/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 9:26:57 AM)
 * @return com.cannontech.database.db.state.State[]
 */
public com.cannontech.database.db.state.State[] getStates() {
	return states;
}
/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 9:26:57 AM)
 * @param newStates com.cannontech.database.db.state.State[]
 */
public void setStates(com.cannontech.database.db.state.State[] newStates) {
	states = newStates;
}
}

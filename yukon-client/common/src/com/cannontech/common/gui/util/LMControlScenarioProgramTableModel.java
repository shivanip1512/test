/*
 * Created on Apr 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.common.gui.util;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMControlScenarioProgramTableModel extends AbstractTableModel 
{
	
	private Vector columnNames = new Vector();	
	private Vector rows = new Vector();
	/**
	 * This method was created in VisualAge.
	 */
	public LMControlScenarioProgramTableModel() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/00 2:54:45 PM)
	 * @param newRow java.util.Vector
	 */
	public void addRow(Vector newRow) {
		rows.addElement(newRow);
		fireTableRowsInserted(rows.size()-1,rows.size()-1);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/00 2:54:45 PM)
	 * @param newRow java.util.Vector
	 */
	public void editRow(int position, Vector newRow) {
		rows.remove(position);
		rows.add(position, newRow);
		fireTableRowsUpdated(position, position);
	}
	public int getColumnCount() {
		return columnNames.size();
	}
	/* Created
	*/
	public String getColumnName(int column) {
		return columnNames.elementAt(column).toString();
	}
//	   Data methods

	public int getRowCount() {
		return rows.size();
	}
	public Object getValueAt(int aRow, int aColumn)
	{
		Vector row = (Vector) rows.elementAt(aRow);
		return row.elementAt(aColumn);
	}
	/**
	 * This method was created in VisualAge.
	 */
	private void initColumns() 
	{	
		rows.removeAllElements();
		columnNames.removeAllElements();

		columnNames = new Vector( 4 );
		columnNames.addElement("Name");
		columnNames.addElement("Start Delay");
		columnNames.addElement("Duration");
		columnNames.addElement("Default Gear");

		fireTableStructureChanged(); // Tell the listeners a new table has arrived.
	}
	public boolean isCellEditable(int row, int column) {

		// make every cell non-editable
		return false;

	}
	/**
	 * This method creates the table
	 */
 
	public void makeTable ( )
	{
		initColumns();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/00 2:54:45 PM)
	 * @param newRow java.util.Vector
	 */
	public void removeRow(int position) {
		rows.remove(position);
		fireTableRowsDeleted(position, position);
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void reset() 
	{
		if ( columnNames != null )
			columnNames.removeAllElements();

		if ( rows != null )
			rows.removeAllElements();

		fireTableChanged(null);
	}
}

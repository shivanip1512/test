package com.cannontech.loadcontrol.gui;

/**
 * Insert the type's description here.
 * Creation date: (9/27/00 11:02:25 AM)
 * @author: 
 */
import com.cannontech.loadcontrol.datamodels.GroupTableModel;

public class GroupTable extends javax.swing.JTable 
{
/**
 * GroupTable constructor comment.
 */
public GroupTable() 
{
	super();
	initialize();
}
/**
 * GroupTable constructor comment.
 * @param rowData java.lang.Object[][]
 * @param columnNames java.lang.Object[]
 */
public GroupTable(java.lang.Object[][] rowData, java.lang.Object[] columnNames) {
	super(rowData, columnNames);
}
/**
 * GroupTable constructor comment.
 * @param numRows int
 * @param numColumns int
 */
public GroupTable(int numRows, int numColumns) {
	super(numRows, numColumns);
}
/**
 * GroupTable constructor comment.
 * @param rowData java.util.Vector
 * @param columnNames java.util.Vector
 */
public GroupTable(java.util.Vector rowData, java.util.Vector columnNames) {
	super(rowData, columnNames);
}
/**
 * GroupTable constructor comment.
 * @param dm javax.swing.table.TableModel
 */
public GroupTable(javax.swing.table.TableModel dm) {
	super(dm);
}
/**
 * GroupTable constructor comment.
 * @param dm javax.swing.table.TableModel
 * @param cm javax.swing.table.TableColumnModel
 */
public GroupTable(javax.swing.table.TableModel dm, javax.swing.table.TableColumnModel cm) {
	super(dm, cm);
}
/**
 * GroupTable constructor comment.
 * @param dm javax.swing.table.TableModel
 * @param cm javax.swing.table.TableColumnModel
 * @param sm javax.swing.ListSelectionModel
 */
public GroupTable(javax.swing.table.TableModel dm, javax.swing.table.TableColumnModel cm, javax.swing.ListSelectionModel sm) {
	super(dm, cm, sm);
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/00 10:58:15 AM)
 */
private void initialize() 
{
	setModel( new GroupTableModel() );
	setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
	setDefaultRenderer( Object.class, new LoadControlCellRenderer() );
	setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
	setGridColor( getTableHeader().getBackground() );
}
}

package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class JComponentTable extends javax.swing.JTable {
/**
 * JComponentTable constructor comment.
 */
public JComponentTable() {
	super();
}
/**
 * JComponentTable constructor comment.
 * @param rowData java.lang.Object[][]
 * @param columnNames java.lang.Object[]
 */
public JComponentTable(java.lang.Object[][] rowData, java.lang.Object[] columnNames) {
	super(rowData, columnNames);
}
/**
 * JComponentTable constructor comment.
 * @param numRows int
 * @param numColumns int
 */
public JComponentTable(int numRows, int numColumns) {
	super(numRows, numColumns);
}
/**
 * JComponentTable constructor comment.
 * @param rowData java.util.Vector
 * @param columnNames java.util.Vector
 */
public JComponentTable(java.util.Vector rowData, java.util.Vector columnNames) {
	super(rowData, columnNames);
}
/**
 * JComponentTable constructor comment.
 * @param dm javax.swing.table.TableModel
 */
public JComponentTable(javax.swing.table.TableModel dm) {
	super(dm);
}
/**
 * JComponentTable constructor comment.
 * @param dm javax.swing.table.TableModel
 * @param cm javax.swing.table.TableColumnModel
 */
public JComponentTable(javax.swing.table.TableModel dm, javax.swing.table.TableColumnModel cm) {
	super(dm, cm);
}
/**
 * JComponentTable constructor comment.
 * @param dm javax.swing.table.TableModel
 * @param cm javax.swing.table.TableColumnModel
 * @param sm javax.swing.ListSelectionModel
 */
public JComponentTable(javax.swing.table.TableModel dm, javax.swing.table.TableColumnModel cm, javax.swing.ListSelectionModel sm) {
	super(dm, cm, sm);
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.table.TableCellEditor
 * @param row int
 * @param column int
 */
public TableCellEditor getCellEditor(int row, int column) {
	 TableColumn tableColumn = getColumnModel().getColumn(column);
									TableCellEditor editor = tableColumn.getCellEditor();
									if (editor == null) {
											Class c = getColumnClass(column);
											if( c.equals(Object.class) )
											{
													Object o = getValueAt(row,column);
													if( o != null )
															c = getValueAt(row,column).getClass();
											}
											editor = getDefaultEditor(c);
									}
									return editor;
}
/**
 * This method was created in VisualAge.
 * @return TableCellRenderer
 * @param row int
 * @param column int
 */
public TableCellRenderer getCellRenderer(int row, int column) {
	 TableColumn tableColumn = getColumnModel().getColumn(column);
									TableCellRenderer renderer = tableColumn.getCellRenderer();
									if (renderer == null) {
											Class c = getColumnClass(column);
											if( c.equals(Object.class) )
											{
													Object o = getValueAt(row,column);
													if( o != null )
															c = getValueAt(row,column).getClass();
											}
											renderer = getDefaultRenderer(c);
									}
									return renderer;
}
}

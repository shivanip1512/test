package com.cannontech.loadcontrol.eexchange.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

public class RevisionHistoryTableModel extends javax.swing.table.AbstractTableModel implements com.cannontech.loadcontrol.datamodels.ISelectableLMTableModel
{
	//stores objects of type RevisionHistoryRowData
	java.util.Vector rows = null;
	
	private String currentView = null;
	private String fontName = "dialog";
	private int fontSize = 12;

	//The columns and their column index	
	public static final int OFFER_ID = 0;
	public static final int PROGRAM_NAME = 1;
	public static final int NOTIFY_TIME = 2;
	public static final int EXPIRE_TIME = 3;
	public static final int AMOUNT_COMMITTED = 4;
	public static final int AMOUNT_REQUESTED = 5;
  	
	//The column names based on their column index
	public static final String[] columnNames =
	{
		"Offer ID",
		"Program",
		"Notify Time",
		"Expire Time",
		"Committed Amount(MW)",
		"Amount Requested(MW)"
	};

/**
 * ScheduleTableModel constructor comment.
 */
public RevisionHistoryTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 10:06:14 AM)
 * @return boolean
 * @param rowValue com.cannontech.loadcontrol.eexchange.datamodels.RevisionHistoryRowData
 */
public boolean addRow(RevisionHistoryRowData rowValue) 
{
	for( int i = 0; i < getRowCount(); i++ )
		if( getRowAt(i).equals(rowValue) )
			return false;
			
	boolean added = getRows().add(rowValue);
	fireTableDataChanged();

	return added;
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
public void clear() 
{
	getRows().removeAllElements();

	fireTableDataChanged();	
}
/**
 * This method was created in VisualAge.
 * @param event TableModelEvent
 */
public void fireTableChanged(javax.swing.event.TableModelEvent event) {
	super.fireTableChanged(event);
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellBackgroundColor(int row, int col) 
{
	return Color.black;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellForegroundColor(int row, int col) 
{
	return Color.white;
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return columnNames.length;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param index int
 */
public String getColumnName(int index) {
	return columnNames[index];
}
/**
 * This method returns the value of a row in the form of a RevisionHistoryRowData object.
 */
public RevisionHistoryRowData getRow(int rowIndex) 
{
	return (RevisionHistoryRowData)getRowAt(rowIndex);
}
/**
 * This method returns the value of a row in the form of a RevisionHistoryRowData object.
 */
public Object getRowAt(int rowIndex) 
{
	if( rowIndex < 0 )
		return null;
		
	if( rowIndex < getRowCount() )
		return (RevisionHistoryRowData)getRows().get(rowIndex);
	else
		return null;
}
/**
 * getRowCount method comment.
 */
public int getRowCount() 
{
	return getRows().size();
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 9:46:42 AM)
 * @return java.util.Vector
 */
private java.util.Vector getRows() 
{
	if( rows == null )
		rows = new java.util.Vector( 20 );
	
	return rows;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 */
public java.awt.Color getSelectedRowColor(int row, int column)
{
	return Color.blue;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) 
{
	if( row <= getRowCount() && getRow(row) != null )
	{
		switch( col )
		{
		 	case OFFER_ID:
				return getRow(row).getOfferIDString();

		 	case PROGRAM_NAME:
		 		return getRow(row).getProgramName();
	
			case NOTIFY_TIME:
		 		return new com.cannontech.clientutils.commonutils.ModifiedDate( getRow(row).getNotifyTime().getTime() );
			
			case EXPIRE_TIME:
		 		return new com.cannontech.clientutils.commonutils.ModifiedDate( getRow(row).getExpireTime().getTime() );
				
			case AMOUNT_COMMITTED:
		 		return java.text.NumberFormat.getNumberInstance().format(getRow(row).getAmountCommitted().doubleValue() );
				
			case AMOUNT_REQUESTED:
		 		return java.text.NumberFormat.getNumberInstance().format(getRow(row).getAmountRequested().doubleValue() );

		 			
			default:
				return null;
		}
	}
	else
		return null;

}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param row int
 * @param column int
 */
public boolean isCellEditable(int row, int column) {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 10:08:28 AM)
 * @param newCurrentControlArea com.cannontech.loadcontrol.data.LMControlArea
 */
public synchronized void setCurrentControlArea(com.cannontech.loadcontrol.data.LMControlArea newCurrentControlArea) 
{
}
}

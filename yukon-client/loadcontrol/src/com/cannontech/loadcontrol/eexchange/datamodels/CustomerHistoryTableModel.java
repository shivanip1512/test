package com.cannontech.loadcontrol.eexchange.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

public class CustomerHistoryTableModel extends javax.swing.table.AbstractTableModel implements com.cannontech.loadcontrol.datamodels.SelectableLMTableModel
{
	//stores objects of type RevisionHistoryRowData
	java.util.Vector rows = null;
	
	private String currentView = null;
	private String fontName = "dialog";
	private int fontSize = 12;

	//The columns and their column index	
	public static final int CUSTOMER_NAME= 0;
	public static final int ACCEPT = 1;
  	public static final int TOTAL = 2;	
	public static final int ACCEPT_PERSON = 3;
	
	//The column names based on their column index
	public static final String[] columnNames =
	{
		"Customer",
		"Accept",
		"Total (kWh)",
		"Accept Person"
	};

/**
 * ScheduleTableModel constructor comment.
 */
public CustomerHistoryTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 10:06:14 AM)
 * @return boolean
 * @param rowValue com.cannontech.loadcontrol.eexchange.datamodels.CustomerHistoryRowData
 */
public boolean addRow(CustomerHistoryRowData rowValue) 
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
	if( getRow(row).getAccept().equalsIgnoreCase(
		    	com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply.STRING_ACCEPTED) )
		return Color.green;
	else if( getRow(row).getAccept().equalsIgnoreCase(
				com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply.STRING_DECLINED) )
		return Color.red;
	else
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
public CustomerHistoryRowData getRow(int rowIndex) 
{
	return (CustomerHistoryRowData)getRowAt(rowIndex);
}
/**
 * This method returns the value of a row in the form of a RevisionHistoryRowData object.
 */
public Object getRowAt(int rowIndex) 
{
	if( rowIndex < 0 )
		return null;
		
	if( rowIndex < getRowCount() )
		return (CustomerHistoryRowData)getRows().get(rowIndex);
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
	if( row <= getRowCount() )
	{
		switch( col )
		{
		 	case CUSTOMER_NAME:
				return getRow(row).getCustomerName();

		 	case ACCEPT:
		 		return getRow(row).getAccept();
	
			case TOTAL:
		 		return java.text.NumberFormat.getNumberInstance().format(getRow(row).getTotal().doubleValue() );

			case ACCEPT_PERSON:
		 		return getRow(row).getAcceptPerson();
			
		 			
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

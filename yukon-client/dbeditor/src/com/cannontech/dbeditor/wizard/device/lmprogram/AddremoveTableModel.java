package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * Insert the type's description here.
 * Creation date: (5/7/2001 10:49:03 AM)
 * @author: 
 */
public class AddremoveTableModel extends javax.swing.table.AbstractTableModel implements com.cannontech.common.gui.util.AddRemoveJTableModel 
{
	public static final String[] COLUMNS =
	{
		"Name",
		"Ack Required"
	};

	private java.util.Vector rows = null;

	public class RowValue
	{
		public com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList customer = null;
		public Boolean reqAck = new Boolean(false);

		public RowValue( com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList custList )
		{
			customer = custList;
		};
		
		public String toString()
		{
			if( customer != null )
				return customer.toString();
			else
				return "(null)";
		}
	}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 10:49:03 AM)
 * @param obj java.lang.Object
 */
public void addRow(Object obj) 
{

	//if we removed the row and then re-added it, we will already 
	//  have the RowValue object created!! Spunky stunt!
	if( obj instanceof RowValue )
		getRows().add( (RowValue)obj );
	else if( obj instanceof com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList )
	{
		RowValue row = new RowValue( (com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList)obj );
		getRows().add( row );
	}
	else
		com.cannontech.clientutils.CTILogger.info("*** WRONG : " + obj.getClass().getName() );
	//{
		//RowValue row = new RowValue( (com.cannontech.database.data.device.customer.CustomerBase)obj );
		//getRows().add( row );
	//}

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 10:49:03 AM)
 * @param obj java.lang.Object
 */
public void addRow(Object obj, String reqAck ) 
{

	//if we removed the row and then re-added it, we will already 
	//  have the RowValue object created!! Spunky stunt!
	if( obj instanceof RowValue )
		getRows().add( (RowValue)obj );
	else if( obj instanceof com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList )
	{		
		RowValue row = new RowValue( (com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList)obj );
		row.reqAck = (reqAck.equalsIgnoreCase("Y") ? new Boolean(true) : new Boolean(false) );
		getRows().add( row );
	}
	else
		com.cannontech.clientutils.CTILogger.info("*** WRONG : " + obj.getClass().getName() );
/*	{
		RowValue row = new RowValue( (com.cannontech.database.data.device.customer.CustomerBase)obj );
		row.reqAck = (reqAck.equalsIgnoreCase("Y") ? new Boolean(true) : new Boolean(false) );
		getRows().add( row );
	}
*/
	fireTableDataChanged();
}
	/**
	 * Returns the number of columns managed by the data source object. A
	 * <B>JTable</B> uses this method to determine how many columns it
	 * should create and display on initialization.
	 *
	 * @return the number or columns in the model
	 * @see #getRowCount
	 */
public int getColumnCount() {
	return COLUMNS.length;
}
	/**
	 * Returns the name of the column at <i>columnIndex</i>.  This is used
	 * to initialize the table's column header name.  Note, this name does
	 * not need to be unique.  Two columns on a table can have the same name.
	 *
	 * @param	columnIndex	the index of column
	 * @return  the name of the column
	 */
public String getColumnName(int columnIndex) {
	return COLUMNS[columnIndex];
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 10:49:03 AM)
 * @return java.lang.Object
 * @param index int
 */
public Object getRowAt(int index) {
	return getRows().get(index);
}
/**
 * Insert the method's description here.
 * Creation date: (5/14/2001 11:34:45 AM)
 * @return java.lang.Class
 */
public Class getRowClass()
{
	return RowValue.class;
}
	/**
	 * Returns the number of records managed by the data source object. A
	 * <B>JTable</B> uses this method to determine how many rows it
	 * should create and display.  This method should be quick, as it
	 * is call by <B>JTable</B> quite frequently.
	 *
	 * @return the number or rows in the model
	 * @see #getColumnCount
	 */
public int getRowCount() {
	return getRows().size();
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 10:53:37 AM)
 * @return java.util.Vector
 */
private java.util.Vector getRows() 
{
	if( rows == null )
		rows = new java.util.Vector();

	return rows;
}
	/**
	 * Returns an attribute value for the cell at <I>columnIndex</I>
	 * and <I>rowIndex</I>.
	 *
	 * @param	rowIndex	the row whose value is to be looked up
	 * @param	columnIndex 	the column whose value is to be looked up
	 * @return	the value Object at the specified cell
	 */
public Object getValueAt(int rowIndex, int columnIndex) 
{
	RowValue row = (RowValue)getRowAt(rowIndex);
	
	if( columnIndex == 0 )
		return row.toString();
	else if( columnIndex == 1 )
		return row.reqAck;
	else
		return null;
}
	/**
	 * Returns true if the cell at <I>rowIndex</I> and <I>columnIndex</I>
	 * is editable.  Otherwise, setValueAt() on the cell will not change
	 * the value of that cell.
	 *
	 * @param	rowIndex	the row whose value is to be looked up
	 * @param	columnIndex	the column whose value is to be looked up
	 * @return	true if the cell is editable.
	 * @see #setValueAt
	 */
public boolean isCellEditable(int rowIndex, int columnIndex) 
{
	//only column 1 is Editable
	return columnIndex == 1;
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 10:49:03 AM)
 * @param index int
 */
public void removeRow(int index) 
{
	getRows().remove(index);

	fireTableDataChanged();
}
/**
 * setValueAt method comment.
 */
// THIS METHOD IS NEEDED SINCE I AM USING A DEFAULTCELLEDITOR AND A CELLRENDERRER
//   THAT I CREATED, WHEN AN EDITING OCCURS, THIS METHOD IS CALLED.
public void setValueAt(Object value, int row, int col) 
{
	if( row <= getRows().size() && col < getColumnCount() )
	{
		switch( col )
		{
		 	case 0:	
				((RowValue)getRowAt(row)).customer.setCustomerName( value.toString() );
				break;
				
			case 1:
				((RowValue)getRowAt(row)).reqAck = (Boolean)value;
				break;
				
			default:
				com.cannontech.clientutils.CTILogger.info(this.getClass() + " tried to set value for an invalid column, column number " + col );
		}
	}
}
}

package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * Insert the type's description here.
 * Creation date: (5/7/2001 10:49:03 AM)
 * @author: 
 */
public class GearTableModel extends javax.swing.table.AbstractTableModel implements com.cannontech.common.gui.util.AddRemoveJTableModel 
{
	public static final String[] COLUMNS =
	{
		"Gear Name",
		"Method",
		"Reduction %"
	};

	private java.util.Vector rows = null;

	public class RowValue
	{
		public com.cannontech.database.db.device.lm.LMProgramDirectGear gear = null;

		public RowValue( com.cannontech.database.db.device.lm.LMProgramDirectGear dirGear )
		{
			gear = dirGear;
		};
		
		public String toString()
		{
			if( gear != null )
				return gear.getGearName();
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
	else if( obj instanceof com.cannontech.database.db.device.lm.LMProgramDirectGear )
	{
		RowValue row = new RowValue( (com.cannontech.database.db.device.lm.LMProgramDirectGear)obj );
		getRows().add( row );
	}
	else
		com.cannontech.clientutils.CTILogger.info("*** WRONG CLASS @addRow(Object) : " + obj.getClass().getName() + " in : " + this.getClass().getName() );

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2001 11:16:42 AM)
 * @return java.lang.String[]
 */
public String[] getAllGearNames() 
{
	String[] names = new String[ getRowCount() ];
	
	for( int i = 0; i < getRowCount(); i++ )
		names[i] = ((RowValue)getRowAt(i)).gear.getGearName();
		
	return names;
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
 * @return com.cannontech.database.db.device.lm.LMProgramDirectGear
 * @param index int
 */
public com.cannontech.database.db.device.lm.LMProgramDirectGear getGearAt(int index) 
{
	return ((RowValue)getRows().get(index)).gear;
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
		return row.gear.getControlMethod();
	else if( columnIndex == 2 )
		return row.gear.getPercentReduction();		
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
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 10:49:03 AM)
 * @param obj java.lang.Object
 */
public void removeRow(int row) 
{
	if( row >= 0 && row < getRowCount() )
	{
		getRows().remove( row );
		fireTableDataChanged();
	}


}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 10:49:03 AM)
 * @param obj java.lang.Object
 */
public void setRowAt( Object obj, int rowIndex ) 
{
	if( obj instanceof RowValue )
		getRows().setElementAt( (RowValue)obj, rowIndex );
	else if( obj instanceof com.cannontech.database.db.device.lm.LMProgramDirectGear )
	{
		RowValue row = new RowValue( (com.cannontech.database.db.device.lm.LMProgramDirectGear)obj );
		getRows().setElementAt( row, rowIndex );
	}
	else
		com.cannontech.clientutils.CTILogger.info("*** WRONG CLASS @setRowAt(Object, int): " + obj.getClass().getName() + " in : " + this.getClass().getName() );

	fireTableDataChanged();
}
/**
 * setValueAt method comment.
 */
// THIS METHOD IS NEEDED SINCE I AM USING A DEFAULTCELLEDITOR AND A CELLRENDERRER
//   THAT I CREATED, WHEN AN EDITING OCCURS, THIS METHOD IS CALLED.
public void setValueAt(Object value, int row, int col) 
{
	super.setValueAt( value, row, col );

/*
	if( row <= getRows().size() && col < getColumnCount() )
	{
		switch( col )
		{
		 	case 0:	
				((RowValue)getRowAt(row)).gear.setGearName( value.toString() );
				break;
				
			case 1:
				((RowValue)getRowAt(row)).reqAck = (Boolean)value;
				break;
				
			default:
				com.cannontech.clientutils.CTILogger.info(this.getClass() + " tried to set value for an invalid column, column number " + col );
		}
	}
*/
}
}

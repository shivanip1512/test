package com.cannontech.palm.client;

/**
 * Insert the type's description here.
 * Creation date: (5/31/2001 11:52:39 AM)
 * @author: Eric Schmit
 */
public class AddressesTableModel extends javax.swing.table.AbstractTableModel
{
	private java.util.Vector rows = null;

	public static final String[] COLUMNS = { "Definition", "Section", "Class", "Division" };
	public static final int COLUMN_DEF = 0;
	public static final int COLUMN_SEC = 1;
	public static final int COLUMN_CLASS = 2;
	public static final int COLUMN_DIV = 3;

	private LCR_Address lcrAddress = new LCR_Address();
/**
 * AltAddressTableModel constructor comment.
 */
public AddressesTableModel()
{
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/6/2001 2:51:42 PM)
 */
public void addRow(LCR_Address newAddress) 
{
	getRows().add(newAddress);
	System.out.println ("addRow_OK");
	fireTableDataChanged();

	System.out.println ( "size = " + getRows().size() );
}
public int getColumnCount() 
{
	return 4;
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:35:03 PM)
 * @return java.lang.String
 */
public String getColumnName(int index) 
{
	return COLUMNS [index];
}
public Object getLCRAttributes(int row, int col, java.util.Vector storedVector) 
{
	LCR_Address storedAddress = new LCR_Address();
	storedAddress = (LCR_Address)storedVector.elementAt(row);
	
	switch(col)
	{
		case COLUMN_DEF:
			return storedAddress.getAddressName();

		case COLUMN_SEC:
		{
			Integer value = new Integer(storedAddress.getAddressSection());
			return value;
		}
		
		case COLUMN_CLASS:
		{
			Integer value = new Integer(storedAddress.getAddressClass());
			return value;
		}
		
		case COLUMN_DIV:
		{
			Integer value = new Integer(storedAddress.getAddressDivision());
			return value;
		}
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 4:51:08 PM)
 * @param rowNumber int
 */
public LCR_Address getRowAt(int rowNumber) 
{
	if( (rowNumber >= 0) && (rowNumber < getRowCount()) )
		return (LCR_Address)getRows().elementAt(rowNumber);
	else
		return null;
}
public int getRowCount() 
{
	return getRows().size();
} 
public java.util.Vector getRows() 
{
	if(rows == null)
		rows = new java.util.Vector();//(10);
		
	return rows;
}
public java.util.Vector getRows (int row) 
{
	if(rows == null)
		rows = new java.util.Vector(row);//(10);
		
	return rows;
}
//not sure this is needed at all
public Object getTypeAddress()
{
	return rows.elementAt(0);
}
public Object getValueAt(int row, int col) 
{
	if( (row < getRows().size()) && (col < getColumnCount()) )
	{
		return getLCRAttributes(row, col, rows);
	}
	else
		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:22:36 PM)
 * @return boolean
 */
public boolean isCellEditable()  
{
	return false;
}
public void removeRow(int rowNumber)
{
	getRows().remove(rowNumber);

	fireTableDataChanged();
}
}

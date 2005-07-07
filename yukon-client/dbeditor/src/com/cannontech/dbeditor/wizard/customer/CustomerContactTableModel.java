package com.cannontech.dbeditor.wizard.customer;

import com.cannontech.common.gui.dnd.IDroppableTableModel;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteContact;

/**
 * Insert the type's description here.
 * Creation date: (3/19/2001 4:37:05 PM)
 * @author: 
 */
public class CustomerContactTableModel extends javax.swing.table.AbstractTableModel implements IDroppableTableModel 
{
	private java.util.Vector rows = null;

	public static final String[] COLUMNS = 
	{ 
		"Name", 
		"Yukon Login",
		"Notification"
	};
	

	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_LOGIN = 1;
	public static final int COLUMN_NOTIFICATION = 2;

/*
	public class CustomerContactRow
	{
		private com.cannontech.database.data.customer.Contact customer = null;
		private String firstName = null;
		private String lastName = null;
		private String phone1 = null;
		private String phone2 = null;
		private com.cannontech.database.data.lite.LiteContactNotification liteRecipient = null;
		private boolean isPrimeContact = false;
		
		public CustomerContactRow(String nameFirst, String nameLast, String phoneOne, String phoneTwo, com.cannontech.database.data.lite.LiteContactNotification ltRecipient )
		{
			super();

			firstName = nameFirst;
			lastName = nameLast;
			phone1 = phoneOne;
			phone2 = phoneTwo;
			liteRecipient = ltRecipient;
		}

		//use this constructor if you have a CustomerContact already
		public CustomerContactRow( com.cannontech.database.data.customer.Contact customerContact, com.cannontech.database.data.lite.LiteContactNotification ltRecipient )
		{
			super();
			customer = customerContact;
			
			firstName = customer.getContact().getContFirstName();
			lastName = customer.getContact().getContLastName();
			phone1 = customer.getContact().getContPhone1();
			phone2 = customer.getContact().getContPhone2();
			liteRecipient = ltRecipient;
		}
		
		public String getFirstName()
		{
			return firstName;
		}
		public String getLastName()
		{
			return lastName;
		}
		public String getPhone1()
		{
			return phone1;
		}
		public String getPhone2()
		{
			return phone2;
		}
		public void setPrimeContact(boolean isPrime)
		{
			isPrimeContact = isPrime;
		}
		public boolean isPrimeContact()
		{
			return isPrimeContact;
		}
		public com.cannontech.database.data.customer.Contact getCustomer()
		{
			return customer;
		}

		public com.cannontech.database.data.lite.LiteContactNotification getLiteRecipient()
		{
			return liteRecipient;
		}
	};
*/

	/**
	 * CustomerContactTableModel constructor comment.
	 */
	public CustomerContactTableModel() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/19/2001 4:57:45 PM)
	 * @param type java.lang.String
	 * @param point com.cannontech.database.data.lite.LitePoint
	 * @param val java.lang.Object
	 */
	public void addRow( LiteContact liteContact )
	{
		if( !getRows().contains(liteContact) )
			getRows().addElement( liteContact );
	
		fireTableDataChanged();
	}
	/**
	 * getColumnCount method comment.
	 */
	public int getColumnCount() 
	{
		return COLUMNS.length;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/21/2001 10:25:01 AM)
	 * @return java.lang.String
	 */
	public String getColumnName(int loc) 
	{
		return COLUMNS[loc];
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/19/2001 4:51:08 PM)
	 * @param rowNumber int
	 */
	public LiteContact getLiteContactAt(int rowNumber) 
	{
		if( rowNumber >= 0 && rowNumber < getRowCount() )
			return (LiteContact)getRows().elementAt(rowNumber);
		else
			return null;
	}
	
	public void insertNewRow( Object liteContact ) {
		if( liteContact instanceof LiteContact)
			addRow( (LiteContact)liteContact );
		
		fireTableDataChanged();
	}

	public boolean objectExists( Object liteContact ) {
		return getRows().contains( liteContact );
	}

	public Object getRowAt( int row ) {
		return getLiteContactAt( row );
	}
	
	public void insertRowAt( Object value, int row ) {
		if( value instanceof LiteContact)
			getRows().insertElementAt( value, row );
		
		fireTableDataChanged();	
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
	 * Creation date: (3/19/2001 4:39:14 PM)
	 * @return java.util.Vector
	 */
	private java.util.Vector getRows() 
	{
		if( rows == null )
			rows = new java.util.Vector(10);
			
		return rows;
	}
	/**
	 * getValueAt method comment.
	 */
	public Object getValueAt(int row, int col) 
	{
		if( row >= getRowCount() || col >= getColumnCount() )
			return null;
	
		LiteContact con = getLiteContactAt(row);
		switch( col )
		{
			case COLUMN_NAME:
				return con.getContLastName() + ", " 
					+ con.getContFirstName();
	
			//may be a performance hit doing this every time after each refresh!!??
			case COLUMN_LOGIN:
				return YukonUserFuncs.getLiteYukonUser(
								con.getLoginID()).getUsername();
	
			case COLUMN_NOTIFICATION:
				return con.getNotifactionsStrings();
		}
		
		return null;
	}
	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 * @param row int
	 * @param column int
	 */
	public boolean isCellEditable(int row, int column) 
	{
		return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/19/2001 4:57:45 PM)
	 * @param type int
	 */
	public void removeRow( int rowNumber )
	{
		getRows().remove( rowNumber );
		fireTableDataChanged();
	}

}

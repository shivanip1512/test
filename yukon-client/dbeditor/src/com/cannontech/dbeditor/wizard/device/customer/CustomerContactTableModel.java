package com.cannontech.dbeditor.wizard.device.customer;

/**
 * Insert the type's description here.
 * Creation date: (3/19/2001 4:37:05 PM)
 * @author: 
 */
public class CustomerContactTableModel extends javax.swing.table.AbstractTableModel 
{
	private java.util.Vector rows = null;

	public static final String[] COLUMNS = { "Name", "Phone 1", "Phone 2", "Recipient" };
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_PHONE1 = 1;
	public static final int COLUMN_PHONE2 = 2;
	public static final int COLUMN_RECIPIENT = 3;
	//public static final int COLUMN_PRIME_CONTACT = 4;

	public class CustomerContactRow
	{
		private com.cannontech.database.data.customer.CustomerContact customer = null;
		private String firstName = null;
		private String lastName = null;
		private String phone1 = null;
		private String phone2 = null;
		private com.cannontech.database.data.lite.LiteNotificationRecipient liteRecipient = null;
		private boolean isPrimeContact = false;
		
		public CustomerContactRow(String nameFirst, String nameLast, String phoneOne, String phoneTwo, com.cannontech.database.data.lite.LiteNotificationRecipient ltRecipient )
		{
			super();

			firstName = nameFirst;
			lastName = nameLast;
			phone1 = phoneOne;
			phone2 = phoneTwo;
			liteRecipient = ltRecipient;
		}

		//use this constructor if you have a CustomerContact already
		public CustomerContactRow( com.cannontech.database.data.customer.CustomerContact customerContact, com.cannontech.database.data.lite.LiteNotificationRecipient ltRecipient )
		{
			super();
			customer = customerContact;
			
			firstName = customer.getCustomerContact().getContFirstName();
			lastName = customer.getCustomerContact().getContLastName();
			phone1 = customer.getCustomerContact().getContPhone1();
			phone2 = customer.getCustomerContact().getContPhone2();
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
		public com.cannontech.database.data.customer.CustomerContact getCustomer()
		{
			return customer;
		}

		public com.cannontech.database.data.lite.LiteNotificationRecipient getLiteRecipient()
		{
			return liteRecipient;
		}
	};
/**
 * TriggerTableModel constructor comment.
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
public void addRow(String firstName, String lastName, String phone1, String phone2, com.cannontech.database.data.lite.LiteNotificationRecipient rec )
{
	CustomerContactRow row = new CustomerContactRow( firstName, lastName, phone1, phone2, rec );

	getRows().addElement( row );

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
public CustomerContactRow getRowAt(int rowNumber) 
{
	if( rowNumber >= 0 && rowNumber < getRowCount() )
		return (CustomerContactRow)getRows().elementAt(rowNumber);
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

	switch( col )
	{
		case COLUMN_NAME:
		return getRowAt(row).getLastName() + ", " + getRowAt(row).getFirstName();

		case COLUMN_PHONE1:
		return getRowAt(row).getPhone1();

		case COLUMN_PHONE2:
		return getRowAt(row).getPhone2();

		case COLUMN_RECIPIENT:
		return getRowAt(row).getLiteRecipient();

		//case COLUMN_PRIME_CONTACT:
		//return new Boolean(getRowAt(row).isPrimeContact());

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
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 9:42:19 AM)
 * @param custContact com.cannontech.database.db.device.customer.CustomerContact
 */
public void setCustomerContactRow(com.cannontech.database.data.customer.CustomerContact custContact, com.cannontech.database.data.lite.LiteNotificationRecipient ltRecipient ) 
{

	if( custContact != null )
	{
		CustomerContactRow row = new CustomerContactRow( custContact, ltRecipient );
		getRows().addElement( row );
		fireTableDataChanged();
	}
	
}
}

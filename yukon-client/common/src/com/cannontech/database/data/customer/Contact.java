package com.cannontech.database.data.customer;

import java.util.Vector;

import com.cannontech.database.Transaction;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.customer.Address;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.database.db.NestedDBPersistentComparators;

/**
 * This type was created in VisualAge.
 */

public class Contact extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel, IAddress
{
	private com.cannontech.database.db.contact.Contact customerContact = null;
	
	private Address address = null;
	
	
	//contains com.cannontech.database.db.contact.ContactNotification
	private Vector contactNotifVect = null;


	/**
	 * Contact constructor comment.
	 */
	public Contact() {
		super();
	}
	/**
	 * StatusPoint constructor comment.
	 */
	public Contact(Integer contactID) {
		super();
		setContactID( contactID );
	}
	/**
	 * This method was created in VisualAge.
	 * @exception java.sql.SQLException The exception description.
	 */
	public void add() throws java.sql.SQLException 
	{
		//be sure all DB objects have this ID set
		if( getContact().getContactID() == null )
			setContactID( com.cannontech.database.db.contact.Contact.getNextContactID(getDbConnection()) ); 

		//be sure all or our objects share the same contactID 
		setContactID( getContact().getContactID() ); 
		
		
		getAddress().add();		
		getContact().setAddressID( getAddress().getAddressID() );
		
		
		getContact().add();
		
		for( int i = 0; i < getContactNotifVect().size(); i++ )
		{
			((DBPersistent)getContactNotifVect().get(i)).add();
		}		
	}


	/**
	 * This method was created in VisualAge.
	 * @param pointID java.lang.Integer
	 */
	public final static boolean isUsedInPointAlarming(Integer contactID, String databaseAlias) throws java.sql.SQLException 
	{
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement(
				"SELECT RecipientID FROM " + 
				PointAlarming.TABLE_NAME + 
				" WHERE RecipientID=" + contactID,
				databaseAlias );
	
		try
		{
			stmt.execute();
			return (stmt.getRowCount() > 0 );
		}
		catch( Exception e )
		{
			return false;
		}
	}

	/** 
	 * This method was created in VisualAge.
	 * @exception java.sql.SQLException The exception description.
	 */
	public void delete() throws java.sql.SQLException 
	{
		ContactNotification.deleteAllContactNotifications(
				getDbConnection(),		 
				getContact().getContactID().intValue() );
	
	
		getAddress().setAddressID( 
				getContact().getAddressID() );
	
		delete("ContactNotifGroupMap", "ContactID", getContact().getContactID());

		getContact().delete();

		if (getAddress().getAddressID().intValue() != com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID)
			getAddress().delete();	
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (2/3/2003 11:16:45 AM)
	 * @return java.util.Vector
	 */
	public java.util.Vector getContactNotifVect() 
	{
		if( contactNotifVect == null )
			contactNotifVect = new Vector(8);
	
		return contactNotifVect;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2001 11:12:12 AM)
	 * @return com.cannontech.database.db.customer.CustomerContact
	 */
	public com.cannontech.database.db.contact.Contact getContact() 
	{
		if( customerContact == null )
			customerContact = new com.cannontech.database.db.contact.Contact();
		
		return customerContact;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2001 11:12:12 AM)
	 * @return Address
	 */
	public Address getAddress() 
	{
		if( address == null )
			address = new Address();
		
		return address;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/19/2001 1:45:25 PM)
	 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
	 */
	public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
	{
		com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
		{
			new com.cannontech.message.dispatch.message.DBChangeMsg(
						getContact().getContactID().intValue(),
						com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_CONTACT_DB,
						com.cannontech.message.dispatch.message.DBChangeMsg.CAT_CUSTOMERCONTACT,
						com.cannontech.message.dispatch.message.DBChangeMsg.CAT_CUSTOMERCONTACT,
						typeOfChange)
		};
	
	
		return msgs;
	}
	/**
	 * This method was created in VisualAge.
	 * @exception java.sql.SQLException The exception description.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		getContact().retrieve();
	
	
		getAddress().setAddressID( getContact().getAddressID() );
		getAddress().retrieve();

	
		ContactNotification[] cntNotifs =
			ContactNotification.getContactNotifications(
					getDbConnection(), 
					getContact().getContactID().intValue() );

		for( int i = 0; i < cntNotifs.length; i++ )
		{
			getContactNotifVect().add( cntNotifs[i] );
		}
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void setContactID(Integer contactID) 
	{
		getContact().setContactID(contactID);
		
		for( int i = 0; i < getContactNotifVect().size(); i++ )
		{
			((com.cannontech.database.db.contact.ContactNotification)
					getContactNotifVect().get(i)).setContactID( contactID );
		}		
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/3/2001 11:12:12 AM)
	 * @param newCustomerContact com.cannontech.database.db.customer.CustomerContact
	 */
	public void setCustomerContact(com.cannontech.database.db.contact.Contact newCustomerContact) {
		customerContact = newCustomerContact;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/00 3:32:03 PM)
	 * @param conn java.sql.Connection
	 */
	public void setDbConnection(java.sql.Connection conn) 
	{
		super.setDbConnection(conn);

		getAddress().setDbConnection(conn);	
	
		getContact().setDbConnection(conn);
		
		for( int i = 0; i < getContactNotifVect().size(); i++ )
		{
			((DBPersistent)getContactNotifVect().get(i)).setDbConnection( conn );
		}		
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public String toString() 
	{
		if( getContact() != null )
			return getContact().getContLastName() + ", " + getContact().getContFirstName();
		else
			return null;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @exception java.sql.SQLException The exception description.
	 */
	public void update() throws java.sql.SQLException 
	{
		getAddress().setAddressID( getContact().getAddressID() );
		getAddress().update();
	
		//be sure all or our objects share the same contactID 
		setContactID( getContact().getContactID() ); 

		getContact().update();

		//grab all the previous gear entries for this program
		java.util.Vector oldContactNotifies = ContactNotification.getContactNotifications(this.getContact().getContactID().intValue(), getDbConnection());
	
		//run all the ContactNotifications through the NestedDBPersistent comparator
		//to see which ones need to be added, updated, or deleted.
		Vector newVect = NestedDBPersistentComparators.NestedDBPersistentCompare(oldContactNotifies, getContactNotifVect(), NestedDBPersistentComparators.contactNotificationComparator);
	
		//throw the gears into the Db
		for( int i = 0; i < newVect.size(); i++ )
		{
			((NestedDBPersistent)newVect.elementAt(i)).setDbConnection(getDbConnection());
			((NestedDBPersistent)newVect.elementAt(i)).executeNestedOp();
		}
		
	}


	/**
	 * This method was created in VisualAge.
	 * @param pointID java.lang.Integer
	 */
	public final static boolean isPrimaryContact(Integer contactID, String databaseAlias) throws java.sql.SQLException 
	{
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement(
				"SELECT PrimaryContactID FROM " + 
				Customer.TABLE_NAME + 
				" WHERE PrimaryContactID=" + contactID,
				databaseAlias );
	
		try
		{
			stmt.execute();
			return (stmt.getRowCount() > 0 );
		}
		catch( Exception e )
		{
			return false;
		}
	}

	/**
	 * Sets the address.
	 * @param address The address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

}

package com.cannontech.database.data.customer;

import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.customer.DeviceCustomerList;
import com.cannontech.database.db.graph.GraphCustomerList;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * This type was created in VisualAge.
 */
public class Customer extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange
{	
	private com.cannontech.database.db.customer.Customer customer = null;
	
	//contains com.cannontech.database.db.graph.GraphCustomerList
	private java.util.Vector graphVector = null;

	//contains com.cannontech.database.db.customer.DeviceCustomerList
	private java.util.Vector deviceVector = null;

	//contains ints of ContactIDs
	private int[] customerContactIDs = new int[0];
	
	
	/**
	 * Customer constructor comment.
	 */
	public Customer() 
	{
		this( new Integer(CustomerTypes.CUSTOMER_RESIDENTIAL) );
	}

	/**
	 * Customer constructor comment.
	 */
	protected Customer( Integer typeID_ ) 
	{
		super();
		getCustomer().setCustomerTypeID( typeID_ );
	}

	/**
	 * Insert the method's description here.
	 * @return java.util.Vector
	 */
	public int[] getCustomerContactIDs() 
	{
		return customerContactIDs;
	}

	public void setCustomerContactIDs( int[] ids_ )
	{
		customerContactIDs = ids_;
	}

	public void setCustomer( com.cannontech.database.db.customer.Customer customer_ ) {
		customer = customer_;
	}

	public com.cannontech.database.db.customer.Customer getCustomer()
	{
		if( customer == null )
			customer = new com.cannontech.database.db.customer.Customer();
		
		return customer;
	}
	

	public void setCustomerID( Integer cstID_ )
	{
		getCustomer().setCustomerID( cstID_ );
	}
	
	public Integer getCustomerID()
	{
		return getCustomer().getCustomerID();
	}

	/**
	 * This method was created in VisualAge.
	 * @exception java.sql.SQLException The exception description.
	 */
	public void add() throws java.sql.SQLException 
	{
		if( getCustomerID() == null )
			setCustomerID( 
				com.cannontech.database.db.customer.Customer.getNextCustomerID(getDbConnection()) );
				

		getCustomer().add();
		
		// add all the contacts for this customers
		if( getCustomerContactIDs() != null )
		{
			for( int i = 0; i < getCustomerContactIDs().length; i++ )
			{
				Object addValues[] = 
				{ 	
					getCustomer().getCustomerID(), 
					new Integer( getCustomerContactIDs()[i] )
				};
	
				//just add the bridge value to the mapping table
				// showing that this contact belongs to the current customer 
				add("CustomerAdditionalContact", addValues);
			}
		}

		for (int i = 0; i < getDeviceVector().size(); i++)
			 ((com.cannontech.database.db.DBPersistent) getDeviceVector().elementAt(i)).add();			
		
	}
	
	/** 
	 * This method was created in VisualAge.
	 * @exception java.sql.SQLException The exception description.
	 */
	public void delete() throws java.sql.SQLException 
	{
		// delete all the relations from a graph to this customer
		com.cannontech.database.db.customer.Customer.deleteCustomerGraphList(
				getCustomerID(), getDbConnection() );

		// delete all the relations from a device to this customer
		com.cannontech.database.db.customer.Customer.deleteCustomerDeviceList(
				getCustomerID(), getDbConnection() );
		
		
		// delete all the contacts for this customer
		com.cannontech.database.db.contact.Contact.deleteAllAdditionalContacts(
				getCustomerID(), getDbConnection() );
		
		getCustomer().delete();	
	}

	public void setDbConnection( java.sql.Connection conn )
	{
		super.setDbConnection( conn );
		getCustomer().setDbConnection(conn);

		for (int i = 0; i < getGraphVector().size(); i++)
			 ((com.cannontech.database.db.DBPersistent) getGraphVector().elementAt(i)).setDbConnection(conn);
			 			
		for (int i = 0; i < getDeviceVector().size(); i++)
			 ((com.cannontech.database.db.DBPersistent) getDeviceVector().elementAt(i)).setDbConnection(conn);			

	}
	
	/**
	 * This method was created in VisualAge.
	 * @exception java.sql.SQLException The exception description.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		if (getCustomer().getCustomerID() == null)
			getCustomer().retrieve();
	
		try
		{
			GraphCustomerList[] graphs = 
					com.cannontech.database.db.customer.Customer.getAllGraphCustomerList( 
							getCustomerID(), getDbConnection() );

			for( int i = 0; i < graphs.length; i++ )
			{
				graphs[i].setDbConnection(getDbConnection());
				getGraphVector().addElement( graphs[i] );
			}
		}
		catch(java.sql.SQLException e )
		{
			//not necessarily an error
		}
			
		try
		{
			Contact[] contacts = Contact.getAllCustomerContacts( 
						getCustomerID(), 
						getDbConnection() );

			int[] cntIDs = new int[ contacts.length ];
			for( int i = 0; i < contacts.length; i++ )
				cntIDs[i] = contacts[i].getContactID().intValue();
				
			setCustomerContactIDs( cntIDs );
		}
		catch(java.sql.SQLException e )
		{
			//not necessarily an error
		}

		try
		{
			DeviceCustomerList[] devices =
				com.cannontech.database.db.customer.Customer.getAllDeviceCustomerList( 
				getCustomerID(), 
				getDbConnection() );

			for( int i = 0; i < devices.length; i++ )
			{
				devices[i].setDbConnection(getDbConnection());
				getDeviceVector().addElement( devices[i] );
			}
		}
		catch(java.sql.SQLException e )
		{
			//not necessarily an error
		}
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @exception java.sql.SQLException The exception description.
	 */
	public void update() throws java.sql.SQLException 
	{	
		getCustomer().update();
		
		// delete all the graph references for this customer
		GraphCustomerList.deleteCustomerGraphList( getCustomerID(), getDbConnection() );

		// add all the graphs for this customer
		for (int i = 0; i < getGraphVector().size(); i++)
			 ((GraphCustomerList) getGraphVector().elementAt(i)).add();

		// delete all the device references for this customer
		DeviceCustomerList.deleteDeviceCustomerList( getCustomerID() ,getDbConnection());

		// add all the devices for this customer
		for (int i = 0; i < getDeviceVector().size(); i++)
			 ((DeviceCustomerList) getDeviceVector().elementAt(i)).add();

			 
		// delete all the customer contacts for this customer
		Contact.deleteAllAdditionalContacts( getCustomerID(), getDbConnection() );
	
		// add all the contacts for this customer
		if( getCustomerContactIDs() != null )
		{
			for (int i = 0; i < getCustomerContactIDs().length; i++)
			{
				Object addValues[] =
				{
					getCustomer().getCustomerID(),
					new Integer( getCustomerContactIDs()[i] )
				};
	
				//just add the bridge value to the mapping table
				// showing that this contact belongs to the current customer 
				add("CustomerAdditionalContact", addValues);
			}
		}
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (12/19/2001 1:45:25 PM)
	 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
	 */
	public DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
	{
/*
		DBChangeMsg[] msgs =
			new DBChangeMsg
					[1 + (getCustomerContactIDs().length == 0 
							? 1  //at least have room to put the Global Change into the array
							: getCustomerContactIDs().length)  ];
*/
		DBChangeMsg[] msgs = new DBChangeMsg[ 1 ];
		msgs[0] = new DBChangeMsg(
						getCustomerID().intValue(),
						DBChangeMsg.CHANGE_CUSTOMER_DB,
						DBChangeMsg.CAT_CUSTOMER,
						DBChangeMsg.CAT_CUSTOMER,
						typeOfChange);

/*
		//if CustomerContacts have already been deleted, we must send a Change All CustomerContact Cache out
		if( getCustomerContactIDs().length == 0 )
		{
			msgs[ msgs.length - 1 ] = 	
				new DBChangeMsg( 
					DBChangeMsg.CHANGE_INVALID_ID,
					DBChangeMsg.CHANGE_CUSTOMER_CONTACT_DB,
					DBChangeMsg.CAT_CUSTOMERCONTACT,
					DBChangeMsg.CAT_CUSTOMERCONTACT,
					typeOfChange);
		}
		else
		{
			//add the CustomerContact DBChangeMsg to the new array
			for( int i = 0; i < getCustomerContactIDs().length; i++ )
				msgs[ msgs.length + i ] =
					new DBChangeMsg( 
						getCustomerContactIDs()[i],
						DBChangeMsg.CHANGE_CUSTOMER_CONTACT_DB,
						DBChangeMsg.CAT_CUSTOMERCONTACT,
						DBChangeMsg.CAT_CUSTOMERCONTACT,
						typeOfChange);
		}
	
*/
		return msgs;
	}

	/**
	 * Returns the graphVector.
	 * @return java.util.Vector
	 */
	public java.util.Vector getGraphVector() 
	{	
		if( graphVector == null )
			graphVector = new java.util.Vector(10);

		return graphVector;
	}

	/**
	 * Sets the graphVector.
	 * @param graphVector The graphVector to set
	 */
	public void setGraphVector(java.util.Vector graphVector) {
		this.graphVector = graphVector;
	}

	/**
	 * @return
	 */
	public java.util.Vector getDeviceVector()
	{
		if( deviceVector == null )
			deviceVector = new java.util.Vector(10);

		return deviceVector;
	}

	/**
	 * @param vector
	 */
	public void setDeviceVector(java.util.Vector deviceVector)
	{
		this.deviceVector = deviceVector;
	}
}

package com.cannontech.database.data.customer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.customer.DeviceCustomerList;
import com.cannontech.database.db.graph.GraphCustomerList;
import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;

public class Customer extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange {	
	private com.cannontech.database.db.customer.Customer customer = null;
	private Vector<GraphCustomerList> graphVector = null;
	private Vector<DeviceCustomerList> deviceVector = null;

	//contains ints of ContactIDs
	private int[] customerContactIDs = new int[0];

	public Customer() {
		this(CustomerTypes.CUSTOMER_RESIDENTIAL);
	}

	protected Customer(Integer typeId) {
		super();
		getCustomer().setCustomerTypeID(typeId);
	}

	public int[] getCustomerContactIDs() {
		return customerContactIDs;
	}

	public void setCustomerContactIDs(int[] ids) {
		customerContactIDs = ids;
	}

	public void setCustomer( com.cannontech.database.db.customer.Customer customer) {
		this.customer = customer;
	}

	public final com.cannontech.database.db.customer.Customer getCustomer() {
		if (customer == null )
			customer = new com.cannontech.database.db.customer.Customer();
		
		return customer;
	}

	public void setCustomerID(Integer cstId) {
		getCustomer().setCustomerID(cstId);
	}
	
	public Integer getCustomerID() {
		return getCustomer().getCustomerID();
	}

	@Override
    public void add() throws SQLException  {
		if (getCustomerID() == null) {
			setCustomerID( 
				com.cannontech.database.db.customer.Customer.getNextCustomerID() );
		}		
		getCustomer().add();
		
		// add all the contacts for this customers
		if( getCustomerContactIDs() != null )
		{
			for( int i = 0; i < getCustomerContactIDs().length; i++ )
			{
				Object addValues[] = 
				{ 	
					getCustomer().getCustomerID(), 
					new Integer( getCustomerContactIDs()[i] ),
					new Integer(i)
				};
	
				//just add the bridge value to the mapping table
				// showing that this contact belongs to the current customer 
				add("CustomerAdditionalContact", addValues);
			}
		}

		//Add all customer Graphs
		for (int i = 0; i < getGraphVector().size(); i++)
			 ((DBPersistent) getGraphVector().elementAt(i)).add();			

		//Add all customer devices
		for (int i = 0; i < getDeviceVector().size(); i++)
			 ((DBPersistent) getDeviceVector().elementAt(i)).add();			
	}
	
	@Override
    public void delete() throws SQLException {
		// delete all the relations from a graph to this customer
		GraphCustomerList.deleteGraphCustomerList( getCustomerID(), getDbConnection() );
		
		// delete all the relations from a device to this customer
		DeviceCustomerList.deleteDeviceCustomerList( getCustomerID(), getDbConnection() );
		
		// delete all the contacts for this customer
		Contact.deleteAllAdditionalContacts( getCustomerID(), getDbConnection() );

		delete("CustomerNotifGroupMap", "CustomerID", getCustomerID());

		getCustomer().delete();	
	}

	@Override
    public void setDbConnection(Connection conn) {
		super.setDbConnection( conn );
		getCustomer().setDbConnection(conn);

		for (int i = 0; i < getGraphVector().size(); i++)
			 ((DBPersistent) getGraphVector().elementAt(i)).setDbConnection(conn);
			 			
		for (int i = 0; i < getDeviceVector().size(); i++)
			 ((DBPersistent) getDeviceVector().elementAt(i)).setDbConnection(conn);			

	}
	
	@Override
    public void retrieve() throws SQLException {
		getCustomer().retrieve();
	
		try
		{
			GraphCustomerList[] graphs =  GraphCustomerList.getGraphCustomerList( 
							getCustomerID(), getDbConnection() );

			for( int i = 0; i < graphs.length; i++ )
			{
				graphs[i].setDbConnection(getDbConnection());
				getGraphVector().addElement( graphs[i] );
			}
		}
		catch(SQLException e )
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
		catch(SQLException e )
		{
			//not necessarily an error
		}

		try
		{
			DeviceCustomerList[] devices = DeviceCustomerList.getDeviceCustomerList( 
				getCustomerID(), 
				getDbConnection() );

			for( int i = 0; i < devices.length; i++ )
			{
				devices[i].setDbConnection(getDbConnection());
				getDeviceVector().addElement( devices[i] );
			}
		}
		catch(SQLException e )
		{
			//not necessarily an error
		}
	}
	
	@Override
    public void update() throws SQLException {	
		getCustomer().update();
		
		// delete all the graph references for this customer
		GraphCustomerList.deleteGraphCustomerList( getCustomerID(), getDbConnection() );
		// add all the graphs for this customer
		for (int i = 0; i < getGraphVector().size(); i++)
			 getGraphVector().elementAt(i).add();

		// delete all the device references for this customer
		DeviceCustomerList.deleteDeviceCustomerList( getCustomerID() ,getDbConnection());
		// add all the devices for this customer
		for (int i = 0; i < getDeviceVector().size(); i++)
			 getDeviceVector().elementAt(i).add();

			 
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
					new Integer( getCustomerContactIDs()[i] ),
					new Integer(i)
				};
	
				//just add the bridge value to the mapping table
				// showing that this contact belongs to the current customer 
				add("CustomerAdditionalContact", addValues);
			}
		}
	}

	public DBChangeMessage[] getDBChangeMsgs(DbChangeType dbChangeType) {
		String categoryType = DBChangeMessage.CAT_CUSTOMER;
		if( getCustomer().getCustomerTypeID().intValue() == CustomerTypes.CUSTOMER_CI)
			categoryType= DBChangeMessage.CAT_CI_CUSTOMER;
			
		DBChangeMessage[] msgs = new DBChangeMessage[ 1 ];
		msgs[0] = new DBChangeMessage(
						getCustomerID().intValue(),
						DBChangeMessage.CHANGE_CUSTOMER_DB,
						categoryType,
						categoryType,
						dbChangeType);

		return msgs;
	}

	public Vector<GraphCustomerList> getGraphVector() {	
		if (graphVector == null )
			graphVector = new Vector<GraphCustomerList>(10);

		return graphVector;
	}

	public void setGraphVector(Vector<GraphCustomerList> graphVector) {
		this.graphVector = graphVector;
	}

	public Vector<DeviceCustomerList> getDeviceVector() {
		if (deviceVector == null)
			deviceVector = new Vector<DeviceCustomerList>(10);

		return deviceVector;
	}

	public void setDeviceVector(Vector<DeviceCustomerList> deviceVector) {
		this.deviceVector = deviceVector;
	}
}

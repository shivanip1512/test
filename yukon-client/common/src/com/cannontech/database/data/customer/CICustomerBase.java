package com.cannontech.database.data.customer;

/**
 * Insert the type's description here.
 * Creation date: (12/6/00 3:54:11 PM)
 * @author: 
 */
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.db.customer.CustomerAddress;
import com.cannontech.database.db.customer.CustomerBaseLine;
import com.cannontech.user.UserUtils;

public class CICustomerBase extends CustomerBase implements com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.customer.CICustomerBase ciCustomerBase = null;

	private CustomerAddress customerAddress = null;
	private CustomerBaseLine customerBaseLine = null;

	//currently, 1 customer may only belong to 1 EnergyCompany. This is null
	// if there is customer is not owned by an EnergyCompany
	private EnergyCompany energyCompany = null;

	//contains com.cannontech.database.data.device.customer.CustomerContact
	private java.util.Vector customerContactVector = null;

	//containts com.cannontech.database.db.graph.GraphCustomerList
	private java.util.Vector graphVector = null;

	//contains com.cannontech.database.db.pao.PAOowner
	private java.util.Vector meterVector = null;
/**
 * LMProgramBase constructor comment.
 */
public CICustomerBase() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	getCustomerAddress().add();
	
	getCiCustomerBase().setAddressID( getCustomerAddress().getAddressID() );
	super.add();
	
	getCiCustomerBase().add();
	getCustomerBaseLine().add();

	// add all the contacts for this customers
	if( getCustomerContactVector() != null )
	{
		for( int i = 0; i < getCustomerContactVector().size(); i++ )
		{
			//when creating a CICustomer, we start with no login ability
			((CustomerContact)getCustomerContactVector().elementAt(i)).setUserID(
					new Integer(UserUtils.USER_YUKON_ID) );

			((CustomerContact)getCustomerContactVector().elementAt(i)).add();
			
			Object addValues[] = 
			{ 	
				getCiCustomerBase().getDeviceID(), 
				((CustomerContact) getCustomerContactVector().elementAt(i)).getCustomerContact().getContactID()
			};

			//just add the bridge value to the CICustContact table
			// showing that this contact belongs to the current customer 
			add("CICustContact", addValues);
		}
	}

	if( getEnergyCompany() != null )
	{
		Object addValues[] = 
		{ 
			getEnergyCompany().getEnergyCompanyID(), 
			getCustomerID()
		};

		//just add the bridge value to the EnergyCompanyCustomerList table
		// showing that this customer belongs to the EnergyCompany
		add("EnergyCompanyCustomerList", addValues);		
	}
	
	
	setDbConnection(null);
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{
	delete(
		com.cannontech.database.db.device.lm.LMEnergyExchangeCustomerList.TABLE_NAME,
		"LMCustomerDeviceID",
		getCiCustomerBase().getDeviceID());

	//just delete the bridge value to the EnergyCompanyCustomerList table
	delete("EnergyCompanyCustomerList", "CustomerID", getCustomerID() );		
	
	// delete all the ownership of meters for this customer
	com.cannontech.database.db.pao.PAOowner.deleteAllPWOowners( getCiCustomerBase().getDeviceID(), getDbConnection() );

	// delete all the contacts for this customer
	com.cannontech.database.db.customer.CustomerContact.deleteAllCustomerContacts( getCiCustomerBase().getDeviceID(), getDbConnection() );

	// delete all the relations from a graph to this customer
	com.cannontech.database.db.customer.CustomerContact.deleteCustomerGraphList( getCiCustomerBase().getDeviceID(), getDbConnection() );

	delete("LMEnergyExchangeHourlyCustomer", "CustomerID", getCustomerID() );
	delete("LMEnergyExchangeCustomerReply", "CustomerID", getCustomerID() );
	delete("LMCurtailCustomerActivity", "CustomerID", getCustomerID() );
		
	getCiCustomerBase().delete();

	getCustomerBaseLine().delete();
	
	//delete("CustomerAddress", "AddressID", getCiCustomerBase().getAddressID() );
	getCustomerAddress().setAddressID( 
				com.cannontech.database.db.customer.CICustomerBase.getCustomerAddressID(getCiCustomerBase().getDeviceID(), getDbConnection()) );

	getCiCustomerBase().delete();

	getCustomerAddress().delete();
	

	super.delete();


	setDbConnection(null);
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:27:13 PM)
 * @return com.cannontech.database.db.customer.CICustomerBase
 */
public com.cannontech.database.db.customer.CICustomerBase getCiCustomerBase() 
{
	if( ciCustomerBase == null )
		ciCustomerBase = new com.cannontech.database.db.customer.CICustomerBase();

	return ciCustomerBase;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 2:47:56 PM)
 * @return com.cannontech.database.db.device.customer.CustomerAddress
 */
public CustomerAddress getCustomerAddress() 
{
	if( customerAddress == null )
		customerAddress = new CustomerAddress();
		
	return customerAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 3:34:40 PM)
 * @return com.cannontech.database.db.device.customer.CustomerBaseLine
 */
public CustomerBaseLine getCustomerBaseLine() 
{
	if( customerBaseLine == null )
		customerBaseLine = new CustomerBaseLine();

	return customerBaseLine;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 2:59:45 PM)
 * @return java.util.Vector
 */
public java.util.Vector getCustomerContactVector() 
{
	if( customerContactVector == null )
		customerContactVector = new java.util.Vector(10);

	return customerContactVector;
}

/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 1:45:25 PM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	//get the current DBChangeMsgs from our parent
	com.cannontech.message.dispatch.message.DBChangeMsg[] msgs = super.getDBChangeMsgs(typeOfChange);

	//create a new Array with room for the CustomerContacts
	com.cannontech.message.dispatch.message.DBChangeMsg[] realMsgs =
		new com.cannontech.message.dispatch.message.DBChangeMsg
				[msgs.length + (getCustomerContactVector().size() == 0 
										? 1  //at least have room to put the Global Change into the array
										: getCustomerContactVector().size())  ];

	//copy the original change messages to our new array
	System.arraycopy( msgs, 0, realMsgs, 0, msgs.length );

	//if CustomerContacts have already been deleted, we must send a Change All CustomerContact Cache out
	if( getCustomerContactVector().size() == 0 )
	{
		realMsgs[ realMsgs.length - 1 ] = 	
			new com.cannontech.message.dispatch.message.DBChangeMsg( 
				com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_INVALID_ID,
				com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_CUSTOMER_CONTACT_DB,
				com.cannontech.message.dispatch.message.DBChangeMsg.CAT_CUSTOMERCONTACT,
				com.cannontech.message.dispatch.message.DBChangeMsg.CAT_CUSTOMERCONTACT,
				typeOfChange);
	}
	else
	{
		//add the CustomerContact DBChangeMsg to the new array
		for( int i = 0; i < getCustomerContactVector().size(); i++ )
			realMsgs[ msgs.length + i ] =
					((CustomerContact)getCustomerContactVector().get(i)).getDBChangeMsgs(typeOfChange)[0];
	}
 
	return realMsgs;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2002 4:32:41 PM)
 * @return com.cannontech.database.db.company.EnergyCompany
 */
public com.cannontech.database.db.company.EnergyCompany getEnergyCompany() {
	return energyCompany;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/2001 2:36:57 PM)
 * @return java.util.Vector
 */
public java.util.Vector getGraphVector() 
{
	if( graphVector == null )
		graphVector = new java.util.Vector(10);

	return graphVector;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 12:47:02 PM)
 * @return java.util.Vector
 */
public java.util.Vector getMeterVector() 
{
	if( meterVector == null )
		meterVector = new java.util.Vector(10);

	return meterVector;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	super.retrieve();

	getCiCustomerBase().retrieve();
	
	getCustomerBaseLine().retrieve();

	getCustomerAddress().setAddressID( getCiCustomerBase().getAddressID() );
	getCustomerAddress().retrieve();

	try
	{
		com.cannontech.database.db.customer.CustomerContact[] contacts = com.cannontech.database.db.customer.CustomerContact.getAllCustomerContacts( getCiCustomerBase().getDeviceID(), getDbConnection() );
		for( int i = 0; i < contacts.length; i++ )
		{
			CustomerContact dataContact = new CustomerContact();
			dataContact.setDbConnection( getDbConnection() );
			dataContact.setContactID( contacts[i].getContactID() );
			dataContact.retrieve();
			
			getCustomerContactVector().addElement( dataContact );
		}
	}
	catch(java.sql.SQLException e )
	{
		//not necessarily an error
	}


	try
	{
		com.cannontech.database.db.graph.GraphCustomerList[] graphs = com.cannontech.database.db.customer.CICustomerBase.getAllGraphCustomerList( getCiCustomerBase().getDeviceID(), getDbConnection() );
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
		com.cannontech.database.db.pao.PAOowner[] meters = com.cannontech.database.db.pao.PAOowner.getAllMetersCustomerList( getCiCustomerBase().getDeviceID(), getDbConnection() );
		for( int i = 0; i < meters.length; i++ )
		{
			meters[i].setDbConnection(getDbConnection());
			getMeterVector().addElement( meters[i] );
		}
	}
	catch(java.sql.SQLException e )
	{
		//not necessarily an error
	}

/**************  BEGIN SUPREME HACK!!!!! ***********************************/
	//if an EnergyCompany exists, gets its values
	String[] cols = { "EnergyCompanyID", "CustomerID" };
	String[] keys = { "CustomerID" };
	Object[] vals = { getCustomerID() };
	Object[] rets = retrieve( cols,
									 "EnergyCompanyCustomerList",
									 keys,
									 vals );

	if( rets.length > 0 )
	{
		EnergyCompany ec = new EnergyCompany();
		ec.setEnergyCompanyID( (Integer)rets[0] );
		ec.setDbConnection( getDbConnection() );

		//get the data
		energyCompany = ec;
		getEnergyCompany().retrieve();
	}
/**************  END SUPREME HACK!!!!! ***********************************/

	setDbConnection(null);
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:27:13 PM)
 * @param newCiCustomerBase com.cannontech.database.db.customer.CICustomerBase
 */
public void setCiCustomerBase(com.cannontech.database.db.customer.CICustomerBase newCiCustomerBase) {
	ciCustomerBase = newCiCustomerBase;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 2:47:56 PM)
 * @param newCustomerAddress com.cannontech.database.db.customer.CustomerAddress
 */
public void setCustomerAddress(com.cannontech.database.db.customer.CustomerAddress newCustomerAddress) {
	customerAddress = newCustomerAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 3:34:40 PM)
 * @param newCustomerBaseLine com.cannontech.database.db.customer.CustomerBaseLine
 */
public void setCustomerBaseLine(com.cannontech.database.db.customer.CustomerBaseLine newCustomerBaseLine) {
	customerBaseLine = newCustomerBaseLine;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 2:59:45 PM)
 * @param newCustomerContactVector java.util.Vector
 */
public void setCustomerContactVector(java.util.Vector newCustomerContactVector) {
	customerContactVector = newCustomerContactVector;
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setCustomerID(Integer custID) 
{
	super.setCustomerID( custID );
	getCiCustomerBase().setDeviceID( custID );
	getCustomerBaseLine().setCustomerID(custID);
}

/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	super.setDbConnection(conn);
	getCiCustomerBase().setDbConnection(conn);
	getCustomerAddress().setDbConnection(conn);	
	getCustomerBaseLine().setDbConnection(conn);

	for (int i = 0; i < getCustomerContactVector().size(); i++)
		 ((com.cannontech.database.db.DBPersistent) getCustomerContactVector().elementAt(i)).setDbConnection(conn);

	for (int i = 0; i < getGraphVector().size(); i++)
		 ((com.cannontech.database.db.DBPersistent) getGraphVector().elementAt(i)).setDbConnection(conn);

	for (int i = 0; i < getMeterVector().size(); i++)
		 ((com.cannontech.database.db.DBPersistent) getMeterVector().elementAt(i)).setDbConnection(conn);

	if( getEnergyCompany() != null )
		getEnergyCompany().setDbConnection( conn );
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2002 4:32:41 PM)
 * @param newEnergyCompany com.cannontech.database.db.company.EnergyCompany
 */
public void setEnergyCompany(com.cannontech.database.db.company.EnergyCompany newEnergyCompany) {
	energyCompany = newEnergyCompany;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/2001 2:36:57 PM)
 * @param newGraphVector java.util.Vector
 */
public void setGraphVector(java.util.Vector newGraphVector) {
	graphVector = newGraphVector;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 12:47:02 PM)
 * @param newMeterVector java.util.Vector
 */
public void setMeterVector(java.util.Vector newMeterVector) {
	meterVector = newMeterVector;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException 
{

	getCustomerAddress().setAddressID(getCiCustomerBase().getAddressID());
	getCustomerAddress().update();
	
	super.update();
	getCiCustomerBase().update();

	getCustomerBaseLine().update();

	// delete all the customer contacts for this customer
	com.cannontech.database.db.customer.CustomerContact.deleteAllCustomerContacts( getCiCustomerBase().getDeviceID(), getCiCustomerBase().getDbConnection() );

	
	// add all the contacts for this customer
	if (getCustomerContactVector() != null)
	{
		for (int i = 0; i < getCustomerContactVector().size(); i++)
		{
			((CustomerContact) getCustomerContactVector().elementAt(i)).add();

			Object addValues[] =
			{
				getCiCustomerBase().getDeviceID(),
				((CustomerContact) getCustomerContactVector().elementAt(i)).getCustomerContact().getContactID()};

				//just add the bridge value to the CICustContact table
				// showing that this contact belongs to the current customer 
				add("CICustContact", addValues);
			}

	}


	// delete all the ownership of meters for this customer
	com.cannontech.database.db.pao.PAOowner.deleteAllPWOowners( getCiCustomerBase().getDeviceID(), getDbConnection() );
	// add all the current selected meters for this customer
	for (int i = 0; i < getMeterVector().size(); i++)
		 ((com.cannontech.database.db.pao.PAOowner) getMeterVector().elementAt(i)).add();

		
	// delete all the graph references for this customer
	com.cannontech.database.db.graph.GraphCustomerList.deleteCustomerGraphList(getCiCustomerBase().getDeviceID());
	// add all the graphs for this customer
	for (int i = 0; i < getGraphVector().size(); i++)
		 ((com.cannontech.database.db.graph.GraphCustomerList) getGraphVector().elementAt(i)).add();


	//just delete the bridge value to the EnergyCompanyCustomerList table
	delete("EnergyCompanyCustomerList", "CustomerID", getCustomerID() );

	//add a new EnergyCompany if needed
	if( getEnergyCompany() != null )
	{
		Object addValues[] = 
		{ 
			getEnergyCompany().getEnergyCompanyID(), 
			getCustomerID()
		};

		//just add the bridge value to the EnergyCompanyCustomerList table
		// showing that this customer belongs to the EnergyCompany
		add(com.cannontech.database.db.web.EnergyCompanyCustomerList.tableName,
			 addValues);
	}
		 
	setDbConnection(null);
}






/**
 * This method was created in VisualAge.
 */
/*public void update() throws java.sql.SQLException 
{

	getCustomerAddress().setAddressID(getCiCustomerBase().getAddressID());
	getCustomerAddress().update();
	
	super.update();
	getCiCustomerBase().update();

	getCustomerWebSettings().update();
	getCustomerBaseLine().update();

	// delete all the customer contacts for this customer
	com.cannontech.database.db.customer.CustomerContact.deleteAllCustomerContacts( getCiCustomerBase().getDeviceID(), getCiCustomerBase().getDbConnection() );

	// add all the contacts for this customer
	if (getCustomerContactVector() != null)
	{
		boolean added = false;
		
		for (int i = 0; i < getCustomerContactVector().size(); i++)
		{
			added = false;
			//when creating a CICustomer, we start with no login ability
			if (((CustomerContact) getCustomerContactVector().elementAt(i)).getCustomerLogin().getLoginID() == null)
			{
				((CustomerContact) getCustomerContactVector().elementAt(i)).setLogInID(
					new Integer(com.cannontech.database.db.customer.CustomerLogin.NONE_LOGIN_ID));
				((CustomerContact) getCustomerContactVector().elementAt(i)).getCustomerContact().add();
				added = true;
			}
			else
			{
				Object[] constraintValues = { ((CustomerContact) getCustomerContactVector().elementAt(i)).getCustomerContact().getContactID() };
				Object results[] = retrieve( 
							com.cannontech.database.db.customer.CustomerContact.SETTER_COLUMNS, 
							com.cannontech.database.db.customer.CustomerContact.TABLE_NAME, 
							com.cannontech.database.db.customer.CustomerContact.CONSTRAINT_COLUMNS, 
							constraintValues );

				//if the contact already exists, update the values
				if( results.length == com.cannontech.database.db.customer.CustomerContact.SETTER_COLUMNS.length )
					((CustomerContact) getCustomerContactVector().elementAt(i)).update();
				else
				{
					((CustomerContact) getCustomerContactVector().elementAt(i)).add(); //may cause an error here
					added = true;
				}
			}
			

			if( added )
			{
				Object addValues[] = { getCiCustomerBase().getDeviceID(),
						((CustomerContact) getCustomerContactVector().elementAt(i)).getCustomerContact().getContactID() };

				//just add the bridge value to the CICustContact table
				// showing that this contact belongs to the current customer 
				add("CICustContact", addValues);
			}
			
		}

	}


	// delete all the ownership of meters for this customer
	com.cannontech.database.db.pao.PAOowner.deleteAllPWOowners( getCiCustomerBase().getDeviceID(), getDbConnection() );
	// add all the current selected meters for this customer
	for (int i = 0; i < getMeterVector().size(); i++)
		 ((com.cannontech.database.db.pao.PAOowner) getMeterVector().elementAt(i)).add();

		
	// delete all the graph references for this customer
	com.cannontech.database.db.graph.GraphCustomerList.deleteCustomerGraphList(getCiCustomerBase().getDeviceID());
	// add all the graphs for this customer
	for (int i = 0; i < getGraphVector().size(); i++)
		 ((com.cannontech.database.db.graph.GraphCustomerList) getGraphVector().elementAt(i)).add();

	setDbConnection(null);
}*/
}

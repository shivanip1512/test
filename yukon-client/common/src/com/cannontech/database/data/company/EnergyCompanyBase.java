package com.cannontech.database.data.company;

/**
 * Insert the type's description here.
 * Creation date: (12/6/00 3:54:11 PM)
 * @author: 
 */

public class EnergyCompanyBase extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.company.EnergyCompany energyCompany = null;

	//private CustomerAddress customerAddress = null;
	//private CustomerWebSettings customerWebSettings = null;
	//private CustomerBaseLine customerBaseLine = null;
	
	//contains com.cannontech.database.data.device.customer.CustomerContact
	//private java.util.Vector operatorLoginsVector = null;

	//containts com.cannontech.database.db.customer.CICustomerBase
	private java.util.Vector ciCustomerVector = null;
/**
 * LMProgramBase constructor comment.
 */
public EnergyCompanyBase() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	getEnergyCompany().add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{
/*	delete(
		com.cannontech.database.db.device.lm.LMEnergyExchangeCustomerList.TABLE_NAME,
		"CustomerID",
		getCiCustomerBase().getDeviceID());

	// delete all the ownership of meters for this customer
	com.cannontech.database.db.pao.PAOowner.deleteAllPWOowners( getCiCustomerBase().getDeviceID(), getDbConnection() );

	// delete all the contacts for this customer
	com.cannontech.database.db.customer.CustomerContact.deleteAllCustomerContacts( getCiCustomerBase().getDeviceID(), getDbConnection() );

	// delete all the relations from a graph to this customer
	com.cannontech.database.db.customer.CustomerContact.deleteCustomerGraphList( getCiCustomerBase().getDeviceID(), getDbConnection() );

	getCustomerWebSettings().delete();
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
*/
	delete( "EnergyCompanyCustomerList", "EnergyCompanyID", getEnergyCompany().getEnergyCompanyID() );
	delete( "EnergyCompanyOperatorLoginList", "EnergyCompanyID", getEnergyCompany().getEnergyCompanyID() );
	getEnergyCompany().delete();
	
	if (getEnergyCompany().getPrimaryContactID() != null
		&& getEnergyCompany().getPrimaryContactID().intValue() != com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID)
	{
		com.cannontech.database.data.customer.Contact contact =
				new com.cannontech.database.data.customer.Contact();
		contact.setContactID( getEnergyCompany().getPrimaryContactID() );
		contact.setDbConnection( getDbConnection() );
		contact.delete();
	}
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
					getEnergyCompany().getEnergyCompanyID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_ENERGY_COMPANY_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_ENERGY_COMPANY,
					null,
					typeOfChange)
	};


	return msgs;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2002 10:43:13 AM)
 * @return com.cannontech.database.db.company.EnergyCompany
 */
public com.cannontech.database.db.company.EnergyCompany getEnergyCompany() 
{
	if( energyCompany == null )
		energyCompany = new com.cannontech.database.db.company.EnergyCompany();

	return energyCompany;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	getEnergyCompany().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	super.setDbConnection(conn);
	getEnergyCompany().setDbConnection(conn);

/*	for (int i = 0; i < getCustomerContactVector().size(); i++)
		 ((com.cannontech.database.db.DBPersistent) getCustomerContactVector().elementAt(i)).setDbConnection(conn);

	for (int i = 0; i < getGraphVector().size(); i++)
		 ((com.cannontech.database.db.DBPersistent) getGraphVector().elementAt(i)).setDbConnection(conn);

	for (int i = 0; i < getMeterVector().size(); i++)
		 ((com.cannontech.database.db.DBPersistent) getMeterVector().elementAt(i)).setDbConnection(conn);
*/
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2002 10:43:13 AM)
 * @param newEnergyCompany com.cannontech.database.db.company.EnergyCompany
 */
public void setEnergyCompany(com.cannontech.database.db.company.EnergyCompany newEnergyCompany) {
	energyCompany = newEnergyCompany;
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setEnergyCompanyID(Integer ecID)
{
	getEnergyCompany().setEnergyCompanyID( ecID );
}

public void setName(String name) {
	getEnergyCompany().setName(name);
}

public String getName() {
	return getEnergyCompany().getName();
}

/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException 
{
	getEnergyCompany().update();
	
	// delete all the customer contacts for this customer
	//com.cannontech.database.db.customer.CustomerContact.deleteAllCustomerContacts( getCiCustomerBase().getDeviceID(), getCiCustomerBase().getDbConnection() );


	// delete all the ownership of meters for this customer
	//com.cannontech.database.db.pao.PAOowner.deleteAllPWOowners( getCiCustomerBase().getDeviceID(), getDbConnection() );
	// add all the current selected meters for this customer
	//for (int i = 0; i < getMeterVector().size(); i++)
		 //((com.cannontech.database.db.pao.PAOowner) getMeterVector().elementAt(i)).add();	
}
}

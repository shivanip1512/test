package com.cannontech.database.data.customer;

/**
 * This type was created in VisualAge.
 */

public class CustomerContact extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.customer.CustomerContact customerContact = null;
	private com.cannontech.database.db.customer.CustomerLogin customerLogin = null;
/**
 * StatusPoint constructor comment.
 */
public CustomerContact() {
	super();
}
/**
 * StatusPoint constructor comment.
 */
public CustomerContact(Integer contactID) {
	super();
	getCustomerContact().setContactID(contactID);
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	getCustomerLogin().add();
	getCustomerContact().add();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void delete() throws java.sql.SQLException 
{
	setLogInID( getCustomerContact().getLogInID() );

	getCustomerContact().delete();

	if( getCustomerContact().getLogInID().intValue() > com.cannontech.database.db.customer.CustomerLogin.NONE_LOGIN_ID )
		getCustomerLogin().delete();
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:12:12 AM)
 * @return com.cannontech.database.db.customer.CustomerContact
 */
public com.cannontech.database.db.customer.CustomerContact getCustomerContact() 
{
	if( customerContact == null )
		customerContact = new com.cannontech.database.db.customer.CustomerContact();
	
	return customerContact;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:12:12 AM)
 * @return com.cannontech.database.db.customer.CustomerLogin
 */
public com.cannontech.database.db.customer.CustomerLogin getCustomerLogin() 
{
	if( customerLogin == null )
		customerLogin = new com.cannontech.database.db.customer.CustomerLogin();
		
	return customerLogin;
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
					getCustomerContact().getContactID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_CUSTOMER_CONTACT_DB,
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
	getCustomerContact().retrieve();

	setLogInID( getCustomerContact().getLogInID() );
	if( getCustomerContact().getLogInID().intValue() > com.cannontech.database.db.customer.CustomerLogin.NONE_LOGIN_ID )
		getCustomerLogin().retrieve();
}
/**
 * This method was created in VisualAge.
 */
public void setContactID(Integer contactID) {
	getCustomerContact().setContactID(contactID);
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:12:12 AM)
 * @param newCustomerContact com.cannontech.database.db.customer.CustomerContact
 */
public void setCustomerContact(com.cannontech.database.db.customer.CustomerContact newCustomerContact) {
	customerContact = newCustomerContact;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:12:12 AM)
 * @param newCustomerLogin com.cannontech.database.db.customer.CustomerLogin
 */
public void setCustomerLogin(com.cannontech.database.db.customer.CustomerLogin newCustomerLogin) {
	customerLogin = newCustomerLogin;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getCustomerContact().setDbConnection(conn);
	getCustomerLogin().setDbConnection(conn);
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 1:23:25 PM)
 * @param loginID java.lang.Integer
 */
public void setLogInID(Integer loginID) 
{
	getCustomerContact().setLogInID( loginID );
	getCustomerLogin().setLoginID( loginID );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() 
{
	return getCustomerContact().getContLastName();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{	
	setLogInID( getCustomerContact().getLogInID() );
	if( getCustomerContact().getLogInID().intValue() > com.cannontech.database.db.customer.CustomerLogin.NONE_LOGIN_ID )
	{
		if( com.cannontech.database.db.customer.CustomerLogin.getCustomerLogin( getCustomerContact().getLogInID() ) != null )
			getCustomerLogin().update();
		else
			getCustomerLogin().add();
		
	}

	getCustomerContact().update();	
}
}

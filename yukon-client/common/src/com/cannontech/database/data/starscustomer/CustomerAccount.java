package com.cannontech.database.data.starscustomer;

/**
 * Insert the type's description here.
 * Creation date: (3/25/2002 10:12:56 AM)
 * @author: 
 */
public class CustomerAccount extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange 
{
	private com.cannontech.database.db.starscustomer.CustomerAccount customerAccount = null;
/**
 * CustomerAccount constructor comment.
 */
public CustomerAccount() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	getCustomerAccount().add();
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	getCustomerAccount().delete();
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 10:13:32 AM)
 * @return com.cannontech.database.db.starscustomer.CustomerAccount
 */
public com.cannontech.database.db.starscustomer.CustomerAccount getCustomerAccount() 
{
	if( customerAccount == null )
		customerAccount = new com.cannontech.database.db.starscustomer.CustomerAccount();
	
	return customerAccount;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 10:12:56 AM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg
 */
public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs(int typeOfChange) {
	return null;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	getCustomerAccount().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 10:15:45 AM)
 * @param newID java.lang.Integer
 */
public void setAccountID(Integer newID) 
{
	getCustomerAccount().setAccountID( newID );
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	getCustomerAccount().setDbConnection(conn);
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	getCustomerAccount().update();
}
}

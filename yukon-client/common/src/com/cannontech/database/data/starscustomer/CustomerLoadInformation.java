package com.cannontech.database.data.starscustomer;

/**
 * Insert the type's description here.
 * Creation date: (3/25/2002 10:12:56 AM)
 * @author: 
 */
public abstract class CustomerLoadInformation extends com.cannontech.database.db.DBPersistent
{
	private com.cannontech.database.db.starscustomer.CustomerLoadInformation customerLoadInformation = null;
/**
 * CustomerAccount constructor comment.
 */
public CustomerLoadInformation() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	getCustomerLoadInformation().add();
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	getCustomerLoadInformation().delete();
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 10:56:39 AM)
 * @return com.cannontech.database.db.starscustomer.CustomerLoadInformation
 */
public com.cannontech.database.db.starscustomer.CustomerLoadInformation getCustomerLoadInformation() 
{
	if( customerLoadInformation == null )
		customerLoadInformation = new com.cannontech.database.db.starscustomer.CustomerLoadInformation();

	return customerLoadInformation;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	getCustomerLoadInformation().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	getCustomerLoadInformation().setDbConnection(conn);
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 10:15:45 AM)
 * @param newID java.lang.Integer
 */
public void setLoadID(Integer newID) 
{
	getCustomerLoadInformation().setLoadID( newID );
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	getCustomerLoadInformation().update();
}
}

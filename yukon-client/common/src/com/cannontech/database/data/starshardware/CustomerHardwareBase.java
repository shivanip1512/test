package com.cannontech.database.data.starshardware;

/**
 * Insert the type's description here.
 * Creation date: (3/25/2002 10:12:56 AM)
 * @author: 
 */
public abstract class CustomerHardwareBase extends com.cannontech.database.db.DBPersistent
{
	private com.cannontech.database.db.starshardware.CustomerHardwareBase customerHardwareBase = null;
/**
 * CustomerAccount constructor comment.
 */
public CustomerHardwareBase() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	getCustomerHardwareBase().add();
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	getCustomerHardwareBase().delete();
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 11:13:28 AM)
 * @return com.cannontech.database.db.starshardware.CustomerHardwareBase
 */
public com.cannontech.database.db.starshardware.CustomerHardwareBase getCustomerHardwareBase() 
{
	if( customerHardwareBase == null )
		customerHardwareBase = new com.cannontech.database.db.starshardware.CustomerHardwareBase();

	return customerHardwareBase;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	getCustomerHardwareBase().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	getCustomerHardwareBase().setDbConnection(conn);
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 10:15:45 AM)
 * @param newID java.lang.Integer
 */
public void setHardwareID(Integer newID) 
{
	getCustomerHardwareBase().setHardwareID( newID );
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	getCustomerHardwareBase().update();
}
}

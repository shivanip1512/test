package com.cannontech.database.data.starshardware;

/**
 * Insert the type's description here.
 * Creation date: (3/25/2002 10:12:56 AM)
 * @author: 
 */
public class LIAirConditioner extends com.cannontech.database.data.starscustomer.CustomerLoadInformation
{
	private com.cannontech.database.db.starshardware.LIAirConditioner liAirConditioner = null;
/**
 * CustomerAccount constructor comment.
 */
public LIAirConditioner() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	super.add();
	getLiAirConditioner().add();
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	getLiAirConditioner().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 11:08:09 AM)
 * @return com.cannontech.database.db.starshardware.LIAirConditioner
 */
public com.cannontech.database.db.starshardware.LIAirConditioner getLiAirConditioner() 
{
	if( liAirConditioner == null )
		liAirConditioner = new com.cannontech.database.db.starshardware.LIAirConditioner();

	return liAirConditioner;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	super.retrieve();
	getLiAirConditioner().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	super.setDbConnection( conn );
	getLiAirConditioner().setDbConnection(conn);
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 10:15:45 AM)
 * @param newID java.lang.Integer
 */
public void setLoadID(Integer newID) 
{
	super.setLoadID( newID );
	
	getLiAirConditioner().setLoadID( newID );
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	super.update();
	getLiAirConditioner().update();
}
}

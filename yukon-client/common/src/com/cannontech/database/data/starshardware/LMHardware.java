package com.cannontech.database.data.starshardware;

/**
 * Insert the type's description here.
 * Creation date: (3/25/2002 10:12:56 AM)
 * @author: 
 */
public class LMHardware extends CustomerHardwareBase
{
	private com.cannontech.database.db.starshardware.LMHardware lmHardware = null;	
/**
 * CustomerAccount constructor comment.
 */
public LMHardware() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	super.add();
	getLmHardware().add();
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	getLmHardware().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 11:13:28 AM)
 * @return com.cannontech.database.db.starshardware.CustomerHardwareBase
 */
public com.cannontech.database.db.starshardware.LMHardware getLmHardware() 
{
	if( lmHardware == null )
		lmHardware = new com.cannontech.database.db.starshardware.LMHardware();

	return lmHardware;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	getLmHardware().retrieve();
	super.retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	super.setDbConnection(conn);
	getLmHardware().setDbConnection(conn);	
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 10:15:45 AM)
 * @param newID java.lang.Integer
 */
public void setHardwareID(Integer newID) 
{
	super.setHardwareID(newID);
	getLmHardware().setHardwareID( newID );
	
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	super.update();
	getLmHardware().update();	
}
}

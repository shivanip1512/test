package com.cannontech.database.data.port;

/**
 * This type was created in VisualAge.
 */
 import com.cannontech.database.db.port.*;
 
public class LocalSharedPort extends LocalDirectPort {
	private PortTiming portTiming = null;
/**
 * LocalSharedPort constructor comment.
 */
public LocalSharedPort() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException{
	super.add();
	getPortTiming().add();
	
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException{
	getPortTiming().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.port.PortTiming
 */
public PortTiming getPortTiming() {
	if( portTiming == null )
		portTiming =  new PortTiming();
	return portTiming;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{
	super.retrieve();
	getPortTiming().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getPortTiming().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param portID java.lang.Integer
 */
public void setPortID(Integer portID) {
	super.setPortID( portID );
	getPortTiming().setPortID(portID);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.port.PortTiming
 */
public void setPortTiming(PortTiming newValue) {
	this.portTiming = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();
	getPortTiming().update();
}
}

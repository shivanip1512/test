package com.cannontech.database.data.port;

/**
 * This type was created in VisualAge.
 */
 import com.cannontech.database.db.port.*;
 
public class LocalDirectPort extends DirectPort {
	private PortLocalSerial portLocalSerial = null;
/**
 * LocalDirectPort constructor comment.
 */
public LocalDirectPort() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException{
	super.add();
	getPortLocalSerial().add();
	
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {
	getPortLocalSerial().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.port.PortLocalSerial
 */
public PortLocalSerial getPortLocalSerial() {
	if( portLocalSerial == null )
		portLocalSerial = new PortLocalSerial();
		
	return portLocalSerial;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {
	super.retrieve();
	getPortLocalSerial().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getPortLocalSerial().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public void setPortID(Integer portID) {
	super.setPortID( portID );
	getPortLocalSerial().setPortID( portID );
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.port.PortLocalSerial
 */
public void setPortLocalSerial(PortLocalSerial newValue) {
	this.portLocalSerial = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getPortLocalSerial().update();
}
}

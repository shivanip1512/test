package com.cannontech.database.data.port;

/**
 * This type was created in VisualAge.
 */
 import com.cannontech.database.db.port.PortDialupModem;
 
public class LocalDialupPort extends LocalSharedPort {
	
	private PortDialupModem portDialupModem = null;
/**
 * LocalDialupPort constructor comment.
 */
public LocalDialupPort() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException{
	super.add();
	getPortDialupModem().add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {
	getPortDialupModem().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.port.PortDialupModem
 */
public PortDialupModem getPortDialupModem() {
	if( portDialupModem == null )
		portDialupModem = new PortDialupModem();
		
	return portDialupModem;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{
	super.retrieve();
	getPortDialupModem().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getPortDialupModem().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.port.PortDialupModem
 */
public void setPortDialupModem(PortDialupModem newValue) {
	this.portDialupModem = newValue;
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public void setPortID(Integer portID) {
	super.setPortID( portID );
	getPortDialupModem().setPortID(portID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();
	getPortDialupModem().update();
}
}

package com.cannontech.database.data.port;

/**
 * This type was created in VisualAge.
 */
 import com.cannontech.database.db.port.*;
 
public class TerminalServerDirectPort extends DirectPort {
	private PortTerminalServer portTerminalServer = null;
/**
 * TerminalServerDirectPort constructor comment.
 */
public TerminalServerDirectPort() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException{
	super.add();
	getPortTerminalServer().add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException{
	getPortTerminalServer().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.port.PortTerminalServer
 */
public PortTerminalServer getPortTerminalServer() {
	if( portTerminalServer == null )
		portTerminalServer = new PortTerminalServer();
		
	return portTerminalServer;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {
	super.retrieve();
	getPortTerminalServer().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getPortTerminalServer().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param portID java.lang.Integer
 */
public void setPortID( Integer portID) {
	super.setPortID( portID );
	getPortTerminalServer().setPortID(portID);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.port.PortTerminalServer
 */
public void setPortTerminalServer(PortTerminalServer newValue) {
	this.portTerminalServer = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();
	getPortTerminalServer().update();
}
}

package com.cannontech.database.data.port;

/**
 * This type was created in VisualAge.
 */ 
public class PortDialBack extends LocalSharedPort 
{
	
	private com.cannontech.database.db.port.PortDialBack portDialbackModem = null;
	
/**
 * LocalDialupPort constructor comment.
 */
public PortDialBack() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException{
	super.add();
	getPortDialback().add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {
	getPortDialback().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.port.PortDialBack
 */
public com.cannontech.database.db.port.PortDialBack getPortDialback() 
{
	if( portDialbackModem == null )
		portDialbackModem = new com.cannontech.database.db.port.PortDialBack();
		
	return portDialbackModem;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{
	super.retrieve();
	getPortDialback().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getPortDialback().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.port.PortDialupModem
 */
public void setPortDialupModem(com.cannontech.database.db.port.PortDialBack newValue) {
	this.portDialbackModem = newValue;
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public void setPortID(Integer portID) {
	super.setPortID( portID );
	getPortDialback().setPortID(portID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();
	getPortDialback().update();
}
}

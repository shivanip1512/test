package com.cannontech.database.db.port;

/**
 * This type was created in VisualAge.
 */

 import com.cannontech.database.db.DBPersistent;
 
public class PortTerminalServer extends DBPersistent {
	
	private String ipAddress = null;
	private Integer socketPortNumber = null;
	private Integer portID = null;
/**
 * PortTerminalServer constructor comment.
 */
public PortTerminalServer() {
	super();
	initialize( null, null, null );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public PortTerminalServer( Integer portNumber) {
	super();
	initialize( portNumber, null, null );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 * @param ipAddress java.lang.String
 * @param socketPortNumber java.lang.Integer
 */
public PortTerminalServer( Integer portNumber, String ipAddress, Integer socketPortNumber) {
	super();
	initialize( portNumber, ipAddress, socketPortNumber );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getPortID(), getIpAddress(), getSocketPortNumber() };
	
	add( "PortTerminalServer", addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	
	delete("PortTerminalServer", "PortID", getPortID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getIpAddress() {
	return ipAddress;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPortID() {
	return portID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getSocketPortNumber() {
	return socketPortNumber;
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 * @param ipAddress java.lang.String
 * @param socketPortNumber java.lang.Integer
 */
public void initialize( Integer portID, String ipAddress, Integer socketPortNumber ) {

	setPortID( portID );
	setIpAddress( ipAddress );
	setSocketPortNumber( socketPortNumber );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	
	String selectColumns[] = { "IPAddress", "SocketPortNumber" };
	String constraintColumns[] = { "PortID" };
	Object constraintValues[] = { getPortID() };
	
	Object results[] = retrieve( selectColumns, "PortTerminalServer", constraintColumns, constraintValues );
	if( results.length == selectColumns.length )
	{
		setIpAddress( (String) results[0] );
		setSocketPortNumber( (Integer) results[1] );
	}

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setIpAddress(String newValue) {
	this.ipAddress = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPortID(Integer newValue) {
	this.portID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setSocketPortNumber(Integer newValue) {
	this.socketPortNumber = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	
	String setColumns[] = { "IPAddress", "SocketPortNumber" };
	Object setValues[] = { getIpAddress(), getSocketPortNumber() };

	String constraintColumns[] = { "PortID" };
	Object constraintValues[] = { getPortID() };
	
	update( "PortTerminalServer", setColumns, setValues, constraintColumns, constraintValues );
}
}

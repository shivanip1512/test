package com.cannontech.database.db.port;

/**
 * This type was created in VisualAge.
 */

 import com.cannontech.database.db.DBPersistent;
 
public class PortLocalSerial extends DBPersistent {
	
	private String physicalPort = null;
	private Integer portID = null;
/**
 * SerialPortSettings constructor comment.
 */
public PortLocalSerial() {
	super();
	initialize( null, null );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 */
public PortLocalSerial( Integer portNumber ) {
	super();
	initialize( portNumber, null );
}
/**
 * This method was created in VisualAge.
 * @param portNumber java.lang.Integer
 * @param physicalPort java.lang.String
 * @param baudRate java.lang.Integer
 * @param cdWait java.lang.Integer
 */
public PortLocalSerial( Integer portNumber, String physicalPort ) {
	super();

	initialize( portNumber, physicalPort );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getPortID(), getPhysicalPort()  };
	
	add( "PortLocalSerial", addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( "PortLocalSerial", "PortID", getPortID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getPhysicalPort() {
	return physicalPort;
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
 * @param portNumber java.lang.Integer
 * @param physicalPort java.lang.String
 * @param baudRate java.lang.Integer
 * @param cdWait java.lang.Integer
 */
public void initialize( Integer portID, String physicalPort ) {

	setPortID( portID );
	setPhysicalPort( physicalPort );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String columnNames[] = { "PhysicalPort" };
	String constraintColumnNames[] = { "PortID" };
	Object constraintColumnValues[] = { getPortID() };
	
	Object results[] = retrieve( columnNames, "PortLocalSerial", constraintColumnNames, constraintColumnValues);
	if( results.length == columnNames.length )
	{
		setPhysicalPort( (String) results[0] );
	}
	
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setPhysicalPort(String newValue) {
	this.physicalPort = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPortID(Integer newValue) {
	this.portID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "PhysicalPort" };
	Object setValues[] = { getPhysicalPort() };

	String constraintColumns[] = { "PortID" };
	Object constraintValues[] = { getPortID() };
	
	update("PortLocalSerial", setColumns, setValues, constraintColumns, constraintValues);
}
}

package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */
public class DeviceIED extends com.cannontech.database.db.DBPersistent {
	private Integer deviceID = null;
	private String password = null;
	private String slaveAddress = null;

	private String tableName = "DeviceIED";
/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceIED() {
	super();
	initialize( null, null, null );
}
/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceIED(Integer deviceID) {
	super();
	initialize( deviceID, null, null );
}
/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceIED(Integer deviceID, String password, String slaveAddress ) {
	super();
	initialize( deviceID, password, slaveAddress );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object[] addValues = { getDeviceID(), getPassword(), getSlaveAddress() };

	add( this.tableName, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( this.tableName, "DeviceID", getDeviceID() );
	
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getPassword() {
	return password;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getSlaveAddress() {
	return slaveAddress;
}
/**
 * This method was created in VisualAge.
 */
public void initialize( Integer deviceID, String password, String slaveAddress ) {

	setDeviceID( deviceID );
	setPassword( password );
	setSlaveAddress( slaveAddress );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "Password", "SlaveAddress" };
	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( selectColumns, this.tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setPassword( (String) results[0] );
		setSlaveAddress( (String) results[1] );
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPassword(String newValue) {
	this.password = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setSlaveAddress(String newValue) {
	this.slaveAddress = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "Password", "SlaveAddress" };
	Object setValues[]= { getPassword(), getSlaveAddress() };

	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	update( this.tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}

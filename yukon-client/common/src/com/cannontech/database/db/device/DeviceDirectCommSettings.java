package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */
public class DeviceDirectCommSettings extends com.cannontech.database.db.DBPersistent 
{	
	private Integer deviceID = null;
	private Integer portID = null;

    public static final String COMMPORT_TABLE_NAME = "Commport";
	public static final String TABLE_NAME = "DeviceDirectCommSettings";
/**
 * DeviceDirectCommSettings constructor comment.
 */
public DeviceDirectCommSettings() {
	super();
	initialize( null, null );
}
/**
 * DeviceDirectCommSettings constructor comment.
 */
public DeviceDirectCommSettings(Integer deviceID) {
	super();
	initialize( deviceID, null );
}
/**
 * DeviceDirectCommSettings constructor comment.
 */
public DeviceDirectCommSettings(Integer deviceID, Integer portNumber) {
	super();
	initialize( deviceID, portNumber );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[] = { getDeviceID(), getPortID() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( TABLE_NAME, "DeviceID", getDeviceID() );
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
 * @return java.lang.Integer
 */
public Integer getPortID() {
	return portID;
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 * @param portNumber java.lang.Integer
 * @param address java.lang.Integer
 * @param postCommWait java.lang.Integer
 */
public void initialize( Integer deviceID, Integer portID ) {
	setDeviceID( deviceID );
	setPortID( portID );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	String selectColumns[] = { "PortID" };
	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( selectColumns, TABLE_NAME, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setPortID( (Integer)results[0] );
	}
}
public void setDefaultPortID() throws java.sql.SQLException, Exception
{
    String selectColumns[] = { "PortID" };
    String constraintColumns[] = {};
    Object constraintValues[] = {};
    Object results[] = retrieve( selectColumns, COMMPORT_TABLE_NAME, constraintColumns, constraintValues );

    if( results.length == selectColumns.length )
    {
        setPortID( (Integer)results[0] );
    }else
        throw new Exception("No CommPorts set up in the database");
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
public void setPortID(Integer newValue) {
	this.portID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	String setColumns[] = { "PortID" };
	Object setValues[] = { getPortID() };

	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues );
}
}

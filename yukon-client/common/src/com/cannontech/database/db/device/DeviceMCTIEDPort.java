package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */
public class DeviceMCTIEDPort extends com.cannontech.database.db.DBPersistent {
	private Integer deviceID = null;
	private String connectedIED = null;
	private Integer iedScanRate = null;
	private Integer defaultDataClass = null;
	private Integer defaultDataOffset = null;
	private String password = null;
	private Character realTimeScan = null;

	private String tableName = "DeviceMCTIEDPort";
/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceMCTIEDPort() {
	super();
	initialize( null, null, null, null, null, null, null );
}
/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceMCTIEDPort(Integer deviceID) {
	super();
	initialize( deviceID, null, null, null, null, null, null );
}
/**
 * Insert the method's description here.
 * Creation date: (6/18/2001 11:46:16 AM)
 * @param deviceID java.lang.Integer
 * @param connectedIED java.lang.String
 * @param iedScanRate java.lang.Integer
 * @param defaultDataClass java.lang.Integer
 * @param defaultDataOffset java.lang.Integer
 * @param password java.lang.String
 * @param realTimeScan java.lang.Character
 */
public DeviceMCTIEDPort(Integer deviceID, String connectedIED, Integer iedScanRate, Integer defaultDataClass, Integer defaultDataOffset, String password, Character realTimeScan)
{	super();
	initialize( deviceID, connectedIED, iedScanRate, defaultDataClass, defaultDataOffset, password, realTimeScan );
	
	}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object[] addValues = { getDeviceID(), getConnectedIED(), getIEDScanRate(), getDefaultDataClass(), getDefaultDataOffset(), getPassword(), getRealTimeScan() };

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
 * @return java.lang.String
 */
public String getConnectedIED() {
	return connectedIED;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDefaultDataClass() {
	return defaultDataClass;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDefaultDataOffset() {
	return defaultDataOffset;
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
public Integer getIEDScanRate() {
	return iedScanRate;
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
 * @return java.lang.Integer
 */
public Character getRealTimeScan() {
	return realTimeScan;
}
/**
 * This method was created in VisualAge.
 */
public void initialize( Integer deviceID, String connectedIED, Integer iedScanRate, Integer defaultDataClass, Integer defaultDataOffset, String password, Character realTimeScan ) {

	setDeviceID( deviceID );
	setConnectedIED( connectedIED );
	setIEDScanRate( iedScanRate );
	setDefaultDataClass( defaultDataClass );
	setDefaultDataOffset( defaultDataOffset );
	setPassword( password );
	setRealTimeScan( realTimeScan );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "ConnectedIED", "IEDScanRate", "DefaultDataClass", "DefaultDataOffset", "Password", "RealTimeScan" };
	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( selectColumns, this.tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setConnectedIED( (String) results[0] );
		setIEDScanRate( (Integer) results[1] );
		setDefaultDataClass( (Integer) results[2] );
		setDefaultDataOffset( (Integer) results[3] );
		setPassword( (String) results[4] );
		setRealTimeScan( new Character(((String)results[5]).charAt(0)) );
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setConnectedIED(String newValue) {
	this.connectedIED = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDefaultDataClass(Integer newValue) {
	this.defaultDataClass = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDefaultDataOffset(Integer newValue) {
	this.defaultDataOffset = newValue;
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
public void setIEDScanRate(Integer newValue) {
	this.iedScanRate = newValue;
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
public void setRealTimeScan(Character newValue) {
	this.realTimeScan = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "ConnectedIED", "IEDScanRate", "DefaultDataClass", "DefaultDataOffset", "Password", "RealTimeScan" };
	Object setValues[]= { getConnectedIED(), getIEDScanRate(), getDefaultDataClass(), getDefaultDataOffset(), getPassword(), getRealTimeScan() };

	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	update( this.tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}

package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */
public class DeviceTapPagingSettings extends com.cannontech.database.db.DBPersistent {
	private Integer deviceID = null;
	private String pagerNumber = null;

	private String tableName = "DeviceTapPagingSettings";
/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceTapPagingSettings() {
	super();
	initialize( null, null );
}
/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceTapPagingSettings(Integer deviceID) {
	super();
	initialize( deviceID, null );
}
/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceTapPagingSettings(Integer deviceID, String pagerNumber ) {
	super();
	initialize( deviceID, pagerNumber );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object[] addValues = { getDeviceID(), getPagerNumber() };

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
 * @return java.lang.Integer
 */
public String getPagerNumber() {
	return pagerNumber;
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 * @param monthlyStats java.lang.Character
 * @param twentyFourHourStats java.lang.Character
 * @param hourlyStats java.lang.Character
 * @param failureAlarm java.lang.Character
 * @param performThreshold java.lang.Integer
 * @param performAlarm java.lang.Character
 * @param performTwentyFourAlarm java.lang.Character
 * @param configurationName java.lang.String
 */
public void initialize( Integer deviceID, String pagerNumber ) {

	setDeviceID( deviceID );
	setPagerNumber( pagerNumber );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "PagerNumber" };
	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( selectColumns, this.tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setPagerNumber( (String) results[0] );
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
public void setPagerNumber(String newValue) {
	this.pagerNumber = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "PagerNumber" };
	Object setValues[]= { getPagerNumber() };

	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	update( this.tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}

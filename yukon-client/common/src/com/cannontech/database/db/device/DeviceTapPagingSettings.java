package com.cannontech.database.db.device;

import com.cannontech.common.util.CtiUtilities;
/**
 * This type was created in VisualAge.
 */
public class DeviceTapPagingSettings extends com.cannontech.database.db.DBPersistent {
	private Integer deviceID = null;
	private String pagerNumber = null;
	private String sender = CtiUtilities.STRING_NONE;
	private String securityCode = CtiUtilities.STRING_NONE;
	private String postPath = CtiUtilities.STRING_NONE;
	
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
	Object[] addValues = { getDeviceID(), getPagerNumber(), getSender(),
							getSecurityCode(), getPOSTPath() };

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

public String getSender() {
	return sender;
}

public String getSecurityCode() {
	return securityCode;
}

public String getPOSTPath() {
	return postPath;
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

	String selectColumns[] = { "PagerNumber", "Sender", "SecurityCode", "POSTPath" };
	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( selectColumns, this.tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setPagerNumber( (String) results[0] );
		setSender( (String) results[1] );
		setSecurityCode( (String) results[2] );
		setPOSTPath( (String) results[3] );
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

public void setSender(String newValue) {
	this.sender = newValue;
}

public void setSecurityCode(String newValue) {
	this.securityCode = newValue;
}

public void setPOSTPath(String newValue) {
	this.postPath = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "PagerNumber", "Sender", "SecurityCode", "POSTPath" };
	Object setValues[]= { getPagerNumber(), getSender(), getSecurityCode(), getPOSTPath() };

	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	update( this.tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}

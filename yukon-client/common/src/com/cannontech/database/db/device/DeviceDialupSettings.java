package com.cannontech.database.db.device;

import com.cannontech.common.util.CtiUtilities;

/**
 * This type was created in VisualAge.
 */
public class DeviceDialupSettings extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private String phoneNumber = null;
	private Integer minConnectTime = new Integer(CtiUtilities.NONE_ID);
	private Integer maxConnectTime = new Integer(10);
	private String lineSettings = "8N1";
	private Integer baudRate = new Integer(CtiUtilities.NONE_ID);

	public static final String SETTER_COLUMNS[] = 
	{ 
		"PhoneNumber", "MinConnectTime", "MaxConnectTime",
		"LineSettings", "BaudRate"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
	
	public static final String TABLE_NAME = "DeviceDialupSettings";

/**
 * DeviceDialupSettings constructor comment.
 */
public DeviceDialupSettings() 
{
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	if ( getPhoneNumber() != null )//big time hack
	{
		Object addValues[] = { getDeviceID(), getPhoneNumber(), 
			getMinConnectTime(), getMaxConnectTime(), getLineSettings(), getBaudRate() };

		add( TABLE_NAME, addValues );
	}
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID() );
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 1:07:14 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getBaudRate() {
	return baudRate;
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
public String getLineSettings() {
	return lineSettings;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getMaxConnectTime() {
	return maxConnectTime;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getMinConnectTime() {
	return minConnectTime;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getPhoneNumber() {
	return phoneNumber;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID() } ;

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

	if( results.length == SETTER_COLUMNS.length )
	{
		setPhoneNumber( (String) results[0] );
		setMinConnectTime( (Integer) results[1] );
		setMaxConnectTime( (Integer) results[2] );
		setLineSettings( (String) results[3] );
		setBaudRate( (Integer) results[4] );
	}
	//do not throw the ERROR here if we dont get back any columns!!!!

}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 1:07:14 PM)
 * @param newBaudRate java.lang.Integer
 */
public void setBaudRate(java.lang.Integer newBaudRate) {
	baudRate = newBaudRate;
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
 * @param newValue java.lang.String
 */
public void setLineSettings(String newValue) {
	this.lineSettings = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setMaxConnectTime(Integer newValue) {
	this.maxConnectTime = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setMinConnectTime(Integer newValue) {
	this.minConnectTime = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setPhoneNumber(String newValue) {
	this.phoneNumber = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID() );
	
	if( getPhoneNumber() != null )//big time hack
	{
		Object addValues[] = { getDeviceID(), getPhoneNumber(), 
				getMinConnectTime(), getMaxConnectTime(), getLineSettings(), getBaudRate() };

		add( TABLE_NAME, addValues );
	}

}
}

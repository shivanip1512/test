package com.cannontech.database.db.device;

import java.sql.*;
import com.cannontech.database.db.*;
/**
 * This type was created in VisualAge.
 */
public class DevicePAOCompatible extends com.cannontech.database.db.DBPersistent 
{	
	private Integer deviceID = null;
	private Character alarmInhibit = null;
	private Character controlInhibit = null;
/**
 * Device constructor comment.
 */
public DevicePAOCompatible() 
{
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	
	Object addValues[] = { getDeviceID(), getAlarmInhibit(), getControlInhibit() };

	add( "Device", addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( "Device", "DeviceID", getDeviceID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getAlarmInhibit() {
	return alarmInhibit;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getControlInhibit() {
	return controlInhibit;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	String selectColumns[] = { "AlarmInhibit", "ControlInhibit"  };
	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };
	
	Object results[] = retrieve( selectColumns, "Device", constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setAlarmInhibit( new Character( ((String) results[0]).charAt(0)) );
		setControlInhibit( new Character( ((String) results[1]).charAt(0)) );
	}

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setAlarmInhibit(Character newValue) {
	this.alarmInhibit = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setControlInhibit(Character newValue) {
	this.controlInhibit = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "AlarmInhibit", "ControlInhibit" };
	Object setValues[] = { getAlarmInhibit(), getControlInhibit() };

	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };
	
	update( "Device", setColumns, setValues, constraintColumns, constraintValues );
}
}

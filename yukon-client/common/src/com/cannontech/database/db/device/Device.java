package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */
public class Device extends com.cannontech.database.db.DBPersistent 
{
	public static final int SYSTEM_DEVICE_ID = 0;
	
	private Integer deviceID = null;
	private Character alarmInhibit = new Character( 'N' );
	private Character controlInhibit = new Character( 'N' );

	public static final String SETTER_COLUMNS[] = 
	{ 
		"AlarmInhibit", "ControlInhibit"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
	
	public static final String TABLE_NAME = "Device";
/**
 * Device constructor comment.
 */
public Device() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{	
	Object addValues[] = { getDeviceID(), getAlarmInhibit(), getControlInhibit() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID() );
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
	Object constraintValues[] = { getDeviceID() };
	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
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
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getAlarmInhibit(), getControlInhibit() };

	Object constraintValues[] = { getDeviceID() };
	
	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}

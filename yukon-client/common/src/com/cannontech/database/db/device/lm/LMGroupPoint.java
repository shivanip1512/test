package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */
public class LMGroupPoint extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private Integer deviceIDUsage = new Integer(0);
	private Integer pointIDUsage = new Integer(0);
	private Integer startControlRawState = new Integer(0);

	public static final String SETTER_COLUMNS[] = 
	{ 
		"DeviceIDUsage", "PointIDUsage", "StartControlRawState"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMGroupPoint";
/**
 * LMGroupVersacom constructor comment.
 */
public LMGroupPoint() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getDeviceIDUsage(), 
		getPointIDUsage(), getStartControlRawState() };

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
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2002 12:02:26 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDeviceIDUsage() {
	return deviceIDUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2002 12:02:26 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPointIDUsage() {
	return pointIDUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2002 12:02:26 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getStartControlRawState() {
	return startControlRawState;
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
		setDeviceIDUsage( (Integer) results[0] );
		setPointIDUsage( (Integer) results[1] );
		setStartControlRawState( (Integer) results[2] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2002 12:02:26 PM)
 * @param newDeviceIDUsage java.lang.Integer
 */
public void setDeviceIDUsage(java.lang.Integer newDeviceIDUsage) {
	deviceIDUsage = newDeviceIDUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2002 12:02:26 PM)
 * @param newPointIDUsage java.lang.Integer
 */
public void setPointIDUsage(java.lang.Integer newPointIDUsage) {
	pointIDUsage = newPointIDUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2002 12:02:26 PM)
 * @param newStartControlRawState java.lang.Integer
 */
public void setStartControlRawState(java.lang.Integer newStartControlRawState) {
	startControlRawState = newStartControlRawState;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getDeviceIDUsage(), 
		getPointIDUsage(), getStartControlRawState() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}

package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */
public class LMGroupRipple extends com.cannontech.database.db.DBPersistent {
	private Integer deviceID = null;
	private Integer routeID = null;
	private Integer shedTime = null; //stored in seconds
	private String control = null;
	private String restore = null;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"RouteID", "ShedTime", "ControlValue", "RestoreValue"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMGroupRipple";
/**
 * LMGroupVersacom constructor comment.
 */
public LMGroupRipple() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[] = { getDeviceID(), getRouteID(), 
				getShedTime(), getControl(), getRestore() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID() );
}
/**
 * Insert the method's description here.
 * Creation date: (7/17/2001 9:13:54 AM)
 * @return java.lang.String
 */
public String getControl() {
	return control;
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
 * Creation date: (7/17/2001 9:14:44 AM)
 * @return java.lang.String
 */
public String getRestore()
{
    return restore;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRouteID() {
	return routeID;
}
/**
 * Insert the method's description here.
 * Creation date: (7/17/2001 9:12:46 AM)
 * @return java.lang.Integer
 */
public Integer getShedTime() {
	return shedTime;
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
		setRouteID( (Integer) results[0] );
		setShedTime( (Integer) results[1] );
		setControl( (String) results[2] );
		setRestore( (String) results[3] );
		
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (7/17/2001 9:14:16 AM)
 * @param newValue java.lang.String
 */
public void setControl(String newValue)
{
    control = newValue;
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
 * Creation date: (7/17/2001 9:15:14 AM)
 * @param newValue java.lang.String
 */
public void setRestore(String newValue)
{
    restore = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRouteID(Integer newValue) {
	this.routeID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (7/17/2001 9:13:19 AM)
 * @param newValue java.lang.Integer
 */
public void setShedTime(Integer newValue)
{
	shedTime = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getRouteID(), getShedTime(), getControl(), getRestore() };
	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}

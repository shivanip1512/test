package com.cannontech.database.db.device;

/**
 * This type was created by Cannon Technologies Inc.
 */
public class DeviceLoadProfile extends com.cannontech.database.db.DBPersistent {
	private Integer deviceID = null;
	private Integer lastIntervalDemandRate = new Integer(300);
	private Integer loadProfileDemandRate = new Integer(300);
	private String loadProfileCollection = "NNNN";
/**
 * DeviceLoadProfile constructor comment.
 */
public DeviceLoadProfile() {
	super();
}

/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getDeviceID(), getLastIntervalDemandRate(), getLoadProfileDemandRate(), getLoadProfileCollection() };

	add( "DeviceLoadProfile", addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( "DeviceLoadProfile", "DeviceID", getDeviceID() );
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
public Integer getLastIntervalDemandRate() {
	return lastIntervalDemandRate;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public String getLoadProfileCollection() {
	return loadProfileCollection;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getLoadProfileDemandRate() {
	return loadProfileDemandRate;
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "LastIntervalDemandRate", "LoadProfileDemandRate", "LoadProfileCollection" };
	String constraintColumns[] = { "DeviceID" };

	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( selectColumns, "DeviceLoadProfile", constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setLastIntervalDemandRate( (Integer) results[0] );
		setLoadProfileDemandRate( (Integer) results[1] );
		setLoadProfileCollection( (String) results[2] );
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
public void setLastIntervalDemandRate(Integer newValue) {
	this.lastIntervalDemandRate = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setLoadProfileCollection(String newValue) {
	this.loadProfileCollection = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setLoadProfileDemandRate(Integer newValue) {
	this.loadProfileDemandRate = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "LastIntervalDemandRate", "LoadProfileDemandRate", "LoadProfileCollection" };
	Object setValues[] = { getLastIntervalDemandRate(), getLoadProfileDemandRate(), getLoadProfileCollection() };

	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	update( "DeviceLoadProfile", setColumns, setValues, constraintColumns, constraintValues );
}
}

package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */

public class DeviceRoutes extends com.cannontech.database.db.DBPersistent {
	private Integer deviceID = null;
	private Integer routeID = null;

	public static final String SETTER_COLUMNS[] = { "RouteID" };

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
	public static final String TABLE_NAME = "DeviceRoutes";
/**
 * DeviceRoutes constructor comment.
 */
public DeviceRoutes() {
	super();
	initialize( null, null );
}
/**
 * DeviceRoutes constructor comment.
 */
public DeviceRoutes(Integer deviceID, Integer routeID) {
	super();
	initialize( deviceID, routeID );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getDeviceID(), getRouteID() };

	add( this.TABLE_NAME, addValues ); 
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	Object values[] = { getDeviceID() };
	delete( this.TABLE_NAME, CONSTRAINT_COLUMNS, values);
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
public Integer getRouteID() {
	return routeID;
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 * @param routeOrder java.lang.Integer
 * @param routeName java.lang.String
 */
public void initialize(Integer devID, Integer rtID ) {
	deviceID = devID;
	routeID = rtID;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setRouteID( (Integer) results[0] );
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
public void setRouteID(Integer newValue) {
	this.routeID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	
	Object setValues[] = { getRouteID() };
	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}

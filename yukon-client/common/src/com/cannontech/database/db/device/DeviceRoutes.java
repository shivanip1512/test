package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */

public class DeviceRoutes extends com.cannontech.database.db.DBPersistent {
	private Integer deviceID = null;
	private Integer routeID = null;

	private final String tableName = "DeviceRoutes";
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

	add( this.tableName, addValues ); 
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	String deleteColumns[] = { "DeviceID" };
	Object deleteValues[] = { getDeviceID() };
	
	delete( this.tableName, deleteColumns, deleteValues );
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

	String selectColumns[] = { "RouteID" };

	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( selectColumns, this.tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
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

	String setColumns[] = { "RouteID" };
	Object setValues[] = { getRouteID() };

	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	update( this.tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}

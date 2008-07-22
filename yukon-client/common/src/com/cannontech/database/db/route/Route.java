package com.cannontech.database.db.route;

/**
 * This type was created in VisualAge.
 */

public class Route extends com.cannontech.database.db.DBPersistent 
{	
	private Integer routeID = null;
	private Integer deviceID = null;
	private String defaultRoute = null;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"DeviceID", "DefaultRoute"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "RouteID" };
	
	public static final String TABLE_NAME = "Route";

/**
 * Route constructor comment.
 */
public Route() 
{
	super();
}
/**
 * Route constructor comment.
 */
public Route(Integer newRouteID, Integer newDeviceID, String newDefRoute) 
{
	super();
	
	setRouteID( routeID );
	setDeviceID( newDeviceID );
	setDefaultRoute( newDefRoute );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{	
	Object addValues[] = { getRouteID(), getDeviceID(), getDefaultRoute() };
	
	add( TABLE_NAME, addValues);
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
    delete("SubstationToRouteMapping", "RouteID", getRouteID()); 
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getRouteID() );
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 9:59:20 AM)
 * @return java.lang.String
 */
public java.lang.String getDefaultRoute() {
	return defaultRoute;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 9:59:20 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDeviceID() {
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
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getRouteID() };

	Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setDeviceID( (Integer) results[0] );
		setDefaultRoute( (String) results[1] );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 9:59:20 AM)
 * @param newDefaultRoute java.lang.String
 */
public void setDefaultRoute(java.lang.String newDefaultRoute) {
	defaultRoute = newDefaultRoute;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 9:59:20 AM)
 * @param newDeviceID java.lang.Integer
 */
public void setDeviceID(java.lang.Integer newDeviceID) {
	deviceID = newDeviceID;
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
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getDeviceID(), getDefaultRoute() };
	
	Object constraintValues[] = { getRouteID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}

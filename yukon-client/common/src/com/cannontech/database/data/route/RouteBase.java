package com.cannontech.database.data.route;

import com.cannontech.database.db.*;
import com.cannontech.database.db.route.*;

/**
 * This type was created in VisualAge.
 */
public class RouteBase extends com.cannontech.database.data.pao.YukonPAObject implements com.cannontech.common.editor.EditorPanel
{
	private Route route = null;
/**
 * RouteBase constructor comment.
 */
public RouteBase() 
{
	super();

	initialize();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	
	if( getRoute().getRouteID() == null )
		setRouteID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );

	super.add();
	getRoute().add();	
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	getRoute().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj com.cannontech.database.data.route.RouteBase
 */
public boolean equals(Object obj) {

	if( obj instanceof RouteBase )
		return getRoute().getRouteID().equals( ((RouteBase) obj).getRoute().getRouteID() );
	else
		return super.equals(obj);
}
/**
 * This method was created in VisualAge.
 * @param routeID java.lang.Integer
 */
public String getDefaultRoute()
{
	return getRoute().getDefaultRoute();
}
/**
 * This method was created in VisualAge.
 * @param routeID java.lang.Integer
 */
public Integer getDeviceID()
{
	return getRoute().getDeviceID();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.route.Route
 */
private Route getRoute() {
	if( route == null )
		route = new Route();
		
	return route;
}
/**
 * This method was created in VisualAge.
 * @param routeID java.lang.Integer
 */
public Integer getRouteID()
{
	return getRoute().getRouteID();
}
/**
 * This method was created in VisualAge.
 * @param routeID java.lang.Integer
 */
public String getRouteName()
{
	return getYukonPAObject().getPaoName();
}
/**
 * This method was created in VisualAge.
 */
private void initialize()
{
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	getRoute().retrieve();
	super.retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);
	getRoute().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param routeID java.lang.Integer
 */
public void setDefaultRoute(String newDefRoute) 
{
	getRoute().setDefaultRoute(newDefRoute);
}
/**
 * This method was created in VisualAge.
 * @param routeID java.lang.Integer
 */
public void setDeviceID( Integer newDevID )
{
	getRoute().setDeviceID( newDevID );
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.route.Route
 */
public void setRoute(Route newValue) {
	this.route = newValue;
}
/**
 * This method was created in VisualAge.
 * @param routeID java.lang.Integer
 */
public void setRouteID(Integer routeID) 
{
	super.setPAObjectID( routeID );

	getRoute().setRouteID(routeID);
}
/**
 * This method was created in VisualAge.
 * @param routeID java.lang.Integer
 */
public void setRouteName(String newRouteName) 
{
	getYukonPAObject().setPaoName( newRouteName);
}
/**
 * This method was created in VisualAge.
 */
public void setRouteType( String value )
{
	getYukonPAObject().setType( value );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString()
{
	return getPAOName();
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	getRoute().update();
	super.update();
}
}

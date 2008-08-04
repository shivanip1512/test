package com.cannontech.database.data.route;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.route.CarrierRoute;
import com.cannontech.database.db.route.RepeaterRoute;

/**
 * This type was created in VisualAge.
 */
public class CCURoute extends RouteBase 
{
	private CarrierRoute carrierRoute = null;

   //contains com.cannontech.database.db.route.RepeaterRoute instances
	private java.util.Vector<RepeaterRoute> repeaterVector = null;
/**
 * CCURoute constructor comment.
 */
public CCURoute() 
{
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getCarrierRoute().add();

	for( int i = 0; i < getRepeaterVector().size(); i++ )
		((DBPersistent) getRepeaterVector().elementAt(i)).add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException 
{
   RepeaterRoute rArray[] = RepeaterRoute.getRepeaterRoutes( getRouteID(), getDbConnection() );
   for( int i = 0; i < rArray.length; i++ )
   {
      rArray[i].setDbConnection( getDbConnection() );
      rArray[i].delete();
      rArray[i].setDbConnection( null );
   }

	getCarrierRoute().delete();
   		
	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.route.CarrierRoute
 */
public CarrierRoute getCarrierRoute() {
	if( carrierRoute == null )
		carrierRoute = new CarrierRoute();
	return carrierRoute;
}
/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 */
public java.util.Vector<RepeaterRoute> getRepeaterVector() {
	
	//Make sure to instantiate all the repeaters we'll need
	//otherwise on a delete they will be skipped
	//and probably cause constraint violations
	
	if( repeaterVector == null )
		repeaterVector = new java.util.Vector<RepeaterRoute>();
	
	return repeaterVector;
	
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException 
{
	super.retrieve();
	getCarrierRoute().retrieve();

	//Need to get all of the  rows.....
	repeaterVector = new java.util.Vector<RepeaterRoute>();

	try
	{		
		RepeaterRoute rArray[] = RepeaterRoute.getRepeaterRoutes( getRouteID(), getDbConnection() );
		for( int i = 0; i < rArray.length; i++ )
			repeaterVector.addElement( rArray[i] );
	
	}
	catch(java.sql.SQLException e )
	{
		//not necessarily an error
	}
	
	
	for( int i = 0; i < getRepeaterVector().size(); i++ ) 
   {
		((DBPersistent) getRepeaterVector().elementAt(i)).setDbConnection(getDbConnection());
		((DBPersistent) getRepeaterVector().elementAt(i)).retrieve();
		((DBPersistent) getRepeaterVector().elementAt(i)).setDbConnection(null);
			
   }
}

/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.route.CarrierRoute
 */
public void setCarrierRoute(CarrierRoute newValue) {
	this.carrierRoute = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getCarrierRoute().setDbConnection(conn);

	java.util.Vector<RepeaterRoute> v = getRepeaterVector();

	if( v != null )
	{
		for( int i = 0; i < v.size(); i++ )
			((DBPersistent) v.elementAt(i)).setDbConnection(conn);
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue java.util.Vector
 */
public void setRepeaterVector(java.util.Vector<RepeaterRoute> newValue) {
	this.repeaterVector = newValue;
}
/**
 * This method was created in VisualAge.
 * @param routeID java.lang.Integer
 */
public void setRouteID(Integer routeID) {
	super.setRouteID(routeID);
	getCarrierRoute().setRouteID(routeID);

	for( int i = 0; i < getRepeaterVector().size(); i++ )
		getRepeaterVector().elementAt(i).setRouteID(routeID);	
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getCarrierRoute().update();

	RepeaterRoute.deleteRepeaterRoutes( getRouteID(), getDbConnection() );
	 	
	for( int i = 0; i < getRepeaterVector().size(); i++ )
		((DBPersistent) getRepeaterVector().elementAt(i)).add();
}
}

package com.cannontech.database.data.route;

/**
 * This type was created in VisualAge.
 */
public class MacroRoute extends RouteBase {
	
	private java.util.Vector macroRouteVector = null;
/**
 * MacroRoute constructor comment.
 */
public MacroRoute() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();

	for( int i = 0; i < getMacroRouteVector().size(); i++ )
		((com.cannontech.database.db.route.MacroRoute) getMacroRouteVector().elementAt(i)).add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException{

	com.cannontech.database.db.route.MacroRoute.deleteAllMacroRoutes( getRouteID() );

	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 */
public java.util.Vector getMacroRouteVector() {

	if( macroRouteVector == null )
		macroRouteVector = new java.util.Vector();
	
	return macroRouteVector;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{
	super.retrieve();

		macroRouteVector = new java.util.Vector();

		try
		{
			
			com.cannontech.database.db.route.MacroRoute rArray[] = com.cannontech.database.db.route.MacroRoute.getMacroRoutes( getRouteID() );

			for( int i = 0; i < rArray.length; i++ )
			{
				//Since we are in the process of doing a retrieve
				//we need to make sure the new macro routes have a database connection to use
				//otherwise we bomb below
				rArray[i].setDbConnection( getDbConnection() );
				macroRouteVector.addElement( rArray[i] );
			}
		
		}
		catch(java.sql.SQLException e )
		{
			//not necessarily an error
		}
	
	//This necessary??
	for( int i = 0; i < getMacroRouteVector().size(); i++ )
		((com.cannontech.database.db.route.MacroRoute) getMacroRouteVector().elementAt(i)).retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	java.util.Vector v = getMacroRouteVector();

	if( v != null )
	{
		for( int i = 0; i < v.size(); i++ )
			((com.cannontech.database.db.route.MacroRoute) v.elementAt(i)).setDbConnection(conn);
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue java.util.Vector
 */
public void setMacroRouteVector(java.util.Vector newValue) {
	this.macroRouteVector = newValue;
}
/**
 * This method was created in VisualAge.
 * @param routeID java.lang.Integer
 */
public void setRouteID(Integer routeID) {

	super.setRouteID(routeID);

	for( int i = 0; i < getMacroRouteVector().size(); i++ )
		((com.cannontech.database.db.route.MacroRoute) getMacroRouteVector().elementAt(i)).setRouteID(routeID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();

	com.cannontech.database.db.route.MacroRoute.deleteAllMacroRoutes( getRouteID() );
	
	for( int i = 0; i < getMacroRouteVector().size(); i++ )
		((com.cannontech.database.db.route.MacroRoute) getMacroRouteVector().elementAt(i)).add();
}
}

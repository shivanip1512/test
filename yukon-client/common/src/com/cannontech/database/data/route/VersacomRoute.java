package com.cannontech.database.data.route;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.db.*;
import com.cannontech.database.db.route.*;

public class VersacomRoute extends RouteBase {
	private com.cannontech.database.db.route.VersacomRoute versacomRoute = null;
/**
 * VersacomRoute constructor comment.
 */
public VersacomRoute() {
	super();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getVersacomRoute().add();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void delete() throws java.sql.SQLException {
	getVersacomRoute().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.route.VersacomRoute
 */
public com.cannontech.database.db.route.VersacomRoute getVersacomRoute() 
{
	if( versacomRoute == null )
		versacomRoute = new com.cannontech.database.db.route.VersacomRoute();
		
	return versacomRoute;
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void retrieve() throws java.sql.SQLException {
	super.retrieve();
	getVersacomRoute().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getVersacomRoute().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param id java.lang.Integer
 */
public void setRouteID(Integer id) {
	super.setRouteID( id );

	getVersacomRoute().setRouteID(id);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.route.VersacomRoute
 */
public void setVersacomRoute(com.cannontech.database.db.route.VersacomRoute newValue) 
{
	versacomRoute = newValue;
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getVersacomRoute().update();
}
}

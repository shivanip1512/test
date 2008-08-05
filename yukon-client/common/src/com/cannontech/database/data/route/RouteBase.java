package com.cannontech.database.data.route;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.device.lm.LMGroupExpressCom;
import com.cannontech.database.db.device.lm.LMGroupMCT;
import com.cannontech.database.db.device.lm.LMGroupVersacom;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.database.db.route.Route;

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
	if( getRoute().getRouteID() == null ) {
        PaoDao paoDao = DaoFactory.getPaoDao();
        setRouteID(paoDao.getNextPaoId());
    }

	super.add();
	getRoute().add();	
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	deleteFromMacro();
	getRoute().delete();
	super.delete();
}

public void deleteFromMacro() throws java.sql.SQLException 
{
	com.cannontech.database.db.route.MacroRoute.deleteFromMacro( 
		 getRouteID(), getDbConnection() );
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

public final static String hasDevice( Integer routeID ) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement(
			"SELECT PAOName FROM " + 
			com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME + " y, " +
			com.cannontech.database.db.device.DeviceRoutes.TABLE_NAME + " r" +             
			" WHERE r.RouteID=" + routeID +
			" AND r.DeviceID=y.PAObjectID",
			com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

	try
	{
		stmt.execute();
		if(stmt.getRowCount() > 0 )
		 return stmt.getRow(0)[0].toString();
	  else
		 return null;
	}
	catch( Exception e )
	{
		return null;
	}

}

public final static String hasLoadGroup( Integer routeID ) throws SQLException 
{
	SqlStatement expresscomStmt =
		new SqlStatement(
				"SELECT PAOName FROM " + 
				YukonPAObject.TABLE_NAME + " y, " +
				LMGroupExpressCom.TABLE_NAME + " lmg" +             
				" WHERE lmg.RouteID = " + routeID +
				" AND lmg.LMGroupId = y.PAObjectID",
				CtiUtilities.getDatabaseAlias() );

	SqlStatement versacomStmt =
		new SqlStatement(
				"SELECT PAOName FROM " + 
				YukonPAObject.TABLE_NAME + " y, " +
				LMGroupVersacom.TABLE_NAME + " lmg" +             
				" WHERE lmg.RouteID = " + routeID +
				" AND lmg.DeviceID = y.PAObjectID",
				CtiUtilities.getDatabaseAlias() );

	SqlStatement mctStmt =
		new SqlStatement(
				"SELECT PAOName FROM " + 
				YukonPAObject.TABLE_NAME + " y, " +
				LMGroupMCT.TABLE_NAME + " lmg" +             
				" WHERE lmg.RouteID = " + routeID +
				" AND lmg.DeviceID = y.PAObjectID",
				CtiUtilities.getDatabaseAlias() );
	
	try {
		expresscomStmt.execute();
		if(expresscomStmt.getRowCount() > 0 ) {
			return expresscomStmt.getRow(0)[0].toString();
		}

		versacomStmt.execute();
		if(versacomStmt.getRowCount() > 0 ) {
			return versacomStmt.getRow(0)[0].toString();
		}
		
		mctStmt.execute();
		if(mctStmt.getRowCount() > 0 ) {
			return mctStmt.getRow(0)[0].toString();
		}
		
		return null;
		
	} catch( Exception e ) {
		return null;
	}
	
}

public final static String hasRepeater( Integer routeID ) throws SQLException 
{
	SqlStatement stmt =
		new SqlStatement(
				"SELECT PAOName FROM " + 
				YukonPAObject.TABLE_NAME + " y, " +
				RepeaterRoute.TABLE_NAME + " rr" +             
				" WHERE rr.RouteID = " + routeID +
				" AND rr.DeviceID = y.PAObjectID",
				CtiUtilities.getDatabaseAlias() );
	
	try {
		stmt.execute();
		if(stmt.getRowCount() > 0 ) {
			return stmt.getRow(0)[0].toString();
		} else {
			return null;
		}
		
	} catch( Exception e ) {
		return null;
	}
	
}

public final static String inMacroRoute( Integer routeID ) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement(
			"SELECT PAOName FROM " + 
			com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME + " y, " +
			com.cannontech.database.db.route.MacroRoute.TABLE_NAME + " r" +             
			" WHERE r.SingleRouteID=" + routeID +
			" AND r.RouteID=y.PAObjectID",
			com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

	try
	{
		stmt.execute();
		if(stmt.getRowCount() > 0 )
		 return stmt.getRow(0)[0].toString();
	  else
		 return null;
	}
	catch( Exception e )
	{
		return null;
	}

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

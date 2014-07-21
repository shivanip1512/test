package com.cannontech.yukon.server.cache;

import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class PointUnitLoader implements Runnable {
	private java.util.ArrayList allPointsUnits = null;
	private String databaseAlias = null;
/**
 * DeviceLoader constructor comment.
 */
public PointUnitLoader(java.util.ArrayList pointArray, String alias) {
	super();
	this.allPointsUnits = pointArray;
	this.databaseAlias = alias;
}
/**
 * run method comment.
 */
public void run() {
//temp code
java.util.Date timerStart = null;
java.util.Date timerStop = null;
//temp code

//temp code
timerStart = new java.util.Date();
//temp code
	String sqlString = "SELECT POINTID,UOMID,DECIMALPLACES " +
		"FROM POINTUNIT WHERE POINTID > 0 ORDER BY POINTID";

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( this.databaseAlias );
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sqlString);

		while (rset.next())
		{
			int pointID = rset.getInt(1);
			int uomID = rset.getInt(2);
			int decimalPlaces = rset.getInt(3);

			com.cannontech.database.data.lite.LitePointUnit lpu =
				new com.cannontech.database.data.lite.LitePointUnit( pointID, uomID, decimalPlaces);

			allPointsUnits.add(lpu);
		}
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, stmt, conn );
//temp code
timerStop = new java.util.Date();
com.cannontech.clientutils.CTILogger.info( 
    (timerStop.getTime() - timerStart.getTime())*.001 + 
      " Secs for PointLoader (" + allPointsUnits.size() + " loaded)" );

//temp code
	}
}
}

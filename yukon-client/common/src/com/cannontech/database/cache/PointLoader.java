package com.cannontech.database.cache;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
class PointLoader implements Runnable {
	private java.util.ArrayList allPoints = null;
	private String databaseAlias = null;
/**
 * DeviceLoader constructor comment.
 */
public PointLoader(java.util.ArrayList pointArray, String alias) {
	super();
	this.allPoints = pointArray;
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
	String sqlString = "SELECT POINTID,POINTNAME,POINTTYPE,PAObjectID, " +
		"POINTOFFSET,STATEGROUPID FROM POINT WHERE POINTID > 0 ORDER BY PAObjectID, POINTOFFSET";

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
			String pointName = rset.getString(2).trim();
			String pointType = rset.getString(3).trim();
			int paobjectID = rset.getInt(4);
			int pointOffset = rset.getInt(5);
			int stateGroupID = rset.getInt(6);
			
			com.cannontech.database.data.lite.LitePoint lp =
				new com.cannontech.database.data.lite.LitePoint( pointID, pointName, com.cannontech.database.data.point.PointTypes.getType(pointType),
																						paobjectID, pointOffset, stateGroupID );

			allPoints.add(lp);
		}
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( stmt != null )
				stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
//temp code
timerStop = new java.util.Date();
com.cannontech.clientutils.CTILogger.info( 
    (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for PointLoader" );
//temp code
	}
}
}

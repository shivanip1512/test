package com.cannontech.yukon.server.cache;

import java.util.Date;

import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class PeakPointHistoryLoader implements Runnable {
	private java.util.ArrayList allPeakPointHistory = null;
	private String databaseAlias = null;
/**
 * DeviceLoader constructor comment.
 */
public PeakPointHistoryLoader(java.util.ArrayList pointArray, String alias) {
	super();
	this.allPeakPointHistory = pointArray;
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
	String sqlString = "SELECT RPH1.POINTID POINTID, RPH1.VALUE VALUE, MIN(RPH1.TIMESTAMP) TIMESTAMP " +
						"FROM RAWPOINTHISTORY RPH1 WHERE RPH1.VALUE = " + 
						"(SELECT MAX(RPH2.VALUE) FROM RAWPOINTHISTORY RPH2 WHERE RPH1.POINTID = RPH2.POINTID) " +
						"GROUP BY RPH1.POINTID, RPH1.VALUE";
//	String sqlString = "SELECT POINTID,POINTNAME,POINTTYPE,PAOBJECTID, " +
//		"POINTOFFSET,STATEGROUPID FROM POINT WHERE POINTID > 0 ORDER BY PAObjectID, POINTOFFSET";

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
			Integer pointID = new Integer(rset.getInt(1));
			Double value = new Double(rset.getDouble(2));
			java.sql.Timestamp ts = rset.getTimestamp(3);
			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			cal.setTime(new Date(ts.getTime()));
			//cal.setTimeInMillis(ts.getTime());	// THIS IS A JDK1.4 thing
			
			com.cannontech.database.db.point.PeakPointHistory pph =	new com.cannontech.database.db.point.PeakPointHistory(pointID, cal, value);

			allPeakPointHistory.add(pph);
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
		com.cannontech.clientutils.CTILogger.info( (timerStop.getTime() - timerStart.getTime())*.001 + 
				" Secs for PeakPointHistoryLoader (" + allPeakPointHistory.size() + " loaded)" );

		//temp code
	}
}

}
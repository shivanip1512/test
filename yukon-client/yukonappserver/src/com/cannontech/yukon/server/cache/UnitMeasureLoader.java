package com.cannontech.yukon.server.cache;

import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.db.point.UnitMeasure;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class UnitMeasureLoader implements Runnable {
	private java.util.ArrayList allUnitMeasures = null;
	private String databaseAlias = null;
/**
 * StateGroupLoader constructor comment.
 */
public UnitMeasureLoader(java.util.ArrayList unitMeasureArray, String alias) {
	super();
	this.allUnitMeasures = unitMeasureArray;
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

	String sqlString = 
			"select UoMID, UoMName, CalcType, LongName from " + 
			UnitMeasure.TABLE_NAME + " order by LongName";

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
			int uomID = rset.getInt(1);
			String unitMeasureName = rset.getString(2).trim();
			int unitMeasureCalcType = rset.getInt(3);
			String longName = rset.getString(4).trim();

			LiteUnitMeasure lum =
				new LiteUnitMeasure( uomID, unitMeasureName, unitMeasureCalcType, longName );

			allUnitMeasures.add(lum);
		}
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
//temp code
timerStop = new java.util.Date();
com.cannontech.clientutils.CTILogger.info( 
    (timerStop.getTime() - timerStart.getTime())*.001 + 
      " Secs for UnitMeasureLoader (" + allUnitMeasures.size() + " loaded)" );
//temp code
	}
}
}

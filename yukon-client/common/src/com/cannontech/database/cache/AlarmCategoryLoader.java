package com.cannontech.database.cache;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
class AlarmCategoryLoader implements Runnable {
	private java.util.ArrayList allAlarmStates = null;
	private String databaseAlias = null;
/**
 * StateGroupLoader constructor comment.
 */
public AlarmCategoryLoader(java.util.ArrayList alarmStateArray, String alias) {
	super();
	this.allAlarmStates = alarmStateArray;
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
	String sqlString = "SELECT AlarmCategoryID, CategoryName FROM AlarmCategory " +
			"WHERE AlarmCategoryID > 0 ORDER BY AlarmCategoryID";

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
			int alarmID = rset.getInt(1);
			String alarmName = rset.getString(2).trim();

			com.cannontech.database.data.lite.LiteAlarmCategory la =
				new com.cannontech.database.data.lite.LiteAlarmCategory(alarmID, alarmName);

			allAlarmStates.add(la);
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
System.out.print( (timerStop.getTime() - timerStart.getTime())*.001 );
System.out.println( " Secs for AlarmCategoryLoader" );
//temp code
	}
}
}

package com.cannontech.yukon.server.cache;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
class StateGroupLoader implements Runnable {
	private java.util.ArrayList allStateGroups = null;
	private String databaseAlias = null;
/**
 * StateGroupLoader constructor comment.
 */
public StateGroupLoader(java.util.ArrayList stateGroupArray, String alias) {
	super();
	this.allStateGroups = stateGroupArray;
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
	String sqlString = "SELECT STATEGROUPID,NAME,GroupType FROM STATEGROUP WHERE STATEGROUPID > 0 ORDER BY STATEGROUPID";

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
			int stateGroupID = rset.getInt(1);
			String stateGroupName = rset.getString(2).trim();
         String groupType = rset.getString(3).trim();

			com.cannontech.database.data.lite.LiteStateGroup lsg =
				new com.cannontech.database.data.lite.LiteStateGroup(
               stateGroupID, stateGroupName, groupType );

			allStateGroups.add(lsg);
		}

		sqlString = "SELECT STATEGROUPID,RAWSTATE,TEXT,ImageID FROM STATE WHERE STATEGROUPID > 0 AND RAWSTATE >= 0 ORDER BY STATEGROUPID,RAWSTATE";
		rset = stmt.executeQuery(sqlString);
		while (rset.next())
		{
			int stateGroupID = rset.getInt(1);
			int rawState = rset.getInt(2);
			String text = rset.getString(3);
         int imgID = rset.getInt(4);

			for(int i=0;i<allStateGroups.size();i++)
			{
				if( ((com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i)).getStateGroupID() == stateGroupID )
				{
					((com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i)).getStatesList().add(
                     new com.cannontech.database.data.lite.LiteState(
                        rawState, text, imgID));
					break;
				}
			}
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
      " Secs for StateGroupLoader (" + allStateGroups.size() + " loaded)" );
//temp code
	}
}
}

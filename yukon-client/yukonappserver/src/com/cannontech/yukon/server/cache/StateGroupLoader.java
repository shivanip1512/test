package com.cannontech.yukon.server.cache;

import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.db.state.State;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class StateGroupLoader implements Runnable
{
	private Map allStateGroups = null;

/**
 * StateGroupLoader constructor comment.
 */
public StateGroupLoader(Map stateGroupMap)
{
	super();
	this.allStateGroups = stateGroupMap;
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
		"SELECT STATEGROUPID,NAME,GroupType FROM STATEGROUP " + 
		"ORDER BY STATEGROUPID";

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sqlString);

		while (rset.next())
		{
			int stateGroupID = rset.getInt(1);
			String stateGroupName = rset.getString(2).trim();
         	String groupType = rset.getString(3).trim();

			LiteStateGroup lsg =
				new LiteStateGroup(
               stateGroupID, stateGroupName, groupType );

			allStateGroups.put( new Integer(lsg.getStateGroupID()), lsg);
		}

		sqlString =
			"SELECT StateGroupID, RawState, Text, ForegroundColor, BackgroundColor, ImageID " + 
			"FROM " + State.TABLE_NAME + " WHERE " + 
			"RAWSTATE >= 0 ORDER BY STATEGROUPID,RAWSTATE";
		
		rset = stmt.executeQuery(sqlString);
		while (rset.next())
		{
			int stateGroupID = rset.getInt(1);
			int rawState = rset.getInt(2);
			String text = rset.getString(3);
			int fgColor = rset.getInt(4);
			int bgColor = rset.getInt(5);
         	int imgID = rset.getInt(6);
         	
			LiteStateGroup stateGrp = (LiteStateGroup)allStateGroups.get( new Integer(stateGroupID) );
			if( stateGrp != null )
				stateGrp.getStatesList().add(
					new LiteState( rawState, text, fgColor, bgColor, imgID));
			else
				CTILogger.error( "Unable to find stategroup(id=" + stateGroupID + ") for a given state");
		}
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
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
			CTILogger.error( e.getMessage(), e );
		}
//temp code
timerStop = new java.util.Date();
CTILogger.info( 
    (timerStop.getTime() - timerStart.getTime())*.001 + 
      " Secs for StateGroupLoader (" + allStateGroups.size() + " loaded)" );
//temp code
	}
}
}

package com.cannontech.yukon.server.cache;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class LMScenarioProgramLoader implements Runnable 
{
	private java.util.List allLMScenarioPrograms = null;
	private String databaseAlias = null;

/**
 * LMScenarioLoader constructor comment.
 */
public LMScenarioProgramLoader(java.util.List lmScenarioProgs, String alias) {
	super();
	allLMScenarioPrograms = lmScenarioProgs;
	databaseAlias = alias;
}

/**
 * run method comment.
 */
public void run() 
{
	//temp code
	java.util.Date timerStart = null;
	java.util.Date timerStop = null;
	//temp code
	
	//temp code
	timerStart = new java.util.Date();
	//temp code
	String sqlString = 
			"select ScenarioID, ProgramID, StartOffset, StopOffset, StartGear " +
			"from " + LMControlScenarioProgram.TABLE_NAME + 
			" order by ScenarioID";

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
			LiteLMProgScenario liteProg = new LiteLMProgScenario( rset.getInt(2) );
			liteProg.setScenarioID( rset.getInt(1) );
			liteProg.setStartOffset( rset.getInt(3) );
			liteProg.setStopOffset( rset.getInt(4) );
			liteProg.setStartGear( rset.getInt(5) );
			
			allLMScenarioPrograms.add( liteProg );
		}

	}
	catch( java.sql.SQLException e )
	{
      CTILogger.error("Problem loading LMScenarioPrograms", e );
	}
	finally
	{
		try
		{
			if( rset != null )
				rset.close();
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
      " Secs for LMScenarioProgramLoader (" + allLMScenarioPrograms.size() + " loaded)" );

//temp code
	}
}
	
}

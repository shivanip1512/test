/*
 * Created on Aug 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.yukon.server.cache;


import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteLMPAOExclusion;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMPAOExclusionLoader implements Runnable 
{
	private java.util.List allLMPAOExclusions = null;
	private String databaseAlias = null;

/**
 * LMPAOExclusionLoader constructor comment.
 */
public LMPAOExclusionLoader(java.util.List lmExclusions, String alias) {
	super();
	allLMPAOExclusions = lmExclusions;
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
			"select ExclusionID, PaoID, ExcludedPaoID " +
			"from " + PAOExclusion.TABLE_NAME + " where FunctionID = " + CtiUtilities.LM_SUBORDINATION_FUNC_ID
			+ " order by PaoID";

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
			LiteLMPAOExclusion liteExclu = new LiteLMPAOExclusion( rset.getInt(1) );
			liteExclu.setMasterPaoID( rset.getInt(2) );
			liteExclu.setExcludedPaoID( rset.getInt(3) );
			
			allLMPAOExclusions.add( liteExclu );
		}

	}
	catch( java.sql.SQLException e )
	{
	  CTILogger.error("Problem loading LMPAOExclusions", e );
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
	  " Secs for LMPAOExclusionLoader (" + allLMPAOExclusions.size() + " loaded)" );

//temp code
	}
}
	
}


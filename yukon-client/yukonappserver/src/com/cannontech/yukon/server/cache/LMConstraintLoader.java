/*
 * Created on Mar 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.yukon.server.cache;


/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMConstraintLoader implements Runnable 
{
	
	private String databaseAlias = null;
	private java.util.ArrayList allLMConstraints = null;
	/**
	 * LMConstraintLoader constructor comment.
	 */
	public LMConstraintLoader(java.util.ArrayList constraints ,String dbAlias) {
		super();
		this.allLMConstraints = constraints ;
		this.databaseAlias = dbAlias;
	
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 10:54:17 AM)
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
		String sqlString = "SELECT CONSTRAINTID, CONSTRAINTNAME FROM LMPROGRAMCONSTRAINTS WHERE CONSTRAINTID >= 0";

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(this.databaseAlias);
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);

			while (rset.next())
			{
				int constraintID = rset.getInt(1);
				String constraintName = rset.getString(2).trim();

				com.cannontech.database.data.lite.LiteLMConstraint strain =
					new com.cannontech.database.data.lite.LiteLMConstraint( constraintID, constraintName );
				
				strain.setConstraintName(constraintName);

				allLMConstraints.add(strain);
			}

		}
		catch (java.sql.SQLException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			}
			catch (java.sql.SQLException e)
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
			//temp code
			timerStop = new java.util.Date();
			com.cannontech.clientutils.CTILogger.info(
				(timerStop.getTime() - timerStart.getTime())*.001 + 
				   " Secs for LMConstraintLoader (" + allLMConstraints.size() + " loaded)" );
               
			//temp code
		}

	}

}

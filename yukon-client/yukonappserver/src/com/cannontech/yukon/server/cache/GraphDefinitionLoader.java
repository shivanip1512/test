package com.cannontech.yukon.server.cache;

import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (10/5/00 10:54:04 AM)
 * @author: 
 */
public class GraphDefinitionLoader implements Runnable {
	private String databaseAlias = null;
	private java.util.ArrayList gDefList = null;
/**
 * GraphDefinitionLoader constructor comment.
 */
public GraphDefinitionLoader(java.util.ArrayList gDefList, String dbAlias) {
	super();
	this.gDefList = gDefList;
	this.databaseAlias = dbAlias;
}
	/**
	 * When an object implementing interface <code>Runnable</code> is used 
	 * to create a thread, starting the thread causes the object's 
	 * <code>run</code> method to be called in that separately executing 
	 * thread. 
	 * <p>
	 * The general contract of the method <code>run</code> is that it may 
	 * take any action whatsoever.
	 *
	 * @see     java.lang.Thread#run()
	 */
public void run() 
{
	long timerStart = System.currentTimeMillis();

	String sqlString = "SELECT GraphDefinitionID,Name FROM GraphDefinition ORDER BY Name";

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
			int gDefID = rset.getInt(1);
			String name = rset.getString(2).trim();

			com.cannontech.database.data.lite.LiteGraphDefinition gDef = new com.cannontech.database.data.lite.LiteGraphDefinition( gDefID, name );
			gDefList.add( gDef );
		}
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, stmt, conn );

		com.cannontech.clientutils.CTILogger.info( 
            ((System.currentTimeMillis()-timerStart)*.001) + 
               " Secs for GraphDefinitionLoader (" + gDefList.size() + " loaded)" );
	}
}
}

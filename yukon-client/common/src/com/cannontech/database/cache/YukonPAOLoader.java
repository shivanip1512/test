package com.cannontech.database.cache;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
class YukonPAOLoader implements Runnable 
{
	private java.util.ArrayList allPAObjects = null;
	private String databaseAlias = null;
/**
 * DeviceLoader constructor comment.
 */
public YukonPAOLoader(java.util.ArrayList pAObjectArray, String alias) 
{
	super();
	this.allPAObjects = pAObjectArray;
	this.databaseAlias = alias;
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
	String sqlString = "SELECT PAObjectID, Category, PAOName, " +
			"Type, PAOClass " +
			"FROM YukonPAObject WHERE PAObjectID > 0 ORDER BY Category, PAOClass, PAOName";

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
			int paoID = rset.getInt(1);
			String paoCategory = rset.getString(2).trim();
			String paoName = rset.getString(3).trim();
			String paoType = rset.getString(4).trim();
			String paoClass = rset.getString(5).trim();

			com.cannontech.database.data.lite.LiteYukonPAObject pao =
				new com.cannontech.database.data.lite.LiteYukonPAObject(
						paoID, 
						paoName, 
						com.cannontech.database.data.pao.PAOGroups.getCategory(paoCategory),
						com.cannontech.database.data.pao.PAOGroups.getPAOType(paoCategory, paoType),
						com.cannontech.database.data.pao.PAOGroups.getPAOClass(paoCategory, paoClass) );

			allPAObjects.add( pao );
		}

		if( rset != null )
			rset.close();

		sqlString = "SELECT DEVICEID,PORTID FROM DEVICEDIRECTCOMMSETTINGS WHERE DEVICEID > 0 ORDER BY DEVICEID";
		rset = stmt.executeQuery(sqlString);

		while (rset.next())
		{
			int deviceID = rset.getInt(1);
			int portID = rset.getInt(2);

			for(int i = 0; i < allPAObjects.size(); i++)
			{
				if ( ((com.cannontech.database.data.lite.LiteYukonPAObject)allPAObjects.get(i)).getYukonID() == deviceID )
				{
					((com.cannontech.database.data.lite.LiteYukonPAObject)allPAObjects.get(i)).setPortID(portID);
					break;
				}
			}
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
    (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for YukonPAObjectLoader" );
//temp code
	}
}
}

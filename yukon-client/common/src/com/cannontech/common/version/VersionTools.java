package com.cannontech.common.version;

/**
 * Insert the type's description here.
 * Creation date: (6/26/2001 2:42:37 PM)
 * @author: 
 */
public final class VersionTools 
{
/**
 * VersionTools constructor comment.
 */
private VersionTools() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:18:30 AM)
 * @return java.lang.String
 */
public final static com.cannontech.database.db.version.CTIDatabase getDatabaseVersion() 
{
	com.cannontech.database.db.version.CTIDatabase db = new com.cannontech.database.db.version.CTIDatabase();
	java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
	java.sql.PreparedStatement stat = null;
	
	try
	{
		//get the latest version
		stat = conn.prepareStatement( "select Version, " +
					"CTIEmployeeName, DateApplied, Notes from " + 
					com.cannontech.database.db.version.CTIDatabase.TABLE_NAME + " where DateApplied is not null " + 
					"order by Version desc" );

		java.sql.ResultSet rs = stat.executeQuery();
		rs.next();
		
		db.setVersion( rs.getString("Version") );
		db.setCtiEmployeeName( rs.getString("CTIEmployeeName") );
		db.setDateApplied( new java.util.Date( rs.getTimestamp("DateApplied").getTime() ) );
		db.setNotes( rs.getString("Notes") );
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace( System.out );
	}
	finally
	{
		try
		{
			if( stat != null )
				stat.close();
				
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace( System.out );
		}
	}
		
	return db;
}
/**
 * Insert the method's description here.
 * Creation date: (6/26/2001 2:43:28 PM)
 * @return java.lang.String
 */
public final static java.lang.String getYUKON_VERSION() 
{
	return com.cannontech.common.util.CtiProperties.getInstance().getProperty(
			com.cannontech.common.util.CtiProperties.KEY_YUKON_VERSION, "XX.xx" ).toString();
}
}

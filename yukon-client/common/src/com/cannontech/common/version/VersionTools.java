package com.cannontech.common.version;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

/**
 * Insert the type's description here.
 * Creation date: (6/26/2001 2:42:37 PM)
 * @author: 
 */
public final class VersionTools 
{	
	public static final String KEY_YUKON_VERSION = "Yukon-Version";
	public static final String COMMON_JAR = "common.jar";
	public static String yukonVersion = null;


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
	java.sql.Connection conn = PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
	java.sql.PreparedStatement stat = null;
	
	try
	{
		//get the latest version
		stat = conn.prepareStatement( "select Version, " +
					"CTIEmployeeName, DateApplied, Notes, Build from " + 
					com.cannontech.database.db.version.CTIDatabase.TABLE_NAME + " where DateApplied is not null " + 
					"order by Version desc" );

		java.sql.ResultSet rs = stat.executeQuery();
		rs.next();
		
		db.setVersion( rs.getString("Version") );
		db.setCtiEmployeeName( rs.getString("CTIEmployeeName") );
		db.setDateApplied( new java.util.Date( rs.getTimestamp("DateApplied").getTime() ) );
		db.setNotes( rs.getString("Notes") );
		db.setBuild( new Integer(rs.getInt("Build")) );
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
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
			CTILogger.error( e.getMessage(), e );
		}
	}
		
	return db;
}

/**
 * Check to see if a common STARS table is in the DB
 * @return boolean
 */
public static boolean starsExists()
{
	//case sensitive in Oracle (very important)
	return VersionTools.tableExists("CUSTOMERACCOUNT");
}

public static void main ( String[] args )
{
	starsExists();
}

/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:18:30 AM)
 * @return java.lang.String
 */
private final static boolean tableExists( String tableName_ )
{
	java.sql.Connection conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	java.sql.PreparedStatement stat = null;
	boolean retVal = false;
	
	try
	{
/*      
		ResultSet rs = getDbConnection().getMetaData().getColumns(
								null, 
								null, 
								tableName.toUpperCase(), 
								"%" );
*/
		java.sql.ResultSet rs = conn.getMetaData().getTables( 
							null, null, tableName_.toUpperCase(), null );

		if( rs.next() )
			retVal = true;
	}
	catch( java.sql.SQLException e )
	{}
	finally
	{
		try
		{
			if( stat != null ) stat.close();				
			if( conn != null ) conn.close();
		}
		catch( java.sql.SQLException e )
		{}
	}

	return retVal;
}

/**
 * Insert the method's description here.
 * Creation date: (6/26/2001 2:43:28 PM)
 * @return java.lang.String
 */
public synchronized final static java.lang.String getYUKON_VERSION() 
{
	if( yukonVersion == null )
	{
		try
		{
			java.util.jar.JarFile jf = new java.util.jar.JarFile( COMMON_JAR );
	
			yukonVersion =
					jf.getManifest().getMainAttributes().getValue( KEY_YUKON_VERSION );
	
			jf.close();			
		}
		catch( Exception e )
		{
			CTILogger.info("*** PROPERTY TRANSLATION ERROR: " + KEY_YUKON_VERSION + " key/value not stored." );
		}
		
		if( yukonVersion == null )
			yukonVersion = "XX.xx";		
	}
	
	return yukonVersion;
}
}

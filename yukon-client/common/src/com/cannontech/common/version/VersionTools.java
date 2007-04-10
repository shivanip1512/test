package com.cannontech.common.version;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.version.CTIDatabase;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.spring.YukonSpringHook;

/**
 * Insert the type's description here.
 * Creation date: (6/26/2001 2:42:37 PM)
 * @author: 
 */
public final class VersionTools 
{	
	public static final String KEY_YUKON_VERSION = "Yukon-Version";
	public static final String COMMON_JAR = "common.jar";
	
    private static Boolean starsExists = null;
	private static Boolean crsPtjIntegration = null;
    private static Boolean staticLoadGroupMapping = null;
	public static String yukonVersion = null;
	private static CTIDatabase db_obj = null;
	

	//we need a set of query strings for backward compatability
	// since this is used in DBUpdates that get executed before
	// the DB structure is changed
	public static final String[] QUERY_STRS =
	{
		//latest query string is first!
		"select Version, CTIEmployeeName, DateApplied, Notes, Build from " + 
			CTIDatabase.TABLE_NAME + " where DateApplied is not null " + 
			"order by Version desc, Build desc",

		"select Version, CTIEmployeeName, DateApplied, Notes from " + 
			CTIDatabase.TABLE_NAME + " where DateApplied is not null " + 
			"order by Version desc"
	};


/**
 * VersionTools constructor comment.
 */
private VersionTools() {
	super();
}

/**
 * Returns the latest DB version stored in the DB 
 * 
 * @return
 */
public synchronized static CTIDatabase getDBVersionRefresh()
{
    db_obj = null;
    return getDatabaseVersion();
}

/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 9:18:30 AM)
 * @return java.lang.String
 */
public synchronized static CTIDatabase getDatabaseVersion() 
{	
	if( db_obj != null )
		return db_obj;

	db_obj = new CTIDatabase();
	java.sql.Connection conn = PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
	java.sql.PreparedStatement stat = null;

	try
	{	
		java.sql.ResultSet rs = null;
		
		int i = 0;
		for( i = 0; i < QUERY_STRS.length; i++ )
		{
			try
			{
				//get the latest version
				stat = conn.prepareStatement( QUERY_STRS[i] );

				//chucks if columns are not up to date
				rs = stat.executeQuery();
			}
			catch( Exception exx )
			{}

			if( rs != null )
				break;
		}
	
		rs.next();
		
		db_obj.setVersion( rs.getString("Version") );
		db_obj.setCtiEmployeeName( rs.getString("CTIEmployeeName") );
		db_obj.setDateApplied( new java.util.Date( rs.getTimestamp("DateApplied").getTime() ) );
		db_obj.setNotes( rs.getString("Notes") );
		
		if( i == 0 ) //zeroth query string has the build column
			db_obj.setBuild( new Integer(rs.getInt("Build")) );
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
		
	return db_obj;
}

/**
 * Check to see if a common STARS table is in the DB
 * @return boolean
 */
public static boolean starsExists() throws Exception
{
    if(starsExists == null) {
        boolean appCatExists = VersionTools.tableExists("APPLIANCECATEGORY");
        if(!appCatExists) {
            throw new Exception("STARS tables not present in this database.");
        }
        else {
            return CtiUtilities.isTrue( DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.STARS_ACTIVATION ));
        }
    }

    return starsExists;
}

/**
 * Check to see if a the required SAMToCRS_PTJ table exits in DB
 * @return boolean
 */
public static boolean crsPtjIntegrationExists()
{
	if( crsPtjIntegration == null)
	//case sensitive in Oracle (very important)
		crsPtjIntegration = new Boolean(VersionTools.tableExists("SAMTOCRS_PTJ"));
	return crsPtjIntegration;
}

/**
 * Check to see if a static mapping table for STARS load group assignment exists
 * This is current only used in the Xcel SAM deployment of STARS
 * @return boolean
 */
public static boolean staticLoadGroupMappingExists()
{
    if( staticLoadGroupMapping == null)
    //case sensitive in Oracle (very important)
    /*I don't think that is still true today*/
        staticLoadGroupMapping = new Boolean(VersionTools.tableExists("STATICLOADGROUPMAPPING"));
    return staticLoadGroupMapping;
}

public static void main ( String[] args )
{
	//starsExists();
}

/**
 * Tests whether the given table exists
 * Creation date: (6/25/2001 9:18:30 AM)
 * @return java.lang.String
 */
private final static boolean tableExists( String tableName_ )
{
    // A previous version used meta data but it didn't work with oracle!
    try {
        JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
        int num = (Integer) jdbcOps.queryForObject("select count(*) from " + tableName_, Integer.class);
        return true;
    } 
    catch(DataAccessException dae) {
        // fine, guess it didn't exist
    }
    
    return false;
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
			yukonVersion = "0.0.0";		
	}
	
	return yukonVersion;
}

public static Boolean getStaticLoadGroupMapping() {
    return staticLoadGroupMapping;
}

public static void setStaticLoadGroupMapping(Boolean staticLoadGroupMapping) {
    VersionTools.staticLoadGroupMapping = staticLoadGroupMapping;
}

}
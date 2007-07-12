package com.cannontech.common.version;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
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
    public static final String KEY_YUKON_DETAILS = "Yukon-Details";
	public static final String COMMON_JAR = "common.jar";
	
    private static Boolean starsExists = null;
	private static Boolean crsPtjIntegration = null;
    private static Boolean staticLoadGroupMapping = null;
	public static String yukonVersion = null;
    public static String yukonDetails;
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
	java.sql.ResultSet rs = null;

	try
	{	
		
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
		SqlUtils.close(rs, stat, conn );
	}
		
	return db_obj;
}

/**
 * Check to see if a common STARS table is in the DB
 * @return boolean
 */
public static boolean starsExists()
{
    if (starsExists == null) {
        boolean appCatExists = VersionTools.tableExists("APPLIANCECATEGORY");
        if (!appCatExists) {
            throw new RuntimeException("STARS tables not present in this database.");
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
public synchronized final static String getYUKON_VERSION() 
{
    if( yukonVersion == null ) {
        ClassLoader classLoader = VersionTools.class.getClassLoader();
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL it = null;
                try {
                    it = resources.nextElement();
                    InputStream stream = it.openStream();
                    Manifest manifest = new Manifest(stream);
                    yukonVersion = manifest.getMainAttributes().getValue(KEY_YUKON_VERSION);
                } catch (Exception e) {
                }
                if (yukonVersion != null) {
                    CTILogger.debug("Found Yukon Version '" + yukonVersion + "' on " + it);
                    break;
                }
            }
        } catch ( IOException e ) {
            CTILogger.warn("Caught exception looking up yukon version, setting to 'unknown'", e);
            yukonVersion = "unknown";
        }

        if ( yukonVersion == null ) {
            CTILogger.warn("Yukon version was not found, setting to 'undefined'");
            yukonVersion = "undefined";
        }	
    }

    return yukonVersion;
}

public synchronized static final String getYukonDetails() {
    if (yukonDetails == null) {
        ClassLoader classLoader = VersionTools.class.getClassLoader();
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL it = null;
                try {
                    it = resources.nextElement();
                    InputStream stream = it.openStream();
                    Manifest manifest = new Manifest(stream);
                    yukonDetails = manifest.getMainAttributes().getValue(KEY_YUKON_DETAILS);
                } catch (Exception e) {
                }
                if (yukonDetails != null) {
                    CTILogger.debug("Found Yukon Details '" + yukonDetails + "' on " + it);
                    break;
                }
            }
        } catch ( IOException e ) {
            CTILogger.warn("Caught exception looking up yukon details, setting to 'unknown'", e);
            yukonDetails = "unknown";
        }

        if ( yukonDetails == null ) {
            CTILogger.warn("Yukon details was not found, setting to 'undefined'");
            yukonDetails = "undefined";
        }
    }
    return yukonDetails;
}


public static Boolean getStaticLoadGroupMapping() {
    return staticLoadGroupMapping;
}

public static void setStaticLoadGroupMapping(Boolean staticLoadGroupMapping) {
    VersionTools.staticLoadGroupMapping = staticLoadGroupMapping;
}

}
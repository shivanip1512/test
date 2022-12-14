package com.cannontech.common.version;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.Manifest;

import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.version.CTIDatabase;
import com.google.common.collect.Maps;

public final class VersionTools {
    
    public static final String KEY_YUKON_VERSION = "Yukon-Version";
    public static final String KEY_YUKON_DETAILS = "Yukon-Details";
    public static final String KEY_BUILD_INFO = "Hudson-Build-Details";

    private static Boolean crsPtjIntegration = null;
    private static Boolean staticLoadGroupMapping = null;
    public static String yukonVersion = null;
    public static String yukonDetails;
    public static Map<String, String> buildInfo = null;
    private static CTIDatabase db_obj = null;

    public static final String VERSION_UNKNOWN = "unknown";
    public static final String VERSION_UNDEFINED = "undefined";

    // we need a set of query strings for backward compatibility
    // since this is used in DBUpdates that get executed before
    // the DB structure is changed
    private static final String[] QUERY_STRS = {
        // latest query string is first!
        "SELECT Version, Build FROM CTIDatabase ORDER BY Version DESC, Build DESC",
        // really old databases may not have the Build column
        "SELECT Version FROM CTIDatabase ORDER BY Version DESC"
    };
    
    /**
     * Refreshes the latest DB version value and returns it.
     */
    public synchronized static CTIDatabase getDBVersionRefresh() {
        db_obj = null;
        return getDatabaseVersion();
    }
    
    /** Returns the latest DB version stored in the DB */
    public synchronized static CTIDatabase getDatabaseVersion() {
        
        if (db_obj != null) {
            return db_obj;
        }
        
        Connection conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        PreparedStatement stat = null;
        ResultSet rs = null;
        
        try {
            for (int i = 0; i < QUERY_STRS.length; i++) {
                try {
                    String version = null;
                    Integer build = null;
                    // get the most recent query string
                    stat = conn.prepareStatement(QUERY_STRS[i]);
                    // rs remains null if columns are not up to date
                    rs = stat.executeQuery();
                    if (rs != null) {
                        rs.next();
                        version = rs.getString("Version");
                        if (i == 0) {
                            build = rs.getInt("Build");
                        }
                        db_obj = new CTIDatabase(version, build);
                        break;
                    }
                } catch (SQLException e) {
                    CTILogger.error(e.getMessage(), e);
                }
            }
        } finally {
            SqlUtils.close(rs, stat, conn);
        }
        
        return db_obj;
    }
    
    /**
     * Check to see if a the required SAMToCRS_PTJ table exits in DB
     */
    public static boolean crsPtjIntegrationExists() {
        if (crsPtjIntegration == null) {
            // case sensitive in Oracle (very important)
            crsPtjIntegration = new Boolean(VersionTools.tableExists("SAMTOCRS_PTJ"));
        }
        return crsPtjIntegration;
    }
    
    /**
     * Check to see if a static mapping table for STARS load group assignment exists
     * This is current only used in the Xcel SAM deployment of STARS
     */
    public static boolean staticLoadGroupMappingExists() {
        if (staticLoadGroupMapping == null) {
            // case sensitive in Oracle (very important)
            /* I don't think that is still true today */
            staticLoadGroupMapping = new Boolean(VersionTools.tableExists("STATICLOADGROUPMAPPING"));
        }
        return staticLoadGroupMapping;
    }
    
    public static void main(String[] args) {
        // starsExists();
    }
    
    /**
     * Tests whether the given table exists
     */
    private final static boolean tableExists(String tableName_) {
        // A previous version used meta data but it didn't work with oracle!
        try {
            JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
            jdbcOps.queryForObject("select count(*) from " + tableName_, Integer.class);
            return true;
        } catch (BadSqlGrammarException dae) {
            // fine, guess it didn't exist
        }
        
        return false;
    }
    
    public synchronized final static String getYUKON_VERSION() {
        
        if (yukonVersion != null) {
            return yukonVersion;
        }
        
        if (BootstrapUtils.isWebStartClient()) {
            yukonVersion = System.getProperty("jnlp.yukon.version");
        } else {
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
                    } catch (Exception e) {}
                    if (yukonVersion != null) {
                        CTILogger.debug("Found Yukon Version '" + yukonVersion + "' on " + it);
                        break;
                    }
                }
            } catch (IOException e) {
                CTILogger.warn("Caught exception looking up yukon version, setting to 'unknown'", e);
                yukonVersion = VERSION_UNKNOWN;
            }
            
            if (yukonVersion == null) {
                CTILogger.warn("Yukon version was not found, setting to 'undefined'");
                yukonVersion = VERSION_UNDEFINED;
            }
        }
        
        return yukonVersion;
    }
    
    public synchronized static boolean isYukonVersionDefined() {
        
        final String version = getYUKON_VERSION();
        if (VERSION_UNDEFINED.equals(version) || VERSION_UNKNOWN.equals(version)) {
            return false;
        }
        
        return true;
    }
    
    public synchronized static final String getYukonDetails() {
        
        if (yukonDetails != null) {
            return yukonDetails;
        }
        
        if (BootstrapUtils.isWebStartClient()) {
            yukonDetails = System.getProperty("jnlp.yukon.version.details");
        } else {
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
                    } catch (Exception e) {}
                    if (yukonDetails != null) {
                        CTILogger.debug("Found Yukon Details '" + yukonDetails + "' on " + it);
                        break;
                    }
                }
            } catch (IOException e) {
                CTILogger.warn("Caught exception looking up yukon details, setting to 'unknown'", e);
                yukonDetails = VERSION_UNKNOWN;
            }
            
            if (yukonDetails == null) {
                CTILogger.warn("Yukon details was not found, setting to 'undefined'");
                yukonDetails = VERSION_UNDEFINED;
            }
        }
        
        return yukonDetails;
    }
    
    public synchronized final static Map<String, String> getBuildInfo() {
        
        if (buildInfo == null) {
            
            buildInfo = Maps.newHashMap();
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
                        String[] values = manifest.getMainAttributes().getValue(KEY_BUILD_INFO).split("[,]");
                        int i = 0;
                        for (i = 0; i < values.length; i++) {
                            String[] keyValue = values[i].split("[=]");
                            buildInfo.put(keyValue[0], keyValue[1]);
                        }
                    } catch (Exception e) {}
                    if (!buildInfo.isEmpty()) {
                        CTILogger.debug("Found '" + buildInfo.size() + "' build information parameters on " + it);
                        break;
                    }
                }
            } catch (IOException e) {
                CTILogger.warn("Caught exception looking up build info", e);
            }
            
            if (buildInfo.isEmpty()) {
                CTILogger.debug("Unable to find build information.");
                buildInfo.put("status", "No build information could be found.");
            }
        }
        
        return buildInfo;
    }
    
}
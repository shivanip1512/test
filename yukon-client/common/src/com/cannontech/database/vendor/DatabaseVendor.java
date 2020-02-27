package com.cannontech.database.vendor;

import java.util.Set;
import org.apache.logging.log4j.Logger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.BadConfigurationException;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.ImmutableSet;

public enum DatabaseVendor {

    MS2008("Microsoft SQL Server", 10),
    MS2012("Microsoft SQL Server", 11), // Denali
    MS2014("Microsoft SQL Server", 12),
    MS2016("Microsoft SQL Server", 13),
    MS2017("Microsoft SQL Server", 14),
    MS2019("Microsoft SQL Server", 15),
    MS_UNKNOWN("Microsoft SQL Server", -1),
    ORACLE10G("Oracle", 10, ":"),
    ORACLE11G("Oracle", 11, ":"),
    ORACLE12C("Oracle", 12, "/"),
    ORACLE18C("Oracle", 18, "/"),
    ORACLE19C("Oracle", 19, "/"),
    ORACLE_UNKNOWN("Oracle", -1, "/"),
    UNKNOWN("", -1, "/");

    private static Logger logger = YukonLogManager.getLogger(DatabaseVendor.class);

    private static Set<DatabaseVendor> oracleDatabases =
        ImmutableSet.of(ORACLE10G, ORACLE11G, ORACLE12C, ORACLE18C, ORACLE19C, ORACLE_UNKNOWN);
    private static Set<DatabaseVendor> msDatabases =
        ImmutableSet.of(MS2008, MS2012, MS2014, MS2016, MS2017, MS2019, MS_UNKNOWN);

    private final String vendorName;
    private final int databaseMajorVersion;
    private final String urlCharacter;

    DatabaseVendor(String vendorName, int databaseMajorVersion) {
        this.vendorName = vendorName;
        this.databaseMajorVersion = databaseMajorVersion;
        this.urlCharacter = ""; // default use nothing, i.e. n/a for SQL Server
    }
    
    DatabaseVendor(String vendorName, int databaseMajorVersion, String urlCharacter) {
        this.vendorName = vendorName;
        this.databaseMajorVersion = databaseMajorVersion;
        this.urlCharacter = urlCharacter;
    }

    public String getVendorName() {
        return this.vendorName;
    }

    public int getDatabaseMajorVersion() {
        return this.databaseMajorVersion;
    }
    
    public String getUrlCharacter() {
        return urlCharacter;
    }

    public static DatabaseVendor getDatabaseVendor(String vendorName, String productVersion, int majorVersion) {

        // Try to match without the minor version
        for (DatabaseVendor databaseVendor : values()) {
            if (databaseVendor.getVendorName().contains(vendorName)
                && databaseVendor.getDatabaseMajorVersion() == majorVersion) {
                return databaseVendor;
            }
        }

        logger.warn("Your database is not officially supported by Yukon: " + vendorName + " - " + productVersion);
        if (MS_UNKNOWN.getVendorName().equalsIgnoreCase(vendorName)) {
            return DatabaseVendor.MS_UNKNOWN;
        } else if (ORACLE_UNKNOWN.getVendorName().equalsIgnoreCase(vendorName)) {
            return DatabaseVendor.ORACLE_UNKNOWN;
        }

        return DatabaseVendor.UNKNOWN;
    }
    
        
    /**
     * Returns lowest DatabaseVendor for dbType (as defined by master.cfg DB_TYPTE
     * NOTE: This does not return the actual DatabaseVersion that we are connecting to,
     * but rather the lowest generic DatabaseVendor respective only of the DB_TYPE setting.
     */
    public static DatabaseVendor getForDbType(String dbType) throws BadConfigurationException {
        if ("mssql".equalsIgnoreCase(dbType)) {
            return MS_UNKNOWN;
        } else if ("oracle".equalsIgnoreCase(dbType)) {
            return ORACLE11G; // generically representative of Oracle 10, 11
        } else if (StringUtils.startsWithIgnoreCase(dbType, "oracle")) {
            return ORACLE_UNKNOWN; // assumes anything else with "oracle" uses the modern urlChar of /
        } else {
            throw new BadConfigurationException("Unrecognized database type (DB_TYPE) in master.cfg: " + dbType);
        }
    }
    
    public boolean isOracle() {
        return oracleDatabases.contains(this);
    }

    public boolean isSqlServer() {
        return msDatabases.contains(this);
    }

    public static Set<DatabaseVendor> getMsDatabases() {
        return msDatabases;
    }

    public static Set<DatabaseVendor> getOracleDatabases() {
        return oracleDatabases;
    }
}
package com.cannontech.database.vendor;

import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.google.common.collect.ImmutableSet;


public enum DatabaseVendor {

    MS2008   ("Microsoft SQL Server", 10),
    MS2012   ("Microsoft SQL Server", 11), // Denali
    MS2014   ("Microsoft SQL Server", 12),
    MS2016   ("Microsoft SQL Server", 13),
    MS2017   ("Microsoft SQL Server", 14),
    MS_UNKNOWN("Microsoft SQL Server"),
    ORACLE10G("Oracle", 10),
    ORACLE11G("Oracle", 11),
    ORACLE12C("Oracle", 12),
    ORACLE18C("Oracle", 18),
    ORACLE_UNKNOWN("Oracle"),
    UNKNOWN("");

    private static Logger logger = YukonLogManager.getLogger(DatabaseVendor.class);

    private static Set<DatabaseVendor> oracleDatabases =
        ImmutableSet.of(ORACLE10G, ORACLE11G, ORACLE12C, ORACLE18C, ORACLE_UNKNOWN);
    private static Set<DatabaseVendor> msDatabases =
        ImmutableSet.of(MS2008, MS2012, MS2014, MS2016, MS2017, MS_UNKNOWN);

    private final String vendorName;
    private final int databaseMajorVersion;

    DatabaseVendor(String vendorName) {
        this(vendorName, -1);
    }

    DatabaseVendor(String vendorName, int databaseMajorVersion) {
        this.vendorName = vendorName;
        this.databaseMajorVersion = databaseMajorVersion;
    }

    public String getVendorName() {
        return this.vendorName;
    }

    public int getDatabaseMajorVersion() {
        return this.databaseMajorVersion;
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
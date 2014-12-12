package com.cannontech.database.vendor;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;


public enum DatabaseVendor {
    
    MS2000    ("Microsoft SQL Server",8),
    MS2005    ("Microsoft SQL Server",9),
    MS2008    ("Microsoft SQL Server",10),
    MS2012    ("Microsoft SQL Server",11),  //Denali
    MS2014    ("Microsoft SQL Server",12),
    ORACLE9I  ("Oracle",9),
    ORACLE10G ("Oracle",10),
    ORACLE11G ("Oracle",11),
    ORACLE12C ("Oracle",12),
    UNKNOWN("");
    
    private static Logger logger = YukonLogManager.getLogger(DatabaseVendor.class);
    
    private final String vendorName;
    private final int databaseMajorVersion;
    
    DatabaseVendor(String vendorName) {
        this.vendorName = vendorName;
        this.databaseMajorVersion = -1;
    }
    
    DatabaseVendor(String vendorName, int databaseMajorVersion) {
        this.vendorName = vendorName;
        this.databaseMajorVersion = databaseMajorVersion;
    }

    public String getVendorName() { return this.vendorName; }
    public int getDatabaseMajorVersion() { return this.databaseMajorVersion; }
    
    public static DatabaseVendor getDatabaseVendor(String vendorName, String productVersion, int majorVersion) {

        // Try to match without the minor version
        for (DatabaseVendor databaseVendor : values()){
            if (databaseVendor.getVendorName().contains(vendorName) &&
                databaseVendor.getDatabaseMajorVersion() == majorVersion) {
                return databaseVendor;
            }
        }
        
        logger.warn("Your database is not officially supported by Yukon: " + vendorName +" - "+productVersion);
        return DatabaseVendor.UNKNOWN;
    }
    
    public boolean isOracle() {
        switch (this) {
        case ORACLE9I:
        case ORACLE10G:
        case ORACLE11G:
        case ORACLE12C:
            return true;
        default:
            return false;
        }
    }
    
    public boolean isSqlServer() {
        switch (this) {
        case MS2000:
        case MS2005:
        case MS2008:
        case MS2012:
            return true;
        default:
            return false;
        }
    }
}
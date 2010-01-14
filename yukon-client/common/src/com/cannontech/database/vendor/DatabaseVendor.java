package com.cannontech.database.vendor;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;


public enum DatabaseVendor {
    
    MS2000    ("Microsoft SQL Server",8),
    MS2005    ("Microsoft SQL Server",9),
    MS2008    ("Microsoft SQL Server",10),
    ORACLE9I  ("Oracle",9),
    ORACLE10G ("Oracle",10),
    ORACLE11G ("Oracle",11),
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
}


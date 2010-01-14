package com.cannontech.database.vendor;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;


public enum DatabaseVendor {
    
    MS        ("Microsoft SQL Server"),
    MS2000    ("Microsoft SQL Server",8),
    MS2005    ("Microsoft SQL Server",9),
    MS2008    ("Microsoft SQL Server",10),
    ORACLE    ("Oracle"),
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
    
    public static DatabaseVendor getDatabaseVendor(String vendorName, int majorVersion) {
        return getDatabaseVendor(vendorName, majorVersion, -1);
    }
    
    public static DatabaseVendor getDatabaseVendor(String vendorName, int majorVersion, int minorVersion) {
        
        // Try to match without the minor version
        for (DatabaseVendor databaseVendor : values()){
            if (databaseVendor.getVendorName().equalsIgnoreCase(vendorName) &&
                databaseVendor.getDatabaseMajorVersion() == majorVersion) {
                return databaseVendor;
            }
        }
        
        // Try to match without the major or minor version
        for (DatabaseVendor databaseVendor : values()){
            if (databaseVendor.getVendorName().equalsIgnoreCase(vendorName) &&
                databaseVendor.getDatabaseMajorVersion() == -1) {

                logger.warn("Your database version is not currently supported by Yukon: " + 
                            vendorName + " ("+majorVersion+"."+minorVersion+") ");
                return databaseVendor;
            }
        }
        
        logger.warn("Your database is not officially supported by Yukon: " + vendorName );
        return DatabaseVendor.UNKNOWN;
    }
}


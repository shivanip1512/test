package com.cannontech.database.vendor;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum DatabaseVendor {
    MS2000    ("Microsoft SQL Server","08."),
    MS2005    ("Microsoft SQL Server","09."),
    MS2008    ("Microsoft SQL Server","10."),
    ORACLE9I  ("Oracle","Oracle9i Enterprise Edition Release"),
    ORACLE10G ("Oracle","Oracle Database 10g Release");
    
    private final String venderText;
    private final String productVersionPrefix;
    
    DatabaseVendor(String venderText, String productVersionPrefix) {
        this.venderText = venderText;
        this.productVersionPrefix = productVersionPrefix;
    }
    private String getVenderText() { return this.venderText; }
    private String getProductVersionPrefix() { return this.productVersionPrefix; }

    private static Set<DatabaseVendor> oracleEnums = ImmutableSet.of(ORACLE9I, ORACLE10G);
    private static Set<DatabaseVendor> microsoftEnums = ImmutableSet.of(MS2000, MS2005, MS2008);

    public static DatabaseVendor getDatabaseVender(String venderText, String productVersion){
        for(DatabaseVendor databaseVendor: DatabaseVendor.values())
            if ((venderText.equals(databaseVendor.getVenderText())) &&
                (productVersion.startsWith(databaseVendor.getProductVersionPrefix())))
                return databaseVendor;
        
        throw new IllegalArgumentException(); 
    }

    public static Set<DatabaseVendor> getAllOracle() {
        return oracleEnums ;
    }
    
    public static Set<DatabaseVendor> getAllMicrosoft() {
        return microsoftEnums;
    }
    
    public boolean isOracle() {
        return oracleEnums.contains(this);
    }
    
    public boolean isMicrosoft() {
        return microsoftEnums.contains(this);
    }
}


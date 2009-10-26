package com.cannontech.database.vendor;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum DatabaseVendor {
    MS2000,
    MS2005,
    MS2008,
    ORACLE9I,
    ORACLE10G,
    ;
    
    private static Set<DatabaseVendor> oracleEnums = ImmutableSet.of(ORACLE9I, ORACLE10G);
    private static Set<DatabaseVendor> microsoftEnums = ImmutableSet.of(MS2000, MS2005, MS2008);

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


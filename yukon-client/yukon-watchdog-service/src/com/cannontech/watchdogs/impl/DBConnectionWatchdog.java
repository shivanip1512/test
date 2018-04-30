package com.cannontech.watchdogs.impl;

public interface DBConnectionWatchdog {
    
    /**
     * Will be used by other monitors to check if database connection is available. 
     */
    boolean isDBConnected(DBName dbName);

}

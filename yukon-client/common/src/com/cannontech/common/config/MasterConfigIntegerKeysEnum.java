package com.cannontech.common.config;

public enum MasterConfigIntegerKeysEnum {

    DEVICE_DATA_MONITOR_WORKER_COUNT,
    DEVICE_DATA_MONITOR_QUEUE_SIZE,

    PLC_ACTIONS_CANCEL_TIMEOUT,
    
    DB_JAVA_INITCONS,               // Initial number of database connections per PoolManager
    DB_JAVA_MINIDLECONS,            // Minimum number of idle database connections per PoolManager
    DB_JAVA_MAXIDLECONS,            // Maximum number of idle database connections per PoolManager
    DB_JAVA_MAXCONS                 // Maximum number of active database connections per PoolManager
    ;
}

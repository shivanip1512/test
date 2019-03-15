package com.cannontech.common.config;

public enum MasterConfigInteger {

    DEVICE_DATA_MONITOR_WORKER_COUNT,
    DEVICE_DATA_MONITOR_QUEUE_SIZE,

    PLC_ACTIONS_CANCEL_TIMEOUT,
    MAINTENANCE_DUPLICATE_POINT_DATA_DELETE_DURATION,

    DB_JAVA_INITCONS,               // Initial number of database connections per PoolManager
    DB_JAVA_MINIDLECONS,            // Minimum number of idle database connections per PoolManager
    DB_JAVA_MAXIDLECONS,            // Maximum number of idle database connections per PoolManager
    DB_JAVA_MAXCONS,                 // Maximum number of active database connections per PoolManager
    
    INFRASTRUCTURE_WARNING_MINIMUM_TIME_BETWEEN_RUNS,
    INFRASTRUCTURE_WARNING_RUN_FREQUENCY_MINUTES,
    
    ITRON_RECORD_IDS_PER_READ
    ;
}

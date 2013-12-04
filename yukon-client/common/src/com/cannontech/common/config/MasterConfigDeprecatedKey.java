package com.cannontech.common.config;


/**
 * Mark unused master.cfg CPARMS deprecated.
 * 
 * If a deprecated key is found in master.cfg it will be commented out with '(DEPRECATED)' prepended.
 * When a key is added to this enum it must be removed from the other master config enum types.
 */
public enum MasterConfigDeprecatedKey {

    BULK_POINT_LOAD_THRESHOLD, // removed in 4.1
    CAP_CONTROL_PORT,
    DB_RWDBDLL,
    DISPATCH_COMMERROR_DAYS, // Deprecated as of Yukon 6.0
    DISPATCH_MACHINE,
    DISPATCH_PORT,
    JMS_BROKER_HOST, // only ever used in development 6.0 - 6.0.4
    LOAD_MANAGEMENT_PORT,
    MACS_PORT,
    NOTIFICATION_MACHINE,
    NOTIFICATION_PORT,
    PIL_MACHINE,
    PIL_PORT,
    SCANNER_QUEUE, // see yuk-8552
    SCANNER_QUEUE_SCANS, // see yuk-8552

    /* YUKON_DNP_* deprecated with introduction of DNP Device Configurations in 5.5.2 */
    YUKON_DNP_INTERNAL_RETRIES, // 
    YUKON_DNP_LOCALTIME,
    YUKON_DNP_OMIT_TIME_REQUEST_DEVICEIDS,
    YUKON_DNP_TIMESYNCS,
    ;

    /**
     * Returns true if the CPARM key is deprecated
     * 
     * Throws NullPointerException if keyStr is null
     */
    public static boolean isDeprecated(String keyStr) {
        try {
            // If we get a value here we know this keyStr is a deprecated CPARM
            MasterConfigDeprecatedKey.valueOf(keyStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
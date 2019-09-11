package com.cannontech.common.config;

public enum MasterConfigString {

    AUTHENTICATION_TIMEOUT_STYLE,
    CAP_CONTROL_AMFM_DB_USERNAME(Encryption.ENCRYPTED),
    CAP_CONTROL_AMFM_DB_PASSWORD(Encryption.ENCRYPTED),
    DEMAND_MEASUREMENT_VERIFICATION_ENABLED(Encryption.ENCRYPTED),
    CMEP_UNITS,
    CYME_DIST_BASE_URL,
    CYME_INTEGRATION_SUBBUS,
    CYME_REPORT_NAME,
    CYME_SIM_FILE,

    DB_USERNAME(Encryption.ENCRYPTED),
    DB_PASSWORD(Encryption.ENCRYPTED),
    DB_SQLSERVER(Encryption.ENCRYPTED),
    DB_SQLSERVER_HOST(Encryption.ENCRYPTED),
    DB_TYPE,
    DB_JAVA_DRIVER,
    DB_JAVA_URL(Encryption.ENCRYPTED),
    DB_JAVA_VALIDATION_QUERY,

    EXCLUDED_POINT_QUALITIES, //YUK-11910
    
    JMS_CLIENT_BROKER_CONNECTION,
    JMS_CLIENT_CONNECTION,
    JMS_INTERNAL_MESSAGING_CONNECTION,
    JMS_SERVER_BROKER_LISTEN_CONNECTION,

    MAP_PROJECTION,
    
    MAP_DEVICES_STREET_URL,
    MAP_DEVICES_SATELLITE_URL,
    MAP_DEVICES_HYBRID_URL,
    MAP_DEVICES_ELEVATION_URL,
    MAP_DEVICES_KEY(Encryption.ENCRYPTED),
    
    METER_PROGRAMMING_ENABLED(Encryption.ENCRYPTED),
    
    MSP_ALTGROUP_EXTENSION, //YUK-10787
    MSP_SUBSTATIONNAME_EXTENSION, //YUK-10787
    
    RECAPTCHA_SITE_KEY, //YUK-17950
    RECAPTCHA_SECRET_KEY, //YUK-17950
    RECAPTCHA_VERIFY_URL, //YUK-17950
    
    RFN_ENERGY_COMPANY_NAME,
    RFN_METER_DISCONNECT_ARMING,
    RFN_METER_TEMPLATE_PREFIX,
    
    SMART_NOTIFICATION_INTERVALS,
    
    SUPPORT_BUNDLE_FTP_UPLOAD_USER(Encryption.ENCRYPTED),
    SUPPORT_BUNDLE_FTP_UPLOAD_PASSWORD(Encryption.ENCRYPTED),
    SUPPORT_BUNDLE_FTP_UPLOAD_HOST(Encryption.ENCRYPTED),
    
    YUKON_EXTERNAL_URL,
    ;

    private static enum Encryption {
        NONE,
        ENCRYPTED
    };
    
    private final Encryption encryption;
    
    MasterConfigString() {
        this(Encryption.NONE);
    }
    
    MasterConfigString(Encryption encryption) {
        this.encryption = encryption;
    }
    
    /**
     * Returns true if the CPARM key is an encrypted value, such as DB_PASSWORD.
     * 
     * @param key
     * @return true if {@code key} is a sensitive value which is (or should be) encrypted, false otherwise.
     * 
     */
    public static boolean isEncryptedKey(MasterConfigString key) {
        return key.encryption == Encryption.ENCRYPTED;
    }
    
    /**
     * Returns true if the CPARM key is an encrypted value for example: DB_PASSWORD.
     * <br>This should only be used by low-level loading code, e.g. MasterConfigMap.
     * 
     * <p>All other code should use {@link #isEncryptedKey(MasterConfigString)}
     * 
     * @param key
     * @return true if {@code key} is a sensitive value which is (or should be) encrypted, false otherwise.
     * 
     * Throws NullPointerException if {@code key} is null
     */
    static boolean isEncryptedKey(String key) {
        try {
            return isEncryptedKey(valueOf(key));
        } catch (@SuppressWarnings("unused") IllegalArgumentException e) {
            return false; // Some valid CPARMS might pass through this method that are not in this enum
        }
    }
}

package com.cannontech.common.config;

import java.util.HashSet;
import java.util.Set;

public enum MasterConfigString {

    AUTHENTICATION_TIMEOUT_STYLE,
    CAP_CONTROL_AMFM_DB_USERNAME,
    CAP_CONTROL_AMFM_DB_PASSWORD,
    DEMAND_MEASUREMENT_VERIFICATION_ENABLED,
    CMEP_UNITS,
    CYME_DIST_BASE_URL,
    CYME_INTEGRATION_SUBBUS,
    CYME_REPORT_NAME,
    CYME_SIM_FILE,

    DB_USERNAME,
    DB_PASSWORD,
    DB_SQLSERVER,
    DB_SQLSERVER_HOST,
    DB_TYPE,
    DB_JAVA_DRIVER,
    DB_JAVA_URL,
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
    MAP_DEVICES_KEY,
    
    MSP_ALTGROUP_EXTENSION, //YUK-10787
    MSP_SUBSTATIONNAME_EXTENSION, //YUK-10787
    
    RECAPTCHA_SITE_KEY, //YUK-17950
    RECAPTCHA_SECRET_KEY, //YUK-17950
    RECAPTCHA_VERIFY_URL, //YUK-17950
    
    RFN_ENERGY_COMPANY_NAME,
    RFN_METER_DISCONNECT_ARMING,
    RFN_METER_TEMPLATE_PREFIX,
    
    SMART_NOTIFICATION_INTERVALS,
    
    SUPPORT_BUNDLE_FTP_UPLOAD_USER,
    SUPPORT_BUNDLE_FTP_UPLOAD_PASSWORD,
    SUPPORT_BUNDLE_FTP_UPLOAD_HOST,
    
    YUKON_EXTERNAL_URL,
    ;
    
    private static final Set<MasterConfigString> sensitiveData;
    
    /**
     * If you add to this list, update the MasterConfigCryptoUtilsTest as well.
     */
    static {
        sensitiveData = new HashSet<>();
        sensitiveData.add(CAP_CONTROL_AMFM_DB_USERNAME);
        sensitiveData.add(CAP_CONTROL_AMFM_DB_PASSWORD);
        sensitiveData.add(DEMAND_MEASUREMENT_VERIFICATION_ENABLED);
        sensitiveData.add(DB_USERNAME);
        sensitiveData.add(DB_PASSWORD);
        sensitiveData.add(DB_SQLSERVER);
        sensitiveData.add(DB_SQLSERVER_HOST);
        sensitiveData.add(DB_JAVA_URL);
        sensitiveData.add(MAP_DEVICES_KEY);
        sensitiveData.add(SUPPORT_BUNDLE_FTP_UPLOAD_USER);
        sensitiveData.add(SUPPORT_BUNDLE_FTP_UPLOAD_PASSWORD);
        sensitiveData.add(SUPPORT_BUNDLE_FTP_UPLOAD_HOST);
    }
    
    /**
     * Returns true if the CPARM key is an encrypted value for example: DB_PASSWORD.
     * 
     * @param key
     * @return true if key is a sensitive value which will be or is encrypted false otherwise
     * 
     */
    public static boolean isEncryptedKey(MasterConfigString key) {
        return sensitiveData.contains(key);
    }
    
    /**
     * Returns true if the CPARM key is an encrypted value for example: DB_PASSWORD.
     * 
     * It is prefered to use {@link #isEncryptedKey(MasterConfigString)}
     * 
     * @param key
     * @return true if key is a sensitive value which will be or is encrypted false otherwise
     * 
     * Throws NullPointerException if key sent in is null
     */
    public static boolean isEncryptedKey(String key) {
        try {
            return isEncryptedKey(valueOf(key));
        } catch (IllegalArgumentException  e) {
            return false; // Some valid CPARMS might pass through this method that are not in this enum
        }
    }
}

package com.cannontech.common.config;

import java.util.HashSet;
import java.util.Set;

public enum MasterConfigStringKeysEnum {

    AUTHENTICATION_TIMEOUT_STYLE,
    CMEP_UNITS,
    CYME_DIST_BASE_URL,
    CYME_INTEGRATION_SUBBUS,
    CYME_REPORT_NAME,
    CYME_SIM_FILE,
    DB_USERNAME,
    DB_PASSWORD,
    DB_SQLSERVER,
    DB_SQLSERVER_HOST,
    DB_JAVA_URL,
    RECAPTCHA_PUBLIC_KEY,
    RECAPTCHA_PRIVATE_KEY, 
    SUPPORT_BUNDLE_FTP_UPLOAD_USER,
    SUPPORT_BUNDLE_FTP_UPLOAD_PASSWORD,
    SUPPORT_BUNDLE_FTP_UPLOAD_HOST,
    YUKON_EXTERNAL_URL,
    MSP_ALTGROUP_EXTENSION,             //YUK-10787
    MSP_SUBSTATIONNAME_EXTENSION,   //YUK-10787
    RFN_METER_DISCONNECT_ARMING,
    ;
    
    private static final Set<MasterConfigStringKeysEnum> sensitiveData;
    
    static {
        sensitiveData = new HashSet<MasterConfigStringKeysEnum>();
        sensitiveData.add(DB_USERNAME);
        sensitiveData.add(DB_PASSWORD);
        sensitiveData.add(DB_SQLSERVER);
        sensitiveData.add(DB_SQLSERVER_HOST);
        sensitiveData.add(DB_JAVA_URL);
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
    public static boolean isEncryptedKey(MasterConfigStringKeysEnum key) {
        return sensitiveData.contains(key);
    }
    
    /**
     * Returns true if the CPARM key is an encrypted value for example: DB_PASSWORD.
     * 
     * It is prefered to use {@link #isEncryptedKey(MasterConfigStringKeysEnum)}
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

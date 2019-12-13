package com.cannontech.common.config;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.model.GlobalSetting;
import com.cannontech.web.input.type.InputType;
import com.google.common.collect.Maps;

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
    YUKON_SMTP_SERVER, // used when dispatch sent notifications and it no longer does
    CALC_LOGIC_RUN_HISTORICAL, //see YUK-14245
    NM_COMPATIBILITY, //See YUK-16074
    CAP_CONTROL_IVVC_REGULATOR_REPORTING_RATIO,  // see yuk-17611
    CAP_CONTROL_IVVC_VOLTAGEMONITOR_REPORTING_RATIO,  // see yuk-17611
    CAP_CONTROL_IVVC_BANKS_REPORTING_RATIO,  // see yuk-17611
    RECAPTCHA_PUBLIC_KEY, //public key used for CAPTCHA V1 , as we moved to reCAPTCHA V2 so no longer needed.
    RECAPTCHA_PRIVATE_KEY, //private key used for CAPTCHA V1 , as we moved to reCAPTCHA V2 so no longer needed.
    CAP_CONTROL_AMFM_INTERFACE, // see yuk-20627
    CAP_CONTROL_AMFM_RELOAD_RATE, // see yuk-20627
    CAP_CONTROL_AMFM_DB_SQLSERVER, // see yuk-20627
    CAP_CONTROL_AMFM_DB_TYPE, // see yuk-20627
    CAP_CONTROL_AMFM_DB_USERNAME, // see yuk-20627
    CAP_CONTROL_AMFM_DB_PASSWORD, // see yuk-20627
    
    

    LOG_FILE_RETENTION(GlobalSettingType.LOG_RETENTION_DAYS, UnaryOperator.identity()),
    
    /* The following YUKON_DNP_* keys were intended to be deprecated but have been commented out of this enum.
     * This was done to prevent the 5.5.2 update script from throwing an exception when
     * attempting to look up these CPARMs using the @start-cparm annotation.
     * See YUK-12836 for more information */

//    YUKON_DNP_INTERNAL_RETRIES, 
//    YUKON_DNP_LOCALTIME,
//    YUKON_DNP_OMIT_TIME_REQUEST_DEVICEIDS,
//    YUKON_DNP_TIMESYNCS,
    ;

    private static final Map<String, MasterConfigDeprecatedKey> keyLookup = Maps.uniqueIndex(Arrays.asList(values()), Enum::name);
    
    private final GlobalSettingType globalSettingType;
    private final Function<String, String> translation;
    
    private MasterConfigDeprecatedKey() {
        this.globalSettingType = null;
        this.translation = null;
    }
    
    private MasterConfigDeprecatedKey(GlobalSettingType migration, UnaryOperator<String> translation) {
        this.globalSettingType = migration;
        this.translation = translation;
    }
    
    public GlobalSettingType getGlobalSettingType() {
        return globalSettingType;
    }
    public boolean canMigrateToGlobalSetting() {
        return globalSettingType != null && translation != null;
    }

    public Optional<GlobalSetting> migrate(String value) {
        return Optional.ofNullable(value)
                .filter(x -> canMigrateToGlobalSetting())
                .map(translation)
                .filter(StringUtils::isNotBlank)
                .flatMap(translatedValue -> createObject(translatedValue, globalSettingType.getType()))
                .map(obj -> new GlobalSetting(globalSettingType, obj));
    }

    //  Adapted from YukonResultSet
    private static Optional<? extends Object> createObject(String value, InputType<?> type) {
        Class<?> valueType = type.getTypeClass();
        
        if(valueType == Boolean.class) {
            return Optional.ofNullable(Boolean.valueOf(value));
        }
        if(valueType == String.class) {
            return Optional.ofNullable(value);
        } 
        if(valueType == Integer.class) {
            return Optional.ofNullable(Integer.valueOf(value));
        } 
        if (valueType == Double.class) {
            return Optional.ofNullable(Double.valueOf(value));
        } 
        if (valueType.isEnum()) {
            Function<Enum<?>, String> mapping;
            if (DatabaseRepresentationSource.class.isAssignableFrom(valueType)) {
                mapping = e -> ((DatabaseRepresentationSource)e).getDatabaseRepresentation().toString();
            } else {
                mapping = Enum<?>::name;
            }
            
            return Arrays.stream(valueType.getEnumConstants())
                        .map(e -> (Enum<?>)e)
                        .filter(e -> mapping.apply(e).matches(value))
                        .findFirst();
        } 
        throw new UnsupportedOperationException("Unhandled InputType " + valueType);
    }
    
    public static Optional<MasterConfigDeprecatedKey> find(String keyStr) {
        return Optional.ofNullable(keyLookup.get(keyStr));
    }
    
    /**
     * Returns true if the CPARM key is deprecated
     */
    public static boolean isDeprecated(String keyStr) {
        return keyLookup.containsKey(keyStr);
    }
}
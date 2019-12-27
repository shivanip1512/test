package com.cannontech.system;

import static com.cannontech.core.roleproperties.InputTypeFactory.*;

import java.math.RoundingMode;
import java.util.Set;

import com.cannontech.amr.meter.dao.impl.MeterDisplayFieldEnum;
import com.cannontech.clientutils.ClientApplicationRememberMe;
import com.cannontech.common.config.SmtpEncryptionType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.login.ldap.LDAPEncryptionType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.roleproperties.CisDetailRolePropertyEnum;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakManagePaoLocation;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.system.GlobalSettingTypeValidators.TypeValidator;
import com.cannontech.web.input.type.InputType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSetMultimap.Builder;

public enum GlobalSettingType implements DisplayableEnum {

    // Authentication
    // Authentication > General
    DEFAULT_AUTH_TYPE(GlobalSettingSubCategory.AUTHENTICATION, InputTypeFactory.enumType(AuthenticationCategory.class), AuthenticationCategory.ENCRYPTED),  //Is this ENUM correct? Or should it be AuthType?

    // Authentication > Radius (only enabled when DEFAULT_AUTH_TYPE = RADIUS)
    SERVER_ADDRESS(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "127.0.0.1", GlobalSettingTypeValidators.ipHostNameValidator),
    AUTH_PORT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 1812, GlobalSettingTypeValidators.portValidator),
    ACCT_PORT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 1813, GlobalSettingTypeValidators.portValidator),
    SECRET_KEY(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "cti"),
    AUTH_TIMEOUT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 30),

    // Authentication > LDAP (only enabled when DEFAULT_AUTH_TYPE = LDAP)
    LDAP_DN(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "dc=example,dc=com"),
    LDAP_USER_SUFFIX(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "ou=users"),
    LDAP_USER_PREFIX(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "uid="),
    LDAP_SERVER_ADDRESS(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "127.0.0.1", GlobalSettingTypeValidators.ipHostNameValidator),
    LDAP_SERVER_PORT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 389, GlobalSettingTypeValidators.portValidator),
    LDAP_SERVER_TIMEOUT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 30),
    LDAP_SSL_ENABLED(GlobalSettingSubCategory.AUTHENTICATION, InputTypeFactory.enumType(LDAPEncryptionType.class), LDAPEncryptionType.NONE),

    // Authentication > Active Directory (only enabled when DEFAULT_AUTH_TYPE = AD)
    AD_SERVER_ADDRESS(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "127.0.0.1", GlobalSettingTypeValidators.ipHostNameValidator),
    AD_SERVER_PORT(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "389", GlobalSettingTypeValidators.portsValidator), // stringType because space separated listed of ints is allowed
    AD_SERVER_TIMEOUT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 30),
    AD_NTDOMAIN(GlobalSettingSubCategory.AUTHENTICATION, stringType(), null),
    AD_SSL_ENABLED(GlobalSettingSubCategory.AUTHENTICATION, InputTypeFactory.enumType(LDAPEncryptionType.class), LDAPEncryptionType.NONE),

    CLIENT_APPLICATIONS_REMEMBER_ME(GlobalSettingSubCategory.AUTHENTICATION,
                                   InputTypeFactory.enumType(ClientApplicationRememberMe.class),
                                   ClientApplicationRememberMe.USERNAME),

    // Authentication > Password Management  (this could go under Authentication > General if we didn't want two sub sections with so few items)
    ENABLE_PASSWORD_RECOVERY(GlobalSettingSubCategory.AUTHENTICATION, booleanType(), true),
    ENABLE_CAPTCHAS(GlobalSettingSubCategory.AUTHENTICATION, booleanType(), true),
    // END Authentication

    // Multispeak
    MSP_PAONAME_ALIAS(GlobalSettingSubCategory.MULTISPEAK, InputTypeFactory.enumType(MspPaoNameAliasEnum.class), MspPaoNameAliasEnum.METER_NUMBER),
    MSP_PAONAME_EXTENSION(GlobalSettingSubCategory.MULTISPEAK, stringType(), null),
    MSP_PRIMARY_CB_VENDORID(GlobalSettingSubCategory.MULTISPEAK, integerType(), 0),
    MSP_BILLING_CYCLE_PARENT_DEVICEGROUP(GlobalSettingSubCategory.MULTISPEAK, stringType(), "/Meters/Billing/"),
    MSP_LM_MAPPING_SETUP(GlobalSettingSubCategory.MULTISPEAK, booleanType(), false),
    MSP_METER_LOOKUP_FIELD(GlobalSettingSubCategory.MULTISPEAK, InputTypeFactory.enumType(MultispeakMeterLookupFieldEnum.class), MultispeakMeterLookupFieldEnum.AUTO_METER_NUMBER_FIRST),
    MSP_EXCLUDE_DISABLED_METERS(GlobalSettingSubCategory.MULTISPEAK, booleanType(), false),
    MSP_RFN_PING_FORCE_CHANNEL_READ(GlobalSettingSubCategory.MULTISPEAK, booleanType(), false),      //YUK-13818
    MSP_DISABLE_DISCONNECT_STATUS(GlobalSettingSubCategory.MULTISPEAK, booleanType(), false),
    CIS_DETAIL_TYPE(GlobalSettingSubCategory.MULTISPEAK, InputTypeFactory.enumType(CisDetailRolePropertyEnum.class), CisDetailRolePropertyEnum.NONE),
    MSP_REMOVE_DEVICE_FROM_CIS_GROUP(GlobalSettingSubCategory.MULTISPEAK, booleanType(), false),
    MSP_MANAGE_PAO_LOCATION(GlobalSettingSubCategory.MULTISPEAK, InputTypeFactory.enumType(MultispeakManagePaoLocation.class), MultispeakManagePaoLocation.NONE),
    
    // OpenADR
    OADR_REQUEST_INTERVAL(GlobalSettingSubCategory.OPEN_ADR, integerType(), 60000),
    OADR_VEN_ID(GlobalSettingSubCategory.OPEN_ADR, stringType(), "VEN"),
    OADR_VTN_ID(GlobalSettingSubCategory.OPEN_ADR, stringType(), "VTN"),
    OADR_VTN_URL(GlobalSettingSubCategory.OPEN_ADR, stringType(), null),
    OADR_KEYSTORE_PATH(GlobalSettingSubCategory.OPEN_ADR, stringType(), null),
    OADR_KEYSTORE_PASSWORD(GlobalSettingSubCategory.OPEN_ADR, stringType(), null),
    OADR_TRUSTSTORE_PATH(GlobalSettingSubCategory.OPEN_ADR, stringType(), null),
    OADR_TRUSTSTORE_PASSWORD(GlobalSettingSubCategory.OPEN_ADR, stringType(), null),
    OADR_REPLY_LIMIT(GlobalSettingSubCategory.OPEN_ADR, integerType(), 0),
    OADR_VTN_THUMBPRINT(GlobalSettingSubCategory.OPEN_ADR, stringType(), null),
    OADR_YUKON_USER(GlobalSettingSubCategory.OPEN_ADR, userType(), null),
    OADR_OPEN_ENDED_CONTROL_DURATION(GlobalSettingSubCategory.OPEN_ADR, stringType(), null),

    // Yukon Services
    JMS_BROKER_HOST(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "localhost", GlobalSettingTypeValidators.ipHostNameValidator),
    JMS_BROKER_PORT(GlobalSettingSubCategory.YUKON_SERVICES, integerType(), 61616, GlobalSettingTypeValidators.portValidator),
    SMTP_HOST(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), null, GlobalSettingTypeValidators.ipHostNameValidator),
    SMTP_PORT(GlobalSettingSubCategory.YUKON_SERVICES, integerType(), null, GlobalSettingTypeValidators.portValidator),
    SMTP_USERNAME(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), null),
    SMTP_PASSWORD(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), null),
    SMTP_ENCRYPTION_TYPE(GlobalSettingSubCategory.YUKON_SERVICES, InputTypeFactory.enumType(SmtpEncryptionType.class), SmtpEncryptionType.NONE),
    MAIL_FROM_ADDRESS(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "yukon@eaton.com", GlobalSettingTypeValidators.emailValidator),
    NETWORK_MANAGER_ADDRESS(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "http://127.0.0.1:8081/nmclient/", GlobalSettingTypeValidators.urlValidator),
    RFN_FIRMWARE_UPDATE_SERVER(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "https://127.0.0.1:8443/updateserver/latest/", GlobalSettingTypeValidators.urlValidator),
    RFN_FIRMWARE_UPDATE_SERVER_USER(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "gateway"),
    RFN_FIRMWARE_UPDATE_SERVER_PASSWORD(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "gwupdate"),

    // DR
    BATCHED_SWITCH_COMMAND_TOGGLE(GlobalSettingSubCategory.DR, stringType(), "auto"),
    STARS_PRELOAD_DATA(GlobalSettingSubCategory.DR, booleanType(), true),
    CUSTOMER_INFO_IMPORTER_FILE_LOCATION(GlobalSettingSubCategory.DR, stringType(), null),
    OPT_OUTS_COUNT(GlobalSettingSubCategory.DR, booleanType(), true),
    LAST_COMMUNICATION_HOURS(GlobalSettingSubCategory.DR, 60, Range.inclusive(0, 8784)),
    LAST_RUNTIME_HOURS(GlobalSettingSubCategory.DR, 168, Range.inclusive(0, 8784)),
    RF_BROADCAST_PERFORMANCE(GlobalSettingSubCategory.DR, InputTypeFactory.enumType(OnOff.class), OnOff.OFF),
    ECOBEE_USERNAME(GlobalSettingSubCategory.DR, stringType(), null),
    ECOBEE_PASSWORD(GlobalSettingSubCategory.DR, stringType(), null),
    ECOBEE_SERVER_URL(GlobalSettingSubCategory.DR, stringType(), "https://api.ecobee.com/1/", GlobalSettingTypeValidators.urlValidator),
    ECOBEE_SEND_NOTIFICATIONS(GlobalSettingSubCategory.DR, booleanType(), false),
    HONEYWELL_WIFI_SERVICE_BUS_QUEUE(GlobalSettingSubCategory.DR, stringType(), null),
    HONEYWELL_WIFI_SERVICE_BUS_CONNECTION_STRING(GlobalSettingSubCategory.DR, stringType(), null),
    HONEYWELL_SERVER_URL(GlobalSettingSubCategory.DR, stringType(), "https://qtccna.honeywell.com/TrueHomeStage/", GlobalSettingTypeValidators.urlValidator),
    HONEYWELL_APPLICATIONID(GlobalSettingSubCategory.DR, stringType(), null),
    HONEYWELL_CLIENTID(GlobalSettingSubCategory.DR, stringType(), null),
    HONEYWELL_SECRET(GlobalSettingSubCategory.DR, stringType(), null),
    ITRON_HCM_API_URL(GlobalSettingSubCategory.DR, stringType(), null, GlobalSettingTypeValidators.urlValidator),
    ITRON_HCM_USERNAME(GlobalSettingSubCategory.DR, stringType(), null),
    ITRON_HCM_PASSWORD(GlobalSettingSubCategory.DR, stringType(), null),
    ITRON_HCM_DATA_COLLECTION_HOURS(GlobalSettingSubCategory.DR, integerType(), 4),
    ITRON_SFTP_URL(GlobalSettingSubCategory.DR, stringType(), null),
    ITRON_SFTP_USERNAME(GlobalSettingSubCategory.DR, stringType(), null),
    ITRON_SFTP_PASSWORD(GlobalSettingSubCategory.DR, stringType(), null),
    ITRON_SFTP_PRIVATE_KEY_PASSWORD(GlobalSettingSubCategory.DR, stringType(), null),

    // Web Server
    GOOGLE_ANALYTICS_ENABLED(GlobalSettingSubCategory.WEB_SERVER, booleanType(), true),
    GOOGLE_ANALYTICS_TRACKING_IDS(GlobalSettingSubCategory.WEB_SERVER, stringType(), null),
    YUKON_EXTERNAL_URL(GlobalSettingSubCategory.WEB_SERVER, stringType(), "http://127.0.0.1:8080", GlobalSettingTypeValidators.urlValidator),
    YUKON_INTERNAL_URL(GlobalSettingSubCategory.WEB_SERVER, stringType(), null, GlobalSettingTypeValidators.urlValidator),
    
    // Data Import/Export (previously Billing)
    WIZ_ACTIVATE(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, booleanType(), false),
    INPUT_FILE(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, stringType(), "c:\\yukon\\client\\bin\\BillingIn.txt"),
    DEFAULT_BILLING_FORMAT(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, stringType(), "CTI-CSV"),
    DEMAND_DAYS_PREVIOUS(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, integerType(), 30),
    ENERGY_DAYS_PREVIOUS(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, integerType(), 7),
    APPEND_TO_FILE(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, booleanType(), false),
    REMOVE_MULTIPLIER(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, booleanType(), false),
    COOP_ID_CADP_ONLY(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, stringType(), null),
    DEFAULT_ROUNDING_MODE(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, InputTypeFactory.enumType(RoundingMode.class), RoundingMode.HALF_EVEN),
    SCHEDULE_PARAMETERS_AVAILABLE_FILE_EXTENSIONS(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, stringType(), ".csv,.dat,.txt"),
    // Defaulting to directory specified by CTIUtilities.getExportDirPath(), typically C:\Yukon\Client\Export.
    SCHEDULE_PARAMETERS_IMPORT_PATH(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, stringType(), CtiUtilities.getImportDirPath()),
    SCHEDULE_PARAMETERS_EXPORT_PATH(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, stringType(), CtiUtilities.getExportDirPath()),
    HISTORY_CLEANUP_DAYS_TO_KEEP(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, 365, Range.inclusive(0, Integer.MAX_VALUE)),
    HISTORY_CLEANUP_FILES_TO_KEEP(GlobalSettingSubCategory.DATA_IMPORT_EXPORT, 0, Range.inclusive(0, Integer.MAX_VALUE)),

    // AMI  (may fit better for future under Misc.?)
    DEVICE_DISPLAY_TEMPLATE(GlobalSettingSubCategory.AMI, InputTypeFactory.enumType(MeterDisplayFieldEnum.class), MeterDisplayFieldEnum.DEVICE_NAME),
    // This may eventually be a "Device" setting, just just "AMR", This is to disable "route lookup" during the OLD bulk importer process; reduces comms
    BULK_IMPORTER_COMMUNICATIONS_ENABLED(GlobalSettingSubCategory.AMI, booleanType(), true),
    PRESERVE_ENDPOINT_LOCATION(GlobalSettingSubCategory.AMI, booleanType(), true),
    RFN_INCOMING_DATA_TIMESTAMP_LIMIT(GlobalSettingSubCategory.AMI, 6, Range.inclusive(0, 240)),
    STATUS_POINT_MONITOR_NOTIFICATION_LIMIT(GlobalSettingSubCategory.AMI, integerType(), 1),

    // Misc.
    SYSTEM_TIMEZONE(GlobalSettingSubCategory.MISC, stringType(), null, GlobalSettingTypeValidators.timezoneValidator),
    ALERT_TIMEOUT_HOURS(GlobalSettingSubCategory.MISC, integerType(), 168),
    DATABASE_MIGRATION_FILE_LOCATION(GlobalSettingSubCategory.MISC, stringType(), "/Server/Export/"),
    TEMP_DEVICE_GROUP_DELETION_IN_DAYS(GlobalSettingSubCategory.MISC, integerType(), 7),
    HTTP_PROXY(GlobalSettingSubCategory.MISC, stringType(), null, GlobalSettingTypeValidators.urlWithPortValidator),
    CONTACT_EMAIL(GlobalSettingSubCategory.MISC, stringType(), "EAS-Support@Eaton.com", GlobalSettingTypeValidators.emailValidator),
    CONTACT_PHONE(GlobalSettingSubCategory.MISC, stringType(), "1-800-815-2258"),
    SCHEDULED_REQUEST_MAX_RUN_HOURS(GlobalSettingSubCategory.MISC, integerType(), 23),
    PRODUCER_WINDOW_SIZE(GlobalSettingSubCategory.MISC, integerType(), 1024),
    MAX_INACTIVITY_DURATION(GlobalSettingSubCategory.MISC, integerType(), 30),
    ERROR_REPORTING(GlobalSettingSubCategory.MISC, booleanType(), false),
    MAX_LOG_FILE_SIZE(GlobalSettingSubCategory.MISC, integerType(), 1),
    LOG_RETENTION_DAYS(GlobalSettingSubCategory.MISC, integerType(), 90),
    NETWORK_MANAGER_DB_PASSWORD(GlobalSettingSubCategory.MISC, stringType(), null),
    NETWORK_MANAGER_DB_USER(GlobalSettingSubCategory.MISC, stringType(), null),
    NETWORK_MANAGER_DB_HOSTNAME(GlobalSettingSubCategory.MISC, stringType(), null),
    CLOUD_DATA_SENDING_FREQUENCY(GlobalSettingSubCategory.MISC, 6, Range.inclusive(1, Integer.MAX_VALUE)),
    CLOUD_IOT_HUB_CONNECTION_STRING(GlobalSettingSubCategory.MISC, stringType(), null),

    // Misc. > Web Graph
    HOME_DIRECTORY(GlobalSettingSubCategory.MISC, stringType(), "c:\\yukon\\client\\webgraphs\\"),
    RUN_INTERVAL(GlobalSettingSubCategory.MISC, integerType(), 900),
    
    // Trending
    TRENDS_HISTORICAL_MONTHS(GlobalSettingSubCategory.GRAPHING, integerType(), 24),
    TRENDS_READING_PER_POINT(GlobalSettingSubCategory.GRAPHING, integerType(), 70080), // 15min interval data x 2 years.
    
    // Voice Server
    CALL_RESPONSE_TIMEOUT(GlobalSettingSubCategory.VOICE, integerType(), 240),
    CALL_PREFIX(GlobalSettingSubCategory.VOICE, stringType(), null),
    
    // Dashboard Widgets
    DATA_AVAILABILITY_WINDOW_IN_DAYS(GlobalSettingSubCategory.DASHBOARD_WIDGET, 3, Range.inclusive(1, 7)),
    GATEWAY_CONNECTION_WARNING_MINUTES(GlobalSettingSubCategory.DASHBOARD_WIDGET, integerType(), 60),
    GATEWAY_CONNECTED_NODES_WARNING_THRESHOLD(GlobalSettingSubCategory.DASHBOARD_WIDGET, integerType(), 3500),
    GATEWAY_CONNECTED_NODES_CRITICAL_THRESHOLD(GlobalSettingSubCategory.DASHBOARD_WIDGET, integerType(), 5000),
    GATEWAY_READY_NODES_THRESHOLD(GlobalSettingSubCategory.DASHBOARD_WIDGET, integerType(), 25),
    PORTER_QUEUE_COUNTS_HISTORICAL_MONTHS(GlobalSettingSubCategory.DASHBOARD_WIDGET, integerType(), 3),
    PORTER_QUEUE_COUNTS_TREND_MAX_NUM_PORTS(GlobalSettingSubCategory.DASHBOARD_WIDGET, integerType(), 5),
    PORTER_QUEUE_COUNTS_MINUTES_TO_WAIT_BEFORE_REFRESH(GlobalSettingSubCategory.DASHBOARD_WIDGET, integerType(), 15),

    //Data Point Pruning
    BUSINESS_DAYS(GlobalSettingSubCategory.MISC, weekDaysType("BUSINESS_DAYS"), "NYYYYYN"),
    EXTERNAL_MAINTENANCE_DAYS(GlobalSettingSubCategory.MISC, weekDaysType("EXTERNAL_MAINTENANCE_DAYS"), "YNNNNNN"),
    BUSINESS_HOURS_START_STOP_TIME(GlobalSettingSubCategory.MISC, sliderType("BUSINESS_HOURS_START_STOP_TIME", 24), "420,1020"),
    EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME(GlobalSettingSubCategory.MISC, sliderType("EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME", 48), "60,180")
    ;

    private static final ImmutableSetMultimap<GlobalSettingSubCategory, GlobalSettingType> categoryMapping;
    private final InputType<?> type;
    private final Object defaultValue;
    private final GlobalSettingSubCategory category;
    private final Object validationValue;
    private final TypeValidator<?> validator; 
    private final static ImmutableList<GlobalSettingType> sensitiveSettings;

    static {
        final Builder<GlobalSettingSubCategory, GlobalSettingType> b = ImmutableSetMultimap.builder();
        for (GlobalSettingType globalSettingType : values()) {
            b.put(globalSettingType.getCategory(), globalSettingType);
        }
        categoryMapping = b.build();
        
        sensitiveSettings = ImmutableList.of(
            ECOBEE_PASSWORD,
            ECOBEE_USERNAME,
            RFN_FIRMWARE_UPDATE_SERVER_USER,
            RFN_FIRMWARE_UPDATE_SERVER_PASSWORD,
            HONEYWELL_WIFI_SERVICE_BUS_CONNECTION_STRING,
            HONEYWELL_WIFI_SERVICE_BUS_QUEUE,
            HONEYWELL_APPLICATIONID,
            HONEYWELL_CLIENTID,
            HONEYWELL_SECRET,
            OADR_KEYSTORE_PASSWORD,
            OADR_TRUSTSTORE_PASSWORD,
            SMTP_USERNAME,
            SMTP_PASSWORD,
            ITRON_HCM_USERNAME,
            ITRON_HCM_PASSWORD,
            ITRON_SFTP_USERNAME,
            ITRON_SFTP_PASSWORD,
            ITRON_SFTP_PRIVATE_KEY_PASSWORD);
        }

    private GlobalSettingType(GlobalSettingSubCategory category, InputType<?> type, Object defaultValue) {
        this.type = type;
        this.category = category;
        this.defaultValue = defaultValue;
        validator = null;
        validationValue = null;
    }
    
    private <T> GlobalSettingType(GlobalSettingSubCategory category, InputType<T> type, Object defaultValue, TypeValidator<T> validator) {
        this.type = type;
        this.category = category;
        this.defaultValue = defaultValue;
        this.validator = validator;
        validationValue = null;
    }

/*  TODO  
    private GlobalSettingType(GlobalSettingSubCategory category, InputType<?> type, Object defaultValue, TypeValidator validator, Object validationValue) {
        this.type = type;
        this.category = category;
        this.defaultValue = defaultValue;
        this.validator = validator;
        this.validationValue = validationValue;
    }
*/
    /**
     * Constructor for integerType
     */
    private GlobalSettingType(GlobalSettingSubCategory category, Integer defaultValue, Range<Integer> integerRange) {
        type = integerType();
        this.category = category;
        this.defaultValue = defaultValue;
        validator = GlobalSettingTypeValidators.integerRangeValidator;
        validationValue = integerRange;
    }
    
    public InputType<?> getType() {
        return type;
    }

    public boolean isSensitiveInformation() {
        return sensitiveSettings.contains(this);
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public GlobalSettingSubCategory getCategory() {
        return category;
    }
    
    public TypeValidator<?> getValidator() {
        return validator;
    }
    
    public Object getValidationValue() {
        return validationValue;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.setting." + name();
    }

    public String getDescriptionKey() {
        return getFormatKey() + ".description";
    }

    public static Set<GlobalSettingType> getSettingsForCategory(GlobalSettingSubCategory category) {
        return categoryMapping.get(category);
    }

    public static ImmutableList<GlobalSettingType> getSensitiveSettings() {
        return sensitiveSettings;
    }

}
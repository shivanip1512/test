package com.cannontech.system;

import static com.cannontech.core.roleproperties.InputTypeFactory.booleanType;
import static com.cannontech.core.roleproperties.InputTypeFactory.integerType;
import static com.cannontech.core.roleproperties.InputTypeFactory.stringType;
import static com.cannontech.core.roleproperties.InputTypeFactory.userType;

import java.math.RoundingMode;
import java.util.Set;

import com.cannontech.amr.meter.dao.impl.MeterDisplayFieldEnum;
import com.cannontech.clientutils.ClientApplicationRememberMe;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.web.input.type.InputType;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSetMultimap.Builder;

public enum GlobalSettingType implements DisplayableEnum {

    // Authentication
    // Authentication > General
    DEFAULT_AUTH_TYPE(GlobalSettingSubCategory.AUTHENTICATION, InputTypeFactory.enumType(AuthenticationCategory.class), AuthenticationCategory.ENCRYPTED),  //Is this ENUM correct? Or should it be AuthType?

    // Authentication > Radius (only enabled when DEFAULT_AUTH_TYPE = RADIUS)
    SERVER_ADDRESS(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "127.0.0.1"),
    AUTH_PORT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 1812),
    ACCT_PORT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 1813),
    SECRET_KEY(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "cti"),
    AUTH_METHOD(GlobalSettingSubCategory.AUTHENTICATION, stringType(), null),
    AUTHENTICATION_MODE(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "Yukon"),
    AUTH_TIMEOUT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 30),

    // Authentication > LDAP (only enabled when DEFAULT_AUTH_TYPE = LDAP)
    LDAP_DN(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "dc=example,dc=com"),
    LDAP_USER_SUFFIX(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "ou=users"),
    LDAP_USER_PREFIX(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "uid="),
    LDAP_SERVER_ADDRESS(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "127.0.0.1"),
    LDAP_SERVER_PORT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 389),
    LDAP_SERVER_TIMEOUT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 30),
    LDAP_SSL_ENABLED(GlobalSettingSubCategory.AUTHENTICATION, booleanType(), true),

    // Authentication > Active Directory (only enabled when DEFAULT_AUTH_TYPE = AD)
    AD_SERVER_ADDRESS(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "127.0.0.1"),
    AD_SERVER_PORT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 389),
    AD_SERVER_TIMEOUT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 30),
    AD_NTDOMAIN(GlobalSettingSubCategory.AUTHENTICATION, stringType(), null),
    AD_SSL_ENABLED(GlobalSettingSubCategory.AUTHENTICATION, booleanType(), true),

    CLIENT_APPLICATIONS_REMEMBER_ME(GlobalSettingSubCategory.AUTHENTICATION,
                                   InputTypeFactory.enumType(ClientApplicationRememberMe.class),
                                   ClientApplicationRememberMe.USERNAME),

    // Authentication > Password Management  (this could go under Authentication > General if we didn't want two sub sections with so few items)
    ENABLE_PASSWORD_RECOVERY(GlobalSettingSubCategory.AUTHENTICATION, booleanType(), true),
    ENABLE_CAPTCHAS(GlobalSettingSubCategory.AUTHENTICATION, booleanType(), true),
    // END Authentication

    // Calc Historical
    INTERVAL(GlobalSettingSubCategory.CALC_HISTORICAL, integerType(), 900),
    BASELINE_CALCTIME(GlobalSettingSubCategory.CALC_HISTORICAL, integerType(), 4),
    DAYS_PREVIOUS_TO_COLLECT(GlobalSettingSubCategory.CALC_HISTORICAL, integerType(), 30),

    // Multispeak
    MSP_PAONAME_ALIAS(GlobalSettingSubCategory.MULTISPEAK, InputTypeFactory.enumType(MspPaoNameAliasEnum.class), MspPaoNameAliasEnum.METER_NUMBER),
    MSP_PAONAME_EXTENSION(GlobalSettingSubCategory.MULTISPEAK, stringType(), null),
    MSP_PRIMARY_CB_VENDORID(GlobalSettingSubCategory.MULTISPEAK, integerType(), 0),
    MSP_BILLING_CYCLE_PARENT_DEVICEGROUP(GlobalSettingSubCategory.MULTISPEAK, stringType(), "/Meters/Billing/"),
    MSP_LM_MAPPING_SETUP(GlobalSettingSubCategory.MULTISPEAK, booleanType(), false),
    MSP_METER_LOOKUP_FIELD(GlobalSettingSubCategory.MULTISPEAK, InputTypeFactory.enumType(MultispeakMeterLookupFieldEnum.class), MultispeakMeterLookupFieldEnum.AUTO_METER_NUMBER_FIRST),
    MSP_EXCLUDE_DISABLED_METERS(GlobalSettingSubCategory.MULTISPEAK, booleanType(), false),

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
    JMS_BROKER_HOST(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "localhost"),
    JMS_BROKER_PORT(GlobalSettingSubCategory.YUKON_SERVICES, integerType(), 61616),
    SMTP_HOST(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), null),
    SMTP_PORT(GlobalSettingSubCategory.YUKON_SERVICES, integerType(), null),
    SMTP_USERNAME(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), null),
    SMTP_PASSWORD(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), null),
    MAIL_FROM_ADDRESS(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "yukon@cannontech.com"),
    NETWORK_MANAGER_ADDRESS(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "http://127.0.0.1:8081/nmclient/"),
    SMTP_TLS_ENABLED(GlobalSettingSubCategory.YUKON_SERVICES, booleanType(), false),

    // DR
    BATCHED_SWITCH_COMMAND_TOGGLE(GlobalSettingSubCategory.DR, stringType(), "auto"),
    STARS_PRELOAD_DATA(GlobalSettingSubCategory.DR, booleanType(), true),
    CUSTOMER_INFO_IMPORTER_FILE_LOCATION(GlobalSettingSubCategory.DR, stringType(), null),
    OPT_OUTS_COUNT(GlobalSettingSubCategory.DR, booleanType(), true),
    LAST_COMMUNICATION_HOURS(GlobalSettingSubCategory.DR, integerType(), 60),
    LAST_RUNTIME_HOURS(GlobalSettingSubCategory.DR, integerType(), 168),
    RF_BROADCAST_PERFORMANCE(GlobalSettingSubCategory.DR, InputTypeFactory.enumType(OnOff.class), OnOff.OFF),
    ECOBEE_USERNAME(GlobalSettingSubCategory.DR, stringType(), ""),
    ECOBEE_PASSWORD(GlobalSettingSubCategory.DR, stringType(), ""),
    ECOBEE_SERVER_URL(GlobalSettingSubCategory.DR, stringType(), "https://api.ecobee.com/1/"),

    // Web Server
    GOOGLE_ANALYTICS_ENABLED(GlobalSettingSubCategory.WEB_SERVER, booleanType(), true),
    GOOGLE_ANALYTICS_TRACKING_IDS(GlobalSettingSubCategory.WEB_SERVER, stringType(), null),
    WEB_LOGO_URL(GlobalSettingSubCategory.WEB_SERVER, stringType(), "CannonLogo.gif"),

    // Data Export (previously Billing)
    WIZ_ACTIVATE(GlobalSettingSubCategory.DATA_EXPORT, booleanType(), false),
    INPUT_FILE(GlobalSettingSubCategory.DATA_EXPORT, stringType(), "c:\\yukon\\client\\bin\\BillingIn.txt"),
    DEFAULT_BILLING_FORMAT(GlobalSettingSubCategory.DATA_EXPORT, stringType(), "CTI-CSV"),
    DEMAND_DAYS_PREVIOUS(GlobalSettingSubCategory.DATA_EXPORT, integerType(), 30),
    ENERGY_DAYS_PREVIOUS(GlobalSettingSubCategory.DATA_EXPORT, integerType(), 7),
    APPEND_TO_FILE(GlobalSettingSubCategory.DATA_EXPORT, booleanType(), false),
    REMOVE_MULTIPLIER(GlobalSettingSubCategory.DATA_EXPORT, booleanType(), false),
    COOP_ID_CADP_ONLY(GlobalSettingSubCategory.DATA_EXPORT, stringType(), null),
    DEFAULT_ROUNDING_MODE(GlobalSettingSubCategory.DATA_EXPORT, InputTypeFactory.enumType(RoundingMode.class), RoundingMode.HALF_EVEN),
    SCHEDULE_PARAMETERS_AVAILABLE_FILE_EXTENSIONS(GlobalSettingSubCategory.DATA_EXPORT, stringType(), ".csv,.dat,.txt"),
    // Defaulting to directory specified by CTIUtilities.getExportDirPath(), typically C:\Yukon\Client\Export.
    SCHEDULE_PARAMETERS_EXPORT_PATH(GlobalSettingSubCategory.DATA_EXPORT, stringType(), CtiUtilities.getExportDirPath()),
    HISTORY_CLEANUP_DAYS_TO_KEEP(GlobalSettingSubCategory.DATA_EXPORT, integerType(), 0),
    HISTORY_CLEANUP_FILES_TO_KEEP(GlobalSettingSubCategory.DATA_EXPORT, integerType(), 0),

    // AMI  (may fit better for future under Misc.?)
    DEVICE_DISPLAY_TEMPLATE(GlobalSettingSubCategory.AMI, InputTypeFactory.enumType(MeterDisplayFieldEnum.class), MeterDisplayFieldEnum.DEVICE_NAME),
    // This may eventually be a "Device" setting, just just "AMR", This is to disable "route lookup" during the OLD bulk importer process; reduces comms
    BULK_IMPORTER_COMMUNICATIONS_ENABLED(GlobalSettingSubCategory.AMI, booleanType(), true),

    // Misc.
    SYSTEM_TIMEZONE(GlobalSettingSubCategory.MISC, stringType(), null),
    ALERT_TIMEOUT_HOURS(GlobalSettingSubCategory.MISC, integerType(), 168),
    DATABASE_MIGRATION_FILE_LOCATION(GlobalSettingSubCategory.MISC, stringType(), "/Server/Export/"),
    TEMP_DEVICE_GROUP_DELETION_IN_DAYS(GlobalSettingSubCategory.MISC, integerType(), 7),
    HTTP_PROXY(GlobalSettingSubCategory.MISC, stringType(), "none"),
    CONTACT_EMAIL(GlobalSettingSubCategory.MISC, stringType(), "EAS-Support@Eaton.com"),
    CONTACT_PHONE(GlobalSettingSubCategory.MISC, stringType(), "1-800-815-2258"),
    SCHEDULED_REQUEST_MAX_RUN_HOURS(GlobalSettingSubCategory.MISC, integerType(), 23),

    // Voice Server
    CALL_RESPONSE_TIMEOUT(GlobalSettingSubCategory.VOICE, integerType(), 240),
    CALL_PREFIX(GlobalSettingSubCategory.VOICE, stringType(), null),

    // Web Graph
    HOME_DIRECTORY(GlobalSettingSubCategory.GRAPHING, stringType(), "c:\\yukon\\client\\webgraphs\\"),
    RUN_INTERVAL(GlobalSettingSubCategory.GRAPHING, integerType(), 900),
    ;

    private static final ImmutableSetMultimap<GlobalSettingSubCategory, GlobalSettingType> categoryMapping;
    private final InputType<?> type;
    private final  Object defaultValue;
    private final  GlobalSettingSubCategory category;

    static {
        final Builder<GlobalSettingSubCategory, GlobalSettingType> b = ImmutableSetMultimap.builder();
        for (GlobalSettingType globalSettingType : values()) {
            b.put(globalSettingType.getCategory(), globalSettingType);
        }
        categoryMapping = b.build();
    }

    private GlobalSettingType(GlobalSettingSubCategory category, InputType<?> type, Object defaultValue) {
        this.type = type;
        this.category = category;
        this.defaultValue = defaultValue;
    }

    public InputType<?> getType() {
        return type;
    }

    public boolean isSensitiveInformation() {
        return this == ECOBEE_PASSWORD || this == ECOBEE_USERNAME;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public GlobalSettingSubCategory getCategory() {
        return category;
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

}
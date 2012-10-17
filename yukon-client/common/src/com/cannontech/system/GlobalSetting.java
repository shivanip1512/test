package com.cannontech.system;

import static com.cannontech.core.roleproperties.InputTypeFactory.booleanType;
import static com.cannontech.core.roleproperties.InputTypeFactory.integerType;
import static com.cannontech.core.roleproperties.InputTypeFactory.stringType;

import java.math.RoundingMode;

import com.cannontech.amr.meter.dao.impl.MeterDisplayFieldEnum;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.web.input.type.InputType;

public enum GlobalSetting {

    // Authentication
    SERVER_ADDRESS(GlobalSettingCategory.AUTHENTICATION, stringType(), "127.0.0.1"),
    AUTH_PORT(GlobalSettingCategory.AUTHENTICATION, integerType(), 1812),
    ACCT_PORT(GlobalSettingCategory.AUTHENTICATION, integerType(), 1813),
    SECRET_KEY(GlobalSettingCategory.AUTHENTICATION, stringType(), "cti"),
    AUTH_METHOD(GlobalSettingCategory.AUTHENTICATION, stringType(), " "),
    AUTHENTICATION_MODE(GlobalSettingCategory.AUTHENTICATION, stringType(), "Yukon"),
    AUTH_TIMEOUT(GlobalSettingCategory.AUTHENTICATION, integerType(), 30),
    DEFAULT_AUTH_TYPE(GlobalSettingCategory.AUTHENTICATION, InputTypeFactory.enumType(AuthenticationCategory.class), AuthenticationCategory.ENCRYPTED),
    LDAP_DN(GlobalSettingCategory.AUTHENTICATION, stringType(), "dc=example,dc=com"),
    LDAP_USER_SUFFIX(GlobalSettingCategory.AUTHENTICATION, stringType(), "ou=users"),
    LDAP_USER_PREFIX(GlobalSettingCategory.AUTHENTICATION, stringType(), "uid="),
    LDAP_SERVER_ADDRESS(GlobalSettingCategory.AUTHENTICATION, stringType(), "127.0.0.1"),
    LDAP_SERVER_PORT(GlobalSettingCategory.AUTHENTICATION, integerType(), 389),
    LDAP_SERVER_TIMEOUT(GlobalSettingCategory.AUTHENTICATION, integerType(), 30),
    AD_SERVER_ADDRESS(GlobalSettingCategory.AUTHENTICATION, stringType(), "127.0.0.1"),
    AD_SERVER_PORT(GlobalSettingCategory.AUTHENTICATION, integerType(), 389),
    AD_SERVER_TIMEOUT(GlobalSettingCategory.AUTHENTICATION, integerType(), 30),
    AD_NTDOMAIN(GlobalSettingCategory.AUTHENTICATION, stringType(), " "),
    ENABLE_PASSWORD_RECOVERY(GlobalSettingCategory.AUTHENTICATION, booleanType(), true),
    ENABLE_CAPTCHAS(GlobalSettingCategory.AUTHENTICATION, booleanType(), true),
    
    // Calc Historical
    INTERVAL(GlobalSettingCategory.CALC_HISTORICAL, integerType(), 900),
    BASELINE_CALCTIME(GlobalSettingCategory.CALC_HISTORICAL, integerType(), 4),
    DAYS_PREVIOUS_TO_COLLECT(GlobalSettingCategory.CALC_HISTORICAL, integerType(), 30),
    
    // Multispeak
    MSP_PAONAME_ALIAS(GlobalSettingCategory.MULTISPEAK, InputTypeFactory.enumType(MspPaoNameAliasEnum.class), MspPaoNameAliasEnum.METER_NUMBER),
    MSP_PRIMARY_CB_VENDORID(GlobalSettingCategory.MULTISPEAK, integerType(), 0),
    MSP_BILLING_CYCLE_PARENT_DEVICEGROUP(GlobalSettingCategory.MULTISPEAK, stringType(), "/Meters/Billing/"),
    MSP_LM_MAPPING_SETUP(GlobalSettingCategory.MULTISPEAK, booleanType(), false),
    MSP_METER_LOOKUP_FIELD(GlobalSettingCategory.MULTISPEAK, InputTypeFactory.enumType(MultispeakMeterLookupFieldEnum.class), MultispeakMeterLookupFieldEnum.AUTO_METER_NUMBER_FIRST),
    MSP_PAONAME_EXTENSION(GlobalSettingCategory.MULTISPEAK, stringType(), " "),
    
    // Yukon Services
    DISPATCH_MACHINE(GlobalSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    DISPATCH_PORT(GlobalSettingCategory.YUKON_SERVICES, integerType(), 1510),
    PORTER_MACHINE(GlobalSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    PORTER_PORT(GlobalSettingCategory.YUKON_SERVICES, integerType(), 1540),
    MACS_MACHINE(GlobalSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    MACS_PORT(GlobalSettingCategory.YUKON_SERVICES, integerType(), 1900),
    CAP_CONTROL_MACHINE(GlobalSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    CAP_CONTROL_PORT(GlobalSettingCategory.YUKON_SERVICES, integerType(), 1910),
    LOADCONTROL_MACHINE(GlobalSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    LOADCONTROL_PORT(GlobalSettingCategory.YUKON_SERVICES, integerType(), 1920),
    SMTP_HOST(GlobalSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    MAIL_FROM_ADDRESS(GlobalSettingCategory.YUKON_SERVICES, stringType(), "yukon@cannontech.com"),
    NOTIFICATION_HOST(GlobalSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    NOTIFICATION_PORT(GlobalSettingCategory.YUKON_SERVICES, integerType(), 1515),
    
    // DR
    BATCHED_SWITCH_COMMAND_TOGGLE(GlobalSettingCategory.DR, stringType(), "auto"),
    STARS_PRELOAD_DATA(GlobalSettingCategory.DR, booleanType(), true),
    CUSTOMER_INFO_IMPORTER_FILE_LOCATION(GlobalSettingCategory.DR, stringType(), " "),
    OPT_OUTS_COUNT(GlobalSettingCategory.DR, booleanType(), true),
    
    // Web Server
    WEB_LOGO_URL(GlobalSettingCategory.WEB_SERVER, stringType(), "CannonLogo.gif"),
    GOOGLE_ANALYTICS_ENABLED(GlobalSettingCategory.WEB_SERVER, booleanType(), true),
    GOOGLE_ANALYTICS_TRACKING_IDS(GlobalSettingCategory.WEB_SERVER, stringType(), null),
    
    // Billing
    WIZ_ACTIVATE(GlobalSettingCategory.BILLING, booleanType(), false),
    INPUT_FILE(GlobalSettingCategory.BILLING, stringType(), "c:\\yukon\\client\\bin\\BillingIn.txt"),
    DEFAULT_BILLING_FORMAT(GlobalSettingCategory.BILLING, stringType(), "CTI-CSV"),
    DEMAND_DAYS_PREVIOUS(GlobalSettingCategory.BILLING, integerType(), 30),
    ENERGY_DAYS_PREVIOUS(GlobalSettingCategory.BILLING, integerType(), 7),
    APPEND_TO_FILE(GlobalSettingCategory.BILLING, booleanType(), false),
    REMOVE_MULTIPLIER(GlobalSettingCategory.BILLING, booleanType(), false),
    COOP_ID_CADP_ONLY(GlobalSettingCategory.BILLING, stringType(), " "), 
    DEFAULT_ROUNDING_MODE(GlobalSettingCategory.BILLING, InputTypeFactory.enumType(RoundingMode.class), RoundingMode.HALF_EVEN),
    
    // AMR
    DEVICE_DISPLAY_TEMPLATE(GlobalSettingCategory.AMI, InputTypeFactory.enumType(MeterDisplayFieldEnum.class), MeterDisplayFieldEnum.DEVICE_NAME),
    BULK_IMPORTER_COMMUNICATIONS_ENABLED(GlobalSettingCategory.AMI, booleanType(), true), // NOT SURE WHAT THIS IS
    
    // Misc.
    SYSTEM_TIMEZONE(GlobalSettingCategory.MISC, stringType(), " "),
    ALERT_TIMEOUT_HOURS(GlobalSettingCategory.MISC, integerType(), 168),
    DATABASE_MIGRATION_FILE_LOCATION(GlobalSettingCategory.MISC, stringType(), "/Server/Export/"),
    
    // Voice Server
    CALL_RESPONSE_TIMEOUT(GlobalSettingCategory.VOICE, integerType(), 240),
    CALL_PREFIX(GlobalSettingCategory.VOICE, stringType(), " "),
    
    // Web Graph
    HOME_DIRECTORY(GlobalSettingCategory.GRAPHING, stringType(), "c:\\yukon\\client\\webgraphs\\"),  
    RUN_INTERVAL(GlobalSettingCategory.GRAPHING, integerType(), 900),
    ;
    
    public enum GlobalSettingCategory {
        AUTHENTICATION, // lock
        BILLING, // bill
        CALC_HISTORICAL, // calculator
        MULTISPEAK, // random
        VOICE, // phone
        GRAPHING, // stat line
        DR, // home, light bulb, sun
        AMI, // * create a meter, see hard disc
        YUKON_SERVICES, // * create 2 gears
        WEB_SERVER, // monitor, add yukon log to monitor? or world
        MISC, // gear
        ;
    }
    
    private final InputType<?> type;
    private final Object defaultValue;
    private final GlobalSettingCategory category;
    private GlobalSetting(GlobalSettingCategory category, InputType<?> type, Object defaultValue) {
        this.type = type;
        this.category = category;
        this.defaultValue = defaultValue;
    }

    public InputType<?> getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
    
    public GlobalSettingCategory getCategory() {
        return category;
    }
    
}
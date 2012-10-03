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

public enum YukonSetting {

    // Authentication
    SERVER_ADDRESS(YukonSettingCategory.AUTHENTICATION, stringType(), "127.0.0.1"),
    AUTH_PORT(YukonSettingCategory.AUTHENTICATION, integerType(), 1812),
    ACCT_PORT(YukonSettingCategory.AUTHENTICATION, integerType(), 1813),
    SECRET_KEY(YukonSettingCategory.AUTHENTICATION, stringType(), "cti"),
    AUTH_METHOD(YukonSettingCategory.AUTHENTICATION, stringType(), " "),
    AUTHENTICATION_MODE(YukonSettingCategory.AUTHENTICATION, stringType(), "Yukon"),
    AUTH_TIMEOUT(YukonSettingCategory.AUTHENTICATION, integerType(), 30),
    DEFAULT_AUTH_TYPE(YukonSettingCategory.AUTHENTICATION, InputTypeFactory.enumType(AuthenticationCategory.class), AuthenticationCategory.ENCRYPTED),
    LDAP_DN(YukonSettingCategory.AUTHENTICATION, stringType(), "dc=example,dc=com"),
    LDAP_USER_SUFFIX(YukonSettingCategory.AUTHENTICATION, stringType(), "ou=users"),
    LDAP_USER_PREFIX(YukonSettingCategory.AUTHENTICATION, stringType(), "uid="),
    LDAP_SERVER_ADDRESS(YukonSettingCategory.AUTHENTICATION, stringType(), "127.0.0.1"),
    LDAP_SERVER_PORT(YukonSettingCategory.AUTHENTICATION, integerType(), 389),
    LDAP_SERVER_TIMEOUT(YukonSettingCategory.AUTHENTICATION, integerType(), 30),
    AD_SERVER_ADDRESS(YukonSettingCategory.AUTHENTICATION, stringType(), "127.0.0.1"),
    AD_SERVER_PORT(YukonSettingCategory.AUTHENTICATION, integerType(), 389),
    AD_SERVER_TIMEOUT(YukonSettingCategory.AUTHENTICATION, integerType(), 30),
    AD_NTDOMAIN(YukonSettingCategory.AUTHENTICATION, stringType(), " "),
    ENABLE_PASSWORD_RECOVERY(YukonSettingCategory.AUTHENTICATION, booleanType(), true),
    ENABLE_CAPTCHAS(YukonSettingCategory.AUTHENTICATION, booleanType(), true),
    
    // Calc Historical
    INTERVAL(YukonSettingCategory.CALC_HISTORICAL, integerType(), 900),
    BASELINE_CALCTIME(YukonSettingCategory.CALC_HISTORICAL, integerType(), 4),
    DAYS_PREVIOUS_TO_COLLECT(YukonSettingCategory.CALC_HISTORICAL, integerType(), 30),
    
    // Multispeak
    MSP_PAONAME_ALIAS(YukonSettingCategory.MULTISPEAK, InputTypeFactory.enumType(MspPaoNameAliasEnum.class), MspPaoNameAliasEnum.METER_NUMBER),
    MSP_PRIMARY_CB_VENDORID(YukonSettingCategory.MULTISPEAK, integerType(), 0),
    MSP_BILLING_CYCLE_PARENT_DEVICEGROUP(YukonSettingCategory.MULTISPEAK, stringType(), "/Meters/Billing/"),
    MSP_LM_MAPPING_SETUP(YukonSettingCategory.MULTISPEAK, booleanType(), false),
    MSP_METER_LOOKUP_FIELD(YukonSettingCategory.MULTISPEAK, InputTypeFactory.enumType(MultispeakMeterLookupFieldEnum.class), MultispeakMeterLookupFieldEnum.AUTO_METER_NUMBER_FIRST),
    MSP_PAONAME_EXTENSION(YukonSettingCategory.MULTISPEAK, stringType(), " "),
    
    // Yukon Services
    DISPATCH_MACHINE(YukonSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    DISPATCH_PORT(YukonSettingCategory.YUKON_SERVICES, integerType(), 1510),
    PORTER_MACHINE(YukonSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    PORTER_PORT(YukonSettingCategory.YUKON_SERVICES, integerType(), 1540),
    MACS_MACHINE(YukonSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    MACS_PORT(YukonSettingCategory.YUKON_SERVICES, integerType(), 1900),
    CAP_CONTROL_MACHINE(YukonSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    CAP_CONTROL_PORT(YukonSettingCategory.YUKON_SERVICES, integerType(), 1910),
    LOADCONTROL_MACHINE(YukonSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    LOADCONTROL_PORT(YukonSettingCategory.YUKON_SERVICES, integerType(), 1920),
    SMTP_HOST(YukonSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    MAIL_FROM_ADDRESS(YukonSettingCategory.YUKON_SERVICES, stringType(), "yukon@cannontech.com"),
    NOTIFICATION_HOST(YukonSettingCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    NOTIFICATION_PORT(YukonSettingCategory.YUKON_SERVICES, integerType(), 1515),
    
    // DR
    BATCHED_SWITCH_COMMAND_TOGGLE(YukonSettingCategory.DR, stringType(), "auto"),
    STARS_PRELOAD_DATA(YukonSettingCategory.DR, booleanType(), true),
    CUSTOMER_INFO_IMPORTER_FILE_LOCATION(YukonSettingCategory.DR, stringType(), " "),
    OPT_OUTS_COUNT(YukonSettingCategory.DR, booleanType(), true),
    
    // Web Server
    WEB_LOGO_URL(YukonSettingCategory.WEB_SERVER, stringType(), "CannonLogo.gif"),
    
    // Billing
    WIZ_ACTIVATE(YukonSettingCategory.BILLING, booleanType(), false),
    INPUT_FILE(YukonSettingCategory.BILLING, stringType(), "c:\\yukon\\client\\bin\\BillingIn.txt"),
    DEFAULT_BILLING_FORMAT(YukonSettingCategory.BILLING, stringType(), "CTI-CSV"),
    DEMAND_DAYS_PREVIOUS(YukonSettingCategory.BILLING, integerType(), 30),
    ENERGY_DAYS_PREVIOUS(YukonSettingCategory.BILLING, integerType(), 7),
    APPEND_TO_FILE(YukonSettingCategory.BILLING, booleanType(), false),
    REMOVE_MULTIPLIER(YukonSettingCategory.BILLING, booleanType(), false),
    COOP_ID_CADP_ONLY(YukonSettingCategory.BILLING, stringType(), " "), 
    DEFAULT_ROUNDING_MODE(YukonSettingCategory.BILLING, InputTypeFactory.enumType(RoundingMode.class), RoundingMode.HALF_EVEN),
    
    // AMR
    DEVICE_DISPLAY_TEMPLATE(YukonSettingCategory.AMI, InputTypeFactory.enumType(MeterDisplayFieldEnum.class), MeterDisplayFieldEnum.DEVICE_NAME),
    BULK_IMPORTER_COMMUNICATIONS_ENABLED(YukonSettingCategory.AMI, booleanType(), true), // NOT SURE WHAT THIS IS
    
    // Misc.
    SYSTEM_TIMEZONE(YukonSettingCategory.MISC, stringType(), " "),
    ALERT_TIMEOUT_HOURS(YukonSettingCategory.MISC, integerType(), 168),
    DATABASE_MIGRATION_FILE_LOCATION(YukonSettingCategory.MISC, stringType(), "/Server/Export/"),
    
    // Voice Server
    CALL_RESPONSE_TIMEOUT(YukonSettingCategory.VOICE, integerType(), 240),
    CALL_PREFIX(YukonSettingCategory.VOICE, stringType(), " "),
    
    // Web Graph
    HOME_DIRECTORY(YukonSettingCategory.GRAPHING, stringType(), "c:\\yukon\\client\\webgraphs\\"),  
    RUN_INTERVAL(YukonSettingCategory.GRAPHING, integerType(), 900),
    ;
    
    public enum YukonSettingCategory {
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
    private Object defaultValue;
    private YukonSettingCategory category;
    private YukonSetting(YukonSettingCategory category, InputType<?> type, Object defaultValue) {
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
    
    public YukonSettingCategory getCategory() {
        return category;
    }
    
}
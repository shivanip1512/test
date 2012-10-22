package com.cannontech.system;

import static com.cannontech.core.roleproperties.InputTypeFactory.booleanType;
import static com.cannontech.core.roleproperties.InputTypeFactory.integerType;
import static com.cannontech.core.roleproperties.InputTypeFactory.stringType;

import java.math.RoundingMode;
import java.util.Set;

import com.cannontech.amr.meter.dao.impl.MeterDisplayFieldEnum;
import com.cannontech.common.i18n.DisplayableEnum;
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
    
    // Authentication > Active Directory (only enabled when DEFAULT_AUTH_TYPE = AD)
    AD_SERVER_ADDRESS(GlobalSettingSubCategory.AUTHENTICATION, stringType(), "127.0.0.1"),
    AD_SERVER_PORT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 389),
    AD_SERVER_TIMEOUT(GlobalSettingSubCategory.AUTHENTICATION, integerType(), 30),
    AD_NTDOMAIN(GlobalSettingSubCategory.AUTHENTICATION, stringType(), null),
    
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
    
    // Yukon Services
    DISPATCH_MACHINE(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    DISPATCH_PORT(GlobalSettingSubCategory.YUKON_SERVICES, integerType(), 1510),
    PORTER_MACHINE(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    PORTER_PORT(GlobalSettingSubCategory.YUKON_SERVICES, integerType(), 1540),
    MACS_MACHINE(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    MACS_PORT(GlobalSettingSubCategory.YUKON_SERVICES, integerType(), 1900),
    CAP_CONTROL_MACHINE(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    CAP_CONTROL_PORT(GlobalSettingSubCategory.YUKON_SERVICES, integerType(), 1910),
    LOADCONTROL_MACHINE(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    LOADCONTROL_PORT(GlobalSettingSubCategory.YUKON_SERVICES, integerType(), 1920),
    SMTP_HOST(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    MAIL_FROM_ADDRESS(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "yukon@cannontech.com"),
    NOTIFICATION_HOST(GlobalSettingSubCategory.YUKON_SERVICES, stringType(), "127.0.0.1"),
    NOTIFICATION_PORT(GlobalSettingSubCategory.YUKON_SERVICES, integerType(), 1515),
    
    // DR
    BATCHED_SWITCH_COMMAND_TOGGLE(GlobalSettingSubCategory.DR, stringType(), "auto"),
    STARS_PRELOAD_DATA(GlobalSettingSubCategory.DR, booleanType(), true),
    CUSTOMER_INFO_IMPORTER_FILE_LOCATION(GlobalSettingSubCategory.DR, stringType(), null),
    OPT_OUTS_COUNT(GlobalSettingSubCategory.DR, booleanType(), true),
    
    // Web Server
    GOOGLE_ANALYTICS_ENABLED(GlobalSettingSubCategory.WEB_SERVER, booleanType(), true),
    GOOGLE_ANALYTICS_TRACKING_IDS(GlobalSettingSubCategory.WEB_SERVER, stringType(), null),
    WEB_LOGO_URL(GlobalSettingSubCategory.WEB_SERVER, stringType(), "CannonLogo.gif"),
    
    // Billing
    WIZ_ACTIVATE(GlobalSettingSubCategory.BILLING, booleanType(), false),
    INPUT_FILE(GlobalSettingSubCategory.BILLING, stringType(), "c:\\yukon\\client\\bin\\BillingIn.txt"),
    DEFAULT_BILLING_FORMAT(GlobalSettingSubCategory.BILLING, stringType(), "CTI-CSV"),
    DEMAND_DAYS_PREVIOUS(GlobalSettingSubCategory.BILLING, integerType(), 30),
    ENERGY_DAYS_PREVIOUS(GlobalSettingSubCategory.BILLING, integerType(), 7),
    APPEND_TO_FILE(GlobalSettingSubCategory.BILLING, booleanType(), false),
    REMOVE_MULTIPLIER(GlobalSettingSubCategory.BILLING, booleanType(), false),
    COOP_ID_CADP_ONLY(GlobalSettingSubCategory.BILLING, stringType(), null), 
    DEFAULT_ROUNDING_MODE(GlobalSettingSubCategory.BILLING, InputTypeFactory.enumType(RoundingMode.class), RoundingMode.HALF_EVEN),
    
    // AMI  (may fit better for future under Misc.?)
    DEVICE_DISPLAY_TEMPLATE(GlobalSettingSubCategory.AMI, InputTypeFactory.enumType(MeterDisplayFieldEnum.class), MeterDisplayFieldEnum.DEVICE_NAME),
    // This may eventually be a "Device" setting, just just "AMR", This is to disable "route lookup" during the OLD bulk importer process; reduces comms
    BULK_IMPORTER_COMMUNICATIONS_ENABLED(GlobalSettingSubCategory.AMI, booleanType(), true),
    
    // Misc.
    SYSTEM_TIMEZONE(GlobalSettingSubCategory.MISC, stringType(), null),
    ALERT_TIMEOUT_HOURS(GlobalSettingSubCategory.MISC, integerType(), 168),
    DATABASE_MIGRATION_FILE_LOCATION(GlobalSettingSubCategory.MISC, stringType(), "/Server/Export/"),
    
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
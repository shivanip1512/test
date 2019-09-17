package com.cannontech.core.roleproperties;


import static com.cannontech.core.roleproperties.InputTypeFactory.*;
import static com.cannontech.core.roleproperties.YukonRole.*;
import static com.cannontech.core.roleproperties.HierarchyPermissionLevel.*;
import static com.google.common.base.Preconditions.*;


import com.cannontech.web.input.type.InputType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Enumeration of user group roles and permissions.
 * Note: Yukon Grp associated role properties moved to GlobalSettings enum.
 */
public enum YukonRoleProperty {
    
    DYNAMIC_BILLING_FILE_SETUP(APPLICATION_BILLING, APPLICATION_BILLING.getBasePropertyId(), booleanType()),
    VERSACOM_SERIAL_MODEL(COMMANDER, COMMANDER.getBasePropertyId() - 1, booleanType()),
    EXPRESSCOM_SERIAL_MODEL(COMMANDER, COMMANDER.getBasePropertyId() - 2, booleanType()),
    DCU_SA205_SERIAL_MODEL(COMMANDER, COMMANDER.getBasePropertyId() - 3, booleanType()),
    DCU_SA305_SERIAL_MODEL(COMMANDER, COMMANDER.getBasePropertyId() - 4, booleanType()),
    COMMANDS_GROUP(COMMANDER, COMMANDER.getBasePropertyId() - 5, stringType()),
    READ_DEVICE(COMMANDER, COMMANDER.getBasePropertyId() - 6, booleanType()),
    WRITE_TO_DEVICE(COMMANDER, COMMANDER.getBasePropertyId() - 7, booleanType()),
    CONTROL_DEVICE(COMMANDER, COMMANDER.getBasePropertyId() - 9, booleanType()),
    READ_LM_DEVICE(COMMANDER, COMMANDER.getBasePropertyId() - 10, booleanType()),
    WRITE_TO_LM_DEVICE(COMMANDER, COMMANDER.getBasePropertyId() - 11, booleanType()),
    CONTROL_LM_DEVICE(COMMANDER, COMMANDER.getBasePropertyId() - 12, booleanType()),
    READ_CAP_CONTROL_DEVICE(COMMANDER, COMMANDER.getBasePropertyId() - 13, booleanType()),
    WRITE_TO_CAP_CONTROL_DEVICE(COMMANDER, COMMANDER.getBasePropertyId() - 14, booleanType()),
    CONTROL_CAP_CONTROL_DEVICE(COMMANDER, COMMANDER.getBasePropertyId() - 15, booleanType()),
    EXECUTE_UNKNOWN_COMMAND(COMMANDER, COMMANDER.getBasePropertyId() - 16, booleanType()),
    EXECUTE_MANUAL_COMMAND(COMMANDER, COMMANDER.getBasePropertyId() - 17, booleanType()),
    ENABLE_WEB_COMMANDER(COMMANDER, COMMANDER.getBasePropertyId() - 18, booleanType()),
    ENABLE_CLIENT_COMMANDER(COMMANDER, COMMANDER.getBasePropertyId() - 19, booleanType()),
    
    DBEDITOR_LM(DATABASE_EDITOR, DATABASE_EDITOR.getBasePropertyId() - 2, booleanType()),
    DBEDITOR_SYSTEM(DATABASE_EDITOR, DATABASE_EDITOR.getBasePropertyId() - 4, booleanType()),
    UTILITY_ID_RANGE(DATABASE_EDITOR, DATABASE_EDITOR.getBasePropertyId() - 5, stringType()),
    TRANS_EXCLUSION(DATABASE_EDITOR, DATABASE_EDITOR.getBasePropertyId() - 7, booleanType()),
    PERMIT_LOGIN_EDIT(DATABASE_EDITOR, DATABASE_EDITOR.getBasePropertyId() - 8, booleanType()),
    DATABASE_EDITOR_OPTIONAL_PRODUCT_DEV(DATABASE_EDITOR, DATABASE_EDITOR.getBasePropertyId() - 10, stringType()),
    ALLOW_MEMBER_PROGRAMS(DATABASE_EDITOR, DATABASE_EDITOR.getBasePropertyId() - 11, booleanType()),
    
    LOCKOUT_DURATION(PASSWORD_POLICY, PASSWORD_POLICY.getBasePropertyId() - 6, integerType()),
    LOCKOUT_THRESHOLD(PASSWORD_POLICY, PASSWORD_POLICY.getBasePropertyId() - 5, integerType()),
    MAXIMUM_PASSWORD_AGE(PASSWORD_POLICY, PASSWORD_POLICY.getBasePropertyId() - 4, integerType()),
    MINIMUM_PASSWORD_AGE(PASSWORD_POLICY, PASSWORD_POLICY.getBasePropertyId() - 3, integerType()),
    MINIMUM_PASSWORD_LENGTH(PASSWORD_POLICY, PASSWORD_POLICY.getBasePropertyId() - 2, integerType()),
    PASSWORD_HISTORY(PASSWORD_POLICY, PASSWORD_POLICY.getBasePropertyId() -1, integerType()),
    
    POLICY_QUALITY_CHECK(PASSWORD_POLICY, PASSWORD_POLICY.getBasePropertyId() - 50, integerType()),
    POLICY_RULE_UPPERCASE_CHARACTERS(PASSWORD_POLICY, PASSWORD_POLICY.getBasePropertyId() - 51, booleanType()),
    POLICY_RULE_LOWERCASE_CHARACTERS(PASSWORD_POLICY, PASSWORD_POLICY.getBasePropertyId() - 52, booleanType()),
    POLICY_RULE_BASE_10_DIGITS(PASSWORD_POLICY, PASSWORD_POLICY.getBasePropertyId() - 53, booleanType()),
    POLICY_RULE_NONALPHANUMERIC_CHARACTERS(PASSWORD_POLICY, PASSWORD_POLICY.getBasePropertyId() - 54, booleanType()),
    
    ADMIN_REPORTS_GROUP(REPORTING, REPORTING.getBasePropertyId() - 3, booleanType()),
    AMR_REPORTS_GROUP(REPORTING, REPORTING.getBasePropertyId() - 4, booleanType()),
    STATISTICAL_REPORTS_GROUP(REPORTING, REPORTING.getBasePropertyId() - 5, booleanType()),
    LOAD_MANAGEMENT_REPORTS_GROUP(REPORTING, REPORTING.getBasePropertyId() - 6, booleanType()),
    CAP_CONTROL_REPORTS_GROUP(REPORTING, REPORTING.getBasePropertyId() - 7, booleanType()),
    DATABASE_REPORTS_GROUP(REPORTING, REPORTING.getBasePropertyId() - 8, booleanType()),
    STARS_REPORTS_GROUP(REPORTING, REPORTING.getBasePropertyId() - 9, booleanType()),
    CI_CURTAILMENT_REPORTS_GROUP(REPORTING, REPORTING.getBasePropertyId() - 23, booleanType()),
    
    MACS_EDIT(TABULAR_DISPLAY_CONSOLE, TABULAR_DISPLAY_CONSOLE.getBasePropertyId() - 1, stringType()),
    TDC_MAX_ROWS(TABULAR_DISPLAY_CONSOLE, TABULAR_DISPLAY_CONSOLE.getBasePropertyId() - 3, integerType()),
    TDC_RIGHTS(TABULAR_DISPLAY_CONSOLE, TABULAR_DISPLAY_CONSOLE.getBasePropertyId() -4, stringType()),
    TDC_ALARM_COUNT(TABULAR_DISPLAY_CONSOLE, TABULAR_DISPLAY_CONSOLE.getBasePropertyId() - 7, integerType()),
    DECIMAL_PLACES(TABULAR_DISPLAY_CONSOLE, TABULAR_DISPLAY_CONSOLE.getBasePropertyId() - 8, integerType()),
    LC_REDUCTION_COL(TABULAR_DISPLAY_CONSOLE, TABULAR_DISPLAY_CONSOLE.getBasePropertyId() - 11, booleanType()),
    
    GRAPH_EDIT_GRAPHDEFINITION(TRENDING, TRENDING.getBasePropertyId(), booleanType()),
    TRENDING_DISCLAIMER(TRENDING, TRENDING.getBasePropertyId() - 2, booleanType()),
    SCAN_NOW_ENABLED(TRENDING, TRENDING.getBasePropertyId() - 3, booleanType()),
    MINIMUM_SCAN_FREQUENCY(TRENDING, TRENDING.getBasePropertyId() - 5, integerType()),
    MAXIMUM_DAILY_SCANS(TRENDING, TRENDING.getBasePropertyId() - 6, integerType()),
    
    HOME_URL(WEB_CLIENT, WEB_CLIENT.getBasePropertyId(), stringType()),
    STYLE_SHEET(WEB_CLIENT, WEB_CLIENT.getBasePropertyId() - 2, stringType()),
    HEADER_LOGO(WEB_CLIENT, WEB_CLIENT.getBasePropertyId() - 5, stringType()),
    LOG_IN_URL(WEB_CLIENT, WEB_CLIENT.getBasePropertyId() - 6, stringType()),
    JAVA_WEB_START_LAUNCHER_ENABLED(WEB_CLIENT, WEB_CLIENT.getBasePropertyId() - 12, booleanType()),
    SUPPRESS_ERROR_PAGE_DETAILS(WEB_CLIENT, WEB_CLIENT.getBasePropertyId() - 14, booleanType()),
    DATA_UPDATER_DELAY_MS(WEB_CLIENT, WEB_CLIENT.getBasePropertyId() - 15,integerType()),
    STD_PAGE_STYLE_SHEET(WEB_CLIENT, WEB_CLIENT.getBasePropertyId() - 16, stringType()),
    THEME_NAME(WEB_CLIENT, WEB_CLIENT.getBasePropertyId() - 17, stringType()),
    VIEW_ALARMS_AS_ALERTS(WEB_CLIENT, WEB_CLIENT.getBasePropertyId() - 18, booleanType()),
    DEFAULT_TIMEZONE(WEB_CLIENT, WEB_CLIENT.getBasePropertyId() - 19, stringType()),
    SESSION_TIMEOUT(WEB_CLIENT, WEB_CLIENT.getBasePropertyId() - 20, integerType()),
   
     
    CAP_BANK_FIXED_TEXT(CBC_ONELINE_CAP_SETTINGS, CBC_ONELINE_CAP_SETTINGS.getBasePropertyId() - 5, stringType()),
    
    CAP_CONTROL_ACCESS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId(), booleanType()),
    HIDE_REPORTS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 2, booleanType()),
    HIDE_GRAPHS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 3, booleanType()),
    PFACTOR_DECIMAL_PLACES(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 7, integerType()),
    CBC_ALLOW_OVUV(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 8, booleanType()),
    CBC_DATABASE_EDIT(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 10, booleanType()),
    SHOW_FLIP_COMMAND(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 11, booleanType()),
    SHOW_CB_ADDINFO(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 12, booleanType()),
    AVAILABLE_DEFINITION(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 13, stringType()),
    UNAVAILABLE_DEFINITION(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 14, stringType()),
    TRIPPED_DEFINITION(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 15, stringType()),
    CLOSED_DEFINITION(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 16, stringType()),
    ADD_COMMENTS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 17, booleanType()),
    MODIFY_COMMENTS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 18, booleanType()),
    SYSTEM_WIDE_CONTROLS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 19, booleanType()),
    FORCE_COMMENTS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 20, booleanType()),
    AREA_COMMANDS_AND_ACTIONS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 21, InputTypeFactory.enumType(CapControlCommandsAccessLevel.class)),
    SUBSTATION_COMMANDS_AND_ACTIONS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 22, InputTypeFactory.enumType(CapControlCommandsAccessLevel.class)),
    SUBBUS_COMMANDS_AND_ACTIONS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 23, InputTypeFactory.enumType(CapControlCommandsAccessLevel.class)),
    FEEDER_COMMANDS_AND_ACTIONS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 24, InputTypeFactory.enumType(CapControlCommandsAccessLevel.class)),
    CAPBANK_COMMANDS_AND_ACTIONS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 25, InputTypeFactory.enumType(CapControlCommandsAccessLevel.class)),
    CONTROL_CONFIRMATION_POPUPS(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 26, InputTypeFactory.enumType(CapControlConfirmationPopups.class)),
    CAP_CONTROL_IMPORTER(CBC_SETTINGS, CBC_SETTINGS.getBasePropertyId() - 27, booleanType()),
    
    RESIDENTIAL_CONSUMER_INFO_ACCOUNT_GENERAL(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 1, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 3, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_PROGRAMS_ENROLLMENT(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 4, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_PROGRAMS_OPT_OUT(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 5, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_HARDWARES_THERMOSTAT(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 6, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_QUESTIONS_UTIL(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 7, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_QUESTIONS_FAQ(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 8, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_USERNAME(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 9, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_THERMOSTATS_ALL(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 10, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_PASSWORD(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 11, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_GROUPED_CONTROL_HISTORY_DISPLAY(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 12, booleanType()),
    RESIDENTIAL_OPT_OUT_PERIOD(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 55, stringType()),
    RESIDENTIAL_OPT_OUT_LIMITS(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 56, stringType()),
    RESIDENTIAL_WEB_LINK_FAQ(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 100, stringType()),
    RESIDENTIAL_CONTACTS_ACCESS(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 197, booleanType()),
    RESIDENTIAL_OPT_OUT_TODAY_ONLY(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 198, booleanType()),
    RESIDENTIAL_SIGN_OUT_ENABLED(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 199, booleanType()),
    RESIDENTIAL_OPT_OUT_ALL_DEVICES(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 201, booleanType()),
    RESIDENTIAL_ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 202, booleanType()),
    RESIDENTIAL_ENROLLMENT_PER_DEVICE(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 203, booleanType()),
    RESIDENTIAL_AUTO_THERMOSTAT_MODE_ENABLED(RESIDENTIAL_CUSTOMER, RESIDENTIAL_CUSTOMER.getBasePropertyId() - 300, booleanType()),
    
    ALLOW_CHECK_CONSTRAINTS(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 4, booleanType()),
    ALLOW_OBSERVE_CONSTRAINTS(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 5, booleanType()),
    ALLOW_OVERRIDE_CONSTRAINT(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 6, booleanType()),
    DEFAULT_CONSTRAINT_SELECTION(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 7, stringType()),
    ALLOW_STOP_GEAR_ACCESS(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 8, booleanType()),
    USE_PAO_PERMISSIONS(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 9, booleanType()),
    SHOW_CONTROL_AREAS(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 10, booleanType()),
    SHOW_SCENARIOS(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 11, booleanType()),
    START_NOW_CHECKED_BY_DEFAULT(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 39, booleanType()),
    CONTROL_DURATION_DEFAULT(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 40, integerType()),
    SCHEDULE_STOP_CHECKED_BY_DEFAULT(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 41, booleanType()),
    START_TIME_DEFAULT(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 42, stringType()),
    ALLOW_DR_CONTROL(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 43, booleanType()),
    SHOW_ASSET_AVAILABILITY(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 44, booleanType()),
    SHOW_ECOBEE(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 46, booleanType()),
    ALLOW_CHANGE_GEARS(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 48, booleanType()),
    ENABLE_DISABLE_PROGRAM(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 47, booleanType()),
    DR_SETUP_PERMISSION(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 49, InputTypeFactory.permissionType(OWNER, CREATE, UPDATE, RESTRICTED, NO_ACCESS)),
    
    ALLOW_LOAD_GROUP_CONTROL(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 45, booleanType()),
    DR_VIEW_CONTROL_AREA_TRIGGER_INFO(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 21, booleanType()),
    DR_VIEW_PRIORITY(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 24, booleanType()),    
    DR_VIEW_REDUCTION(DEMAND_RESPONSE, DEMAND_RESPONSE.getBasePropertyId() - 32, booleanType()),
    
    
    NUMBER_OF_CHANNELS(IVR, IVR.getBasePropertyId() - 1, integerType()),
    IVR_URL_DIALER_TEMPLATE(IVR, IVR.getBasePropertyId() - 4, stringType()),
    IVR_URL_DIALER_SUCCESS_MATCHER(IVR, IVR.getBasePropertyId() - 5, stringType()),
    
    TEMPLATE_ROOT(NOTIFICATION_CONFIGURATION, NOTIFICATION_CONFIGURATION.getBasePropertyId(), stringType()),
    
    ADMIN_NM_ACCESS(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 20, booleanType()),
    ADMIN_SUPER_USER(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 19, booleanType()),
    ADMIN_EDIT_ENERGY_COMPANY(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId(), booleanType()),    
    ADMIN_MANAGE_MEMBERS(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 3, booleanType()),
    ADMIN_VIEW_BATCH_COMMANDS(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 4, booleanType()),
    ADMIN_VIEW_OPT_OUT_EVENTS(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 5, booleanType()),
    ADMIN_MEMBER_LOGIN_CNTRL(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 6, booleanType()),
    ADMIN_MEMBER_ROUTE_SELECT(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 7, booleanType()),
    
    ADMIN_MULTI_WAREHOUSE(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 9, booleanType()),
    ADMIN_MULTISPEAK_SETUP(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 11, booleanType()),
    ADMIN_LM_USER_ASSIGN(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 12, booleanType()),
    ADMIN_EDIT_CONFIG(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 13, booleanType()),
    ADMIN_VIEW_CONFIG(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 14, booleanType()),
    ADMIN_MANAGE_INDEXES(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 15, booleanType()),
    ADMIN_VIEW_LOGS(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 16, booleanType()),
    ADMIN_DATABASE_MIGRATION(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 17, booleanType()),
    ADMIN_EVENT_LOGS(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 18, booleanType()),
    ADMIN_MANAGE_DASHBOARDS(OPERATOR_ADMINISTRATOR, OPERATOR_ADMINISTRATOR.getBasePropertyId() - 21, booleanType()),
    
    CURTAILMENT_IS_OPERATOR(CI_CURTAILMENT, CI_CURTAILMENT.getBasePropertyId(), booleanType()),
    
    OPERATOR_CONSUMER_INFO_ACCOUNT_RESIDENCE(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 2, booleanType()),
    OPERATOR_CONSUMER_INFO_ACCOUNT_CALL_TRACKING(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 3, booleanType()),
    OPERATOR_CONSUMER_INFO_METERING_INTERVAL_DATA(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 4, booleanType()),
    OPERATOR_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 6, booleanType()),
    OPERATOR_CONSUMER_INFO_PROGRAMS_ENROLLMENT(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 7, booleanType()),
    OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 8, booleanType()),
    OPERATOR_CONSUMER_INFO_APPLIANCES(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 9, booleanType()),
    OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 10, booleanType()),
    OPERATOR_CONSUMER_INFO_HARDWARES(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 11, booleanType()),
    OPERATOR_CONSUMER_INFO_HARDWARES_CREATE(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 12, booleanType()),
    OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 13, booleanType()),
    OPERATOR_CONSUMER_INFO_WORK_ORDERS(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 14, booleanType()),
    OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 20, booleanType()),
    OPERATOR_CONSUMER_INFO_WS_LM_CONTROL_ACCESS(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 21, booleanType()),
    OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 15, booleanType()),
    OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 19, booleanType()),
    OPERATOR_CONSUMER_INFO_ADMIN_FAQ(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 16, booleanType()),
    OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 17, booleanType()),
    OPERATOR_CONSUMER_INFO_METERING_CREATE(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 18, booleanType()),
    OPERATOR_NEW_ACCOUNT_WIZARD(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 51, booleanType()),
    OPERATOR_IMPORT_CUSTOMER_ACCOUNT(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 52, booleanType()),
    OPERATOR_OPT_OUT_PERIOD(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 57, stringType()),
    OPERATOR_DISABLE_SWITCH_SENDING(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 58, booleanType()),
    OPERATOR_ALLOW_ACCOUNT_EDITING(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 63, booleanType()),
    
    OPERATOR_OPT_OUT_TODAY_ONLY(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId(1) - 94, booleanType()),
    OPERATOR_OPT_OUT_ADMIN_STATUS(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId(1) - 95, booleanType()),
    OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId(1) - 96, booleanType()),
    OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId(1) - 97, booleanType()),
    OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId(1) - 98, booleanType()),
    
    // NOTICE: Property IDs for CONSUMER_INFO were split into 2 spaces. Note the (1) in getBasePropertyId.
    
    OPERATOR_ACCOUNT_SEARCH(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 65, booleanType()),
    OPERATOR_SURVEY_EDIT(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 66, booleanType()),
    OPERATOR_OPT_OUT_SURVEY_EDIT(CONSUMER_INFO, CONSUMER_INFO.getBasePropertyId() - 67, booleanType()),
    
    BULK_IMPORT_OPERATION(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId(), booleanType()),
    BULK_UPDATE_OPERATION(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 1, booleanType()),
    DEVICE_GROUP_EDIT(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 2, booleanType()),
    DEVICE_GROUP_MODIFY(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 3, booleanType()),
    GROUP_COMMANDER(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 4, booleanType()),
    MASS_CHANGE(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 5, booleanType()),
    LOCATE_ROUTE(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 6, booleanType()),
    MASS_DELETE(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 7, booleanType()),
    ADD_REMOVE_POINTS(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 8, booleanType()),
    ASSIGN_CONFIG(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 10, booleanType()),
    SEND_READ_CONFIG(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 9, booleanType()),
    ARCHIVED_DATA_ANALYSIS(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 11, booleanType()),   
    FDR_TRANSLATION_MANAGER(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 12, booleanType()),
    ARCHIVED_DATA_EXPORT(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 13, booleanType()),
    GROUP_DISCONNECT_CONTROL(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 14, booleanType()),  
    GROUP_DEMAND_RESET(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 15, booleanType()),  
    RF_DATA_STREAMING(DEVICE_ACTIONS, DEVICE_ACTIONS.getBasePropertyId() - 16, booleanType()),
    SN_ADD_RANGE(INVENTORY, INVENTORY.getBasePropertyId() - 1, booleanType()),
    SN_UPDATE_RANGE(INVENTORY, INVENTORY.getBasePropertyId() - 2, booleanType()),
    SN_DELETE_RANGE(INVENTORY, INVENTORY.getBasePropertyId() - 4, booleanType()),
    INVENTORY_CREATE_HARDWARE(INVENTORY, INVENTORY.getBasePropertyId() - 5, booleanType()),
    EXPRESSCOM_TOOS_RESTORE_FIRST(INVENTORY, INVENTORY.getBasePropertyId() - 6, booleanType()),
    PURCHASING_ACCESS(INVENTORY, INVENTORY.getBasePropertyId() - 9, booleanType()),
    DEVICE_RECONFIG(INVENTORY, INVENTORY.getBasePropertyId() - 10, booleanType()),
    INVENTORY_SEARCH(INVENTORY, INVENTORY.getBasePropertyId() - 11, booleanType()),
    
    IMPORTER_ENABLED(METERING, METERING.getBasePropertyId() - 3, booleanType()),
    PROFILE_COLLECTION(METERING, METERING.getBasePropertyId() - 6, booleanType()),
    MOVE_IN_MOVE_OUT_AUTO_ARCHIVING(METERING, METERING.getBasePropertyId() - 7, booleanType()),
    MOVE_IN_MOVE_OUT(METERING, METERING.getBasePropertyId() - 8, booleanType()),
    PROFILE_COLLECTION_SCANNING(METERING, METERING.getBasePropertyId() - 9, booleanType()),
    HIGH_BILL_COMPLAINT(METERING, METERING.getBasePropertyId() - 10, booleanType()),
    OUTAGE_PROCESSING(METERING, METERING.getBasePropertyId() - 13, booleanType()),
    TAMPER_FLAG_PROCESSING(METERING, METERING.getBasePropertyId() - 14, booleanType()),
    PHASE_DETECT(METERING, METERING.getBasePropertyId() - 15, booleanType()),
    VALIDATION_ENGINE(METERING, METERING.getBasePropertyId() - 16, booleanType()),
    STATUS_POINT_MONITORING(METERING, METERING.getBasePropertyId() - 17, booleanType()),
    PORTER_RESPONSE_MONITORING(METERING, METERING.getBasePropertyId() - 18, booleanType()),
    METER_EVENTS(METERING, METERING.getBasePropertyId() - 19, booleanType()),
    ALLOW_DISCONNECT_CONTROL(METERING, METERING.getBasePropertyId() - 20, booleanType()),
    DEVICE_DATA_MONITORING(METERING, METERING.getBasePropertyId() - 21, booleanType()),
    WATER_LEAK_REPORT(METERING, METERING.getBasePropertyId() - 22, booleanType()),
    USAGE_THRESHOLD_REPORT(METERING, METERING.getBasePropertyId() - 23, booleanType()),

    ODDS_FOR_CONTROL_ROLE_EXISTS(ODDS_FOR_CONTROL, ODDS_FOR_CONTROL.getBasePropertyId(), booleanType()),
    
    MACS_SCRIPTS(SCHEDULER, SCHEDULER.getBasePropertyId(), InputTypeFactory.enumType(HierarchyPermissionLevel.class)),
    MANAGE_SCHEDULES(SCHEDULER, SCHEDULER.getBasePropertyId() - 1, booleanType()),
    
    WORK_ORDER_SHOW_ALL(WORK_ORDER, WORK_ORDER.getBasePropertyId(), booleanType()),
    WORK_ORDER_CREATE_NEW(WORK_ORDER, WORK_ORDER.getBasePropertyId() - 1, booleanType()),
    WORK_ORDER_REPORT(WORK_ORDER, WORK_ORDER.getBasePropertyId() - 2, booleanType()),
    ADDTL_ORDER_NUMBER_LABEL(WORK_ORDER, WORK_ORDER.getBasePropertyId() - 3, stringType()),
    
    INFRASTRUCTURE_CREATE_AND_UPDATE(DEVICE_MANAGEMENT, DEVICE_MANAGEMENT.getBasePropertyId(), booleanType()),
    INFRASTRUCTURE_DELETE(DEVICE_MANAGEMENT, DEVICE_MANAGEMENT.getBasePropertyId() - 1, booleanType()),
    INFRASTRUCTURE_ADMIN(DEVICE_MANAGEMENT, DEVICE_MANAGEMENT.getBasePropertyId() - 2, booleanType()),
    INFRASTRUCTURE_VIEW(DEVICE_MANAGEMENT, DEVICE_MANAGEMENT.getBasePropertyId() - 3, booleanType()),
    ENDPOINT_PERMISSION(DEVICE_MANAGEMENT, DEVICE_MANAGEMENT.getBasePropertyId() - 4, InputTypeFactory.permissionType(OWNER, CREATE, UPDATE, RESTRICTED, LIMITED)),
    MANAGE_POINT_DATA(DEVICE_MANAGEMENT, DEVICE_MANAGEMENT.getBasePropertyId() - 5, InputTypeFactory.permissionType(RESTRICTED, UPDATE, OWNER)),
    MANAGE_POINTS(DEVICE_MANAGEMENT, DEVICE_MANAGEMENT.getBasePropertyId() - 6, InputTypeFactory.permissionType(OWNER, CREATE, UPDATE, RESTRICTED, NO_ACCESS)),
    MANAGE_NOTES(DEVICE_MANAGEMENT, DEVICE_MANAGEMENT.getBasePropertyId() - 7, InputTypeFactory.enumType(AccessLevel.class))
    ;
    
    private final YukonRole role;
    private final int propertyId;
    private final InputType<?> type;
    private final static ImmutableMap<Integer, YukonRoleProperty> lookup;
    private final static String baseKey = "yukon.common.roleproperty.";
    
    static {
        Builder<Integer, YukonRoleProperty> builder = ImmutableMap.builder();
        for (YukonRoleProperty yukonRoleProperty : values()) {
            builder.put(yukonRoleProperty.propertyId, yukonRoleProperty);
        }
        lookup = builder.build();
    }
    
    public static YukonRoleProperty getForId(int rolePropertyId) throws IllegalArgumentException {
        YukonRoleProperty yukonRoleProperty = lookup.get(rolePropertyId);
        checkArgument(yukonRoleProperty != null, Integer.toString(rolePropertyId));
        return yukonRoleProperty;
    }
    
    private YukonRoleProperty(YukonRole role, int propertyId, InputType<?> type) {
        this.role = role;
        this.propertyId = propertyId;
        this.type = type;
    }
    
    public YukonRole getRole() {
        return role;
    }
    
    public int getPropertyId() {
        return propertyId;
    }
    
    public InputType<?> getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return name() + " (" + getPropertyId() + ")";
    }

    public String getHelpTextKey() {
        //Gets help text based on the Enum Type (if available) - used in Cap Control
        return baseKey + type.getTypeClass().getSimpleName() + ".helpText";
    }
    
}
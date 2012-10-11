package com.cannontech.core.roleproperties;

import static com.cannontech.core.roleproperties.InputTypeFactory.booleanType;
import static com.cannontech.core.roleproperties.InputTypeFactory.enumType;
import static com.cannontech.core.roleproperties.InputTypeFactory.integerType;
import static com.cannontech.core.roleproperties.InputTypeFactory.stringType;
import static com.cannontech.core.roleproperties.YukonRole.APPLICATION_BILLING;
import static com.cannontech.core.roleproperties.YukonRole.APPLICATION_ESUBSTATION_EDITOR;
import static com.cannontech.core.roleproperties.YukonRole.CBC_ONELINE_CAP_SETTINGS;
import static com.cannontech.core.roleproperties.YukonRole.CBC_ONELINE_FEEDER_SETTINGS;
import static com.cannontech.core.roleproperties.YukonRole.CBC_ONELINE_SUB_SETTINGS;
import static com.cannontech.core.roleproperties.YukonRole.CBC_SETTINGS;
import static com.cannontech.core.roleproperties.YukonRole.CI_CURTAILMENT;
import static com.cannontech.core.roleproperties.YukonRole.COMMANDER;
import static com.cannontech.core.roleproperties.YukonRole.CONSUMER_INFO;
import static com.cannontech.core.roleproperties.YukonRole.DATABASE_EDITOR;
import static com.cannontech.core.roleproperties.YukonRole.DEVICE_ACTIONS;
import static com.cannontech.core.roleproperties.YukonRole.ENERGY_COMPANY;
import static com.cannontech.core.roleproperties.YukonRole.INVENTORY;
import static com.cannontech.core.roleproperties.YukonRole.IVR;
import static com.cannontech.core.roleproperties.YukonRole.LM_DIRECT_LOADCONTROL;
import static com.cannontech.core.roleproperties.YukonRole.METERING;
import static com.cannontech.core.roleproperties.YukonRole.NOTIFICATION_CONFIGURATION;
import static com.cannontech.core.roleproperties.YukonRole.ODDS_FOR_CONTROL;
import static com.cannontech.core.roleproperties.YukonRole.OPERATOR_ADMINISTRATOR;
import static com.cannontech.core.roleproperties.YukonRole.OPERATOR_ESUBSTATION_DRAWINGS;
import static com.cannontech.core.roleproperties.YukonRole.PASSWORD_POLICY;
import static com.cannontech.core.roleproperties.YukonRole.REPORTING;
import static com.cannontech.core.roleproperties.YukonRole.RESIDENTIAL_CUSTOMER;
import static com.cannontech.core.roleproperties.YukonRole.SCHEDULER;
import static com.cannontech.core.roleproperties.YukonRole.TABULAR_DISPLAY_CONSOLE;
import static com.cannontech.core.roleproperties.YukonRole.TRENDING;
import static com.cannontech.core.roleproperties.YukonRole.WEB_CLIENT;
import static com.cannontech.core.roleproperties.YukonRole.WORK_ORDER;

import org.apache.commons.lang.Validate;

import com.cannontech.core.roleproperties.enums.CsrfTokenMode;
import com.cannontech.core.roleproperties.enums.SerialNumberValidation;
import com.cannontech.roles.application.CommanderRole;
import com.cannontech.roles.application.DBEditorRole;
import com.cannontech.roles.application.EsubEditorRole;
import com.cannontech.roles.application.PasswordPolicyRole;
import com.cannontech.roles.application.ReportingRole;
import com.cannontech.roles.application.TDCRole;
import com.cannontech.roles.application.TrendingRole;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.roles.capcontrol.CBCOnelineSettingsRole;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.loadcontrol.DirectLoadcontrolRole;
import com.cannontech.roles.notifications.IvrRole;
import com.cannontech.roles.notifications.NotificationConfigurationRole;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.CICurtailmentRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.operator.DeviceActionsRole;
import com.cannontech.roles.operator.EsubDrawingsRole;
import com.cannontech.roles.operator.InventoryRole;
import com.cannontech.roles.operator.MeteringRole;
import com.cannontech.roles.operator.OddsForControlRole;
import com.cannontech.roles.operator.SchedulerRole;
import com.cannontech.roles.operator.WorkOrderRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.web.input.type.InputType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum YukonRoleProperty {
    // Yukon Grp associated role properties moved to GlobalSettings
    
    DYNAMIC_BILLING_FILE_SETUP(APPLICATION_BILLING, com.cannontech.roles.application.BillingRole.DYNAMIC_BILLING_FILE_SETUP, booleanType()),
    
    COMMAND_MSG_PRIORITY(COMMANDER, CommanderRole.COMMAND_MSG_PRIORITY, stringType()),
    VERSACOM_SERIAL_MODEL(COMMANDER, CommanderRole.VERSACOM_SERIAL_MODEL, booleanType()),
    EXPRESSCOM_SERIAL_MODEL(COMMANDER, CommanderRole.EXPRESSCOM_SERIAL_MODEL, booleanType()),
    DCU_SA205_SERIAL_MODEL(COMMANDER, CommanderRole.DCU_SA205_SERIAL_MODEL, booleanType()),
    DCU_SA305_SERIAL_MODEL(COMMANDER, CommanderRole.DCU_SA305_SERIAL_MODEL, booleanType()),
    COMMANDS_GROUP(COMMANDER, CommanderRole.COMMANDS_GROUP, stringType()),
    READ_DEVICE(COMMANDER, CommanderRole.READ_DEVICE, booleanType()),
    WRITE_TO_DEVICE(COMMANDER, CommanderRole.WRITE_TO_DEVICE, booleanType()),
    CONTROL_DEVICE(COMMANDER, CommanderRole.CONTROL_DEVICE, booleanType()),
    READ_LM_DEVICE(COMMANDER, CommanderRole.READ_LM_DEVICE, booleanType()),
    WRITE_TO_LM_DEVICE(COMMANDER, CommanderRole.WRITE_TO_LM_DEVICE, booleanType()),
    CONTROL_LM_DEVICE(COMMANDER, CommanderRole.CONTROL_LM_DEVICE, booleanType()),
    READ_CAP_CONTROL_DEVICE(COMMANDER, CommanderRole.READ_CAP_CONTROL_DEVICE, booleanType()),
    WRITE_TO_CAP_CONTROL_DEVICE(COMMANDER, CommanderRole.WRITE_TO_CAP_CONTROL_DEVICE, booleanType()),
    CONTROL_CAP_CONTROL_DEVICE(COMMANDER, CommanderRole.CONTROL_CAP_CONTROL_DEVICE, booleanType()),
    EXECUTE_UNKNOWN_COMMAND(COMMANDER, CommanderRole.EXECUTE_UNKNOWN_COMMAND, booleanType()),
    EXECUTE_MANUAL_COMMAND(COMMANDER, CommanderRole.EXECUTE_MANUAL_COMMAND, booleanType()),
    ENABLE_WEB_COMMANDER(COMMANDER, CommanderRole.ENABLE_WEB_COMMANDER, booleanType()),
    ENABLE_CLIENT_COMMANDER(COMMANDER, CommanderRole.ENABLE_CLIENT_COMMANDER, booleanType()),
    
    POINT_ID_EDIT(DATABASE_EDITOR, DBEditorRole.POINT_ID_EDIT, booleanType()),
    DBEDITOR_LM(DATABASE_EDITOR, DBEditorRole.DBEDITOR_LM, stringType()),
    DBEDITOR_SYSTEM(DATABASE_EDITOR, DBEditorRole.DBEDITOR_SYSTEM, stringType()),
    UTILITY_ID_RANGE(DATABASE_EDITOR, DBEditorRole.UTILITY_ID_RANGE, stringType()),
    TRANS_EXCLUSION(DATABASE_EDITOR, DBEditorRole.TRANS_EXCLUSION, stringType()),
    PERMIT_LOGIN_EDIT(DATABASE_EDITOR, DBEditorRole.PERMIT_LOGIN_EDIT, stringType()),
    DATABASE_EDITOR_OPTIONAL_PRODUCT_DEV(DATABASE_EDITOR, DBEditorRole.OPTIONAL_PRODUCT_DEV, stringType()),
    ALLOW_MEMBER_PROGRAMS(DATABASE_EDITOR, DBEditorRole.ALLOW_MEMBER_PROGRAMS, stringType()),
    
    LOCKOUT_DURATION(PASSWORD_POLICY, PasswordPolicyRole.LOCKOUT_DURATION, integerType()),
    LOCKOUT_THRESHOLD(PASSWORD_POLICY, PasswordPolicyRole.LOCKOUT_THRESHOLD, integerType()),
    MAXIMUM_PASSWORD_AGE(PASSWORD_POLICY, PasswordPolicyRole.MAXIMUM_PASSWORD_AGE, integerType()),
    MINIMUM_PASSWORD_AGE(PASSWORD_POLICY, PasswordPolicyRole.MINIMUM_PASSWORD_AGE, integerType()),
    MINIMUM_PASSWORD_LENGTH(PASSWORD_POLICY, PasswordPolicyRole.MINIMUM_PASSWORD_LENGTH, integerType()),
    PASSWORD_HISTORY(PASSWORD_POLICY, PasswordPolicyRole.PASSWORD_HISTORY, integerType()),

    POLICY_QUALITY_CHECK(PASSWORD_POLICY, PasswordPolicyRole.POLICY_QUALITY_CHECK, integerType()),
    POLICY_RULE_UPPERCASE_CHARACTERS(PASSWORD_POLICY, PasswordPolicyRole.POLICY_RULE_UPPERCASE_CHARACTERS, booleanType()),
    POLICY_RULE_LOWERCASE_CHARACTERS(PASSWORD_POLICY, PasswordPolicyRole.POLICY_RULE_LOWERCASE_CHARACTERS, booleanType()),
    POLICY_RULE_BASE_10_DIGITS(PASSWORD_POLICY, PasswordPolicyRole.POLICY_RULE_BASE_10_DIGITS, booleanType()),
    POLICY_RULE_NONALPHANUMERIC_CHARACTERS(PASSWORD_POLICY, PasswordPolicyRole.POLICY_RULE_NONALPHANUMERIC_CHARACTERS, booleanType()),
    POLICY_RULE_UNICODE_CHARACTERS(PASSWORD_POLICY, PasswordPolicyRole.POLICY_RULE_UNICODE_CHARACTERS, booleanType()),
    
    ADMIN_REPORTS_GROUP(REPORTING, ReportingRole.ADMIN_REPORTS_GROUP, booleanType()),
    AMR_REPORTS_GROUP(REPORTING, ReportingRole.AMR_REPORTS_GROUP, booleanType()),
    STATISTICAL_REPORTS_GROUP(REPORTING, ReportingRole.STATISTICAL_REPORTS_GROUP, booleanType()),
    LOAD_MANAGEMENT_REPORTS_GROUP(REPORTING, ReportingRole.LOAD_MANAGEMENT_REPORTS_GROUP, booleanType()),
    CAP_CONTROL_REPORTS_GROUP(REPORTING, ReportingRole.CAP_CONTROL_REPORTS_GROUP, booleanType()),
    DATABASE_REPORTS_GROUP(REPORTING, ReportingRole.DATABASE_REPORTS_GROUP, booleanType()),
    STARS_REPORTS_GROUP(REPORTING, ReportingRole.STARS_REPORTS_GROUP, booleanType()),
    SETTLEMENT_REPORTS_GROUP(REPORTING, ReportingRole.SETTLEMENT_REPORTS_GROUP, booleanType()),
    CI_CURTAILMENT_REPORTS_GROUP(REPORTING, ReportingRole.CI_CURTAILMENT_REPORTS_GROUP, booleanType()),
    
    LOADCONTROL_EDIT(TABULAR_DISPLAY_CONSOLE, TDCRole.LOADCONTROL_EDIT, stringType()),
    MACS_EDIT(TABULAR_DISPLAY_CONSOLE, TDCRole.MACS_EDIT, stringType()),
    TDC_EXPRESS(TABULAR_DISPLAY_CONSOLE, TDCRole.TDC_EXPRESS, stringType()),
    TDC_MAX_ROWS(TABULAR_DISPLAY_CONSOLE, TDCRole.TDC_MAX_ROWS, stringType()),
    TDC_RIGHTS(TABULAR_DISPLAY_CONSOLE, TDCRole.TDC_RIGHTS, stringType()),
    TDC_ALARM_COUNT(TABULAR_DISPLAY_CONSOLE, TDCRole.TDC_ALARM_COUNT, stringType()),
    DECIMAL_PLACES(TABULAR_DISPLAY_CONSOLE, TDCRole.DECIMAL_PLACES, stringType()),
    LC_REDUCTION_COL(TABULAR_DISPLAY_CONSOLE, TDCRole.LC_REDUCTION_COL, stringType()),
    
    GRAPH_EDIT_GRAPHDEFINITION(TRENDING, TrendingRole.GRAPH_EDIT_GRAPHDEFINITION, stringType()),
    TRENDING_DISCLAIMER(TRENDING, TrendingRole.TRENDING_DISCLAIMER, stringType()),
    SCAN_NOW_ENABLED(TRENDING, TrendingRole.SCAN_NOW_ENABLED, stringType()),
    MINIMUM_SCAN_FREQUENCY(TRENDING, TrendingRole.MINIMUM_SCAN_FREQUENCY, stringType()),
    MAXIMUM_DAILY_SCANS(TRENDING, TrendingRole.MAXIMUM_DAILY_SCANS, stringType()),
    
    HOME_URL(WEB_CLIENT, WebClientRole.HOME_URL, stringType()),
    STYLE_SHEET(WEB_CLIENT, WebClientRole.STYLE_SHEET, stringType()),
    NAV_BULLET_SELECTED(WEB_CLIENT, WebClientRole.NAV_BULLET_SELECTED, stringType()),
    NAV_BULLET_EXPAND(WEB_CLIENT, WebClientRole.NAV_BULLET_EXPAND, stringType()),
    HEADER_LOGO(WEB_CLIENT, WebClientRole.HEADER_LOGO, stringType()),
    LOG_IN_URL(WEB_CLIENT, WebClientRole.LOG_IN_URL, stringType()),
    NAV_CONNECTOR_BOTTOM(WEB_CLIENT, WebClientRole.NAV_CONNECTOR_BOTTOM, stringType()),
    NAV_CONNECTOR_MIDDLE(WEB_CLIENT, WebClientRole.NAV_CONNECTOR_MIDDLE, stringType()),
    INBOUND_VOICE_HOME_URL(WEB_CLIENT, WebClientRole.INBOUND_VOICE_HOME_URL, stringType()),
    JAVA_WEB_START_LAUNCHER_ENABLED(WEB_CLIENT, WebClientRole.JAVA_WEB_START_LAUNCHER_ENABLED, booleanType()),
    SUPPRESS_ERROR_PAGE_DETAILS(WEB_CLIENT, WebClientRole.SUPPRESS_ERROR_PAGE_DETAILS, booleanType()),
    DATA_UPDATER_DELAY_MS(WEB_CLIENT, WebClientRole.DATA_UPDATER_DELAY_MS, stringType()),
    STD_PAGE_STYLE_SHEET(WEB_CLIENT, WebClientRole.STD_PAGE_STYLE_SHEET, stringType()),
    THEME_NAME(WEB_CLIENT, WebClientRole.THEME_NAME, stringType()),
    VIEW_ALARMS_AS_ALERTS(WEB_CLIENT, WebClientRole.VIEW_ALARMS_AS_ALERTS, booleanType()),
    DEFAULT_TIMEZONE(WEB_CLIENT, WebClientRole.DEFAULT_TIMEZONE, stringType()),
    SESSION_TIMEOUT(WEB_CLIENT, WebClientRole.SESSION_TIMEOUT, integerType()),
    CSRF_TOKEN_MODE(WEB_CLIENT, WebClientRole.CSRF_TOKEN_MODE, InputTypeFactory.enumType(CsrfTokenMode.class)),
    
    ESUB_EDITOR_ROLE_EXITS(APPLICATION_ESUBSTATION_EDITOR, EsubEditorRole.ESUB_EDITOR_ROLE_EXITS, booleanType()),
    
    SUB_TARGET(CBC_ONELINE_SUB_SETTINGS, CBCOnelineSettingsRole.SUB_TARGET, stringType()),
    SUB_VARLOAD(CBC_ONELINE_SUB_SETTINGS, CBCOnelineSettingsRole.SUB_VARLOAD, stringType()),
    SUB_EST_VARLOAD(CBC_ONELINE_SUB_SETTINGS, CBCOnelineSettingsRole.SUB_EST_VARLOAD, stringType()),
    SUB_POWER_FACTOR(CBC_ONELINE_SUB_SETTINGS, CBCOnelineSettingsRole.SUB_POWER_FACTOR, stringType()),
    SUB_EST_POWER_FACTOR(CBC_ONELINE_SUB_SETTINGS, CBCOnelineSettingsRole.SUB_EST_POWER_FACTOR, stringType()),
    SUB_WATTS(CBC_ONELINE_SUB_SETTINGS, CBCOnelineSettingsRole.SUB_WATTS, stringType()),
    SUB_VOLTS(CBC_ONELINE_SUB_SETTINGS, CBCOnelineSettingsRole.SUB_VOLTS, stringType()),
    SUB_DAILY_MAX_OPCNT(CBC_ONELINE_SUB_SETTINGS, CBCOnelineSettingsRole.SUB_DAILY_MAX_OPCNT, stringType()),
    SUB_TIMESTAMP(CBC_ONELINE_SUB_SETTINGS, CBCOnelineSettingsRole.SUB_TIMESTAMP, stringType()),
    SUB_THREE_PHASE(CBC_ONELINE_SUB_SETTINGS, CBCOnelineSettingsRole.SUB_THREE_PHASE, stringType()),
    
    FDR_KVAR(CBC_ONELINE_FEEDER_SETTINGS, CBCOnelineSettingsRole.FDR_KVAR, stringType()),
    FDR_PF(CBC_ONELINE_FEEDER_SETTINGS, CBCOnelineSettingsRole.FDR_PF, stringType()),
    FDR_WATT(CBC_ONELINE_FEEDER_SETTINGS, CBCOnelineSettingsRole.FDR_WATT, stringType()),
    FDR_OP_CNT(CBC_ONELINE_FEEDER_SETTINGS, CBCOnelineSettingsRole.FDR_OP_CNT, stringType()),
    FDR_VOLT(CBC_ONELINE_FEEDER_SETTINGS, CBCOnelineSettingsRole.FDR_VOLT, stringType()),
    FDR_TARGET(CBC_ONELINE_FEEDER_SETTINGS, CBCOnelineSettingsRole.FDR_TARGET, stringType()),
    FDR_TIMESTAMP(CBC_ONELINE_FEEDER_SETTINGS, CBCOnelineSettingsRole.FDR_TIMESTAMP, stringType()),
    FDR_WATT_VOLT(CBC_ONELINE_FEEDER_SETTINGS, CBCOnelineSettingsRole.FDR_WATT_VOLT, stringType()),
    FDR_THREE_PHASE(CBC_ONELINE_FEEDER_SETTINGS, CBCOnelineSettingsRole.FDR_THREE_PHASE, stringType()),
    
    CAP_BANK_SIZE(CBC_ONELINE_CAP_SETTINGS, CBCOnelineSettingsRole.CAP_BANK_SIZE, stringType()),
    CAP_CBC_NAME(CBC_ONELINE_CAP_SETTINGS, CBCOnelineSettingsRole.CAP_CBC_NAME, stringType()),
    CAP_TIMESTAMP(CBC_ONELINE_CAP_SETTINGS, CBCOnelineSettingsRole.CAP_TIMESTAMP, stringType()),
    CAP_BANK_FIXED_TEXT(CBC_ONELINE_CAP_SETTINGS, CBCOnelineSettingsRole.CAP_BANK_FIXED_TEXT, stringType()),
    CAP_DAILY_MAX_TOTAL_OPCNT(CBC_ONELINE_CAP_SETTINGS, CBCOnelineSettingsRole.CAP_DAILY_MAX_TOTAL_OPCNT, stringType()),
    
    CAP_CONTROL_ACCESS(CBC_SETTINGS, CBCSettingsRole.ACCESS, booleanType()),
    HIDE_REPORTS(CBC_SETTINGS, CBCSettingsRole.HIDE_REPORTS, booleanType()),
    HIDE_GRAPHS(CBC_SETTINGS, CBCSettingsRole.HIDE_GRAPHS, booleanType()),
    HIDE_ONELINE(CBC_SETTINGS, CBCSettingsRole.HIDE_ONELINE, booleanType()),
    CAP_CONTROL_INTERFACE(CBC_SETTINGS, CBCSettingsRole.CAP_CONTROL_INTERFACE, stringType()),
    CBC_CREATION_NAME(CBC_SETTINGS, CBCSettingsRole.CBC_CREATION_NAME, stringType()),
    PFACTOR_DECIMAL_PLACES(CBC_SETTINGS, CBCSettingsRole.PFACTOR_DECIMAL_PLACES, integerType()),
    CBC_ALLOW_OVUV(CBC_SETTINGS, CBCSettingsRole.CBC_ALLOW_OVUV, booleanType()),
    CBC_DATABASE_EDIT(CBC_SETTINGS, CBCSettingsRole.CBC_DATABASE_EDIT, booleanType()),
    SHOW_FLIP_COMMAND(CBC_SETTINGS, CBCSettingsRole.SHOW_FLIP_COMMAND, booleanType()),
    SHOW_CB_ADDINFO(CBC_SETTINGS, CBCSettingsRole.SHOW_CB_ADDINFO, booleanType()),
    AVAILABLE_DEFINITION(CBC_SETTINGS, CBCSettingsRole.AVAILABLE_DEFINITION, stringType()),
    UNAVAILABLE_DEFINITION(CBC_SETTINGS, CBCSettingsRole.UNAVAILABLE_DEFINITION, stringType()),
    TRIPPED_DEFINITION(CBC_SETTINGS, CBCSettingsRole.TRIPPED_DEFINITION, stringType()),
    CLOSED_DEFINITION(CBC_SETTINGS, CBCSettingsRole.CLOSED_DEFINITION, stringType()),
    ADD_COMMENTS(CBC_SETTINGS, CBCSettingsRole.ADD_COMMENTS, booleanType()),
    MODIFY_COMMENTS(CBC_SETTINGS, CBCSettingsRole.MODIFY_COMMENTS, booleanType()),
    SYSTEM_WIDE_CONTROLS(CBC_SETTINGS, CBCSettingsRole.SYSTEM_WIDE_CONTROLS, booleanType()),
    FORCE_COMMENTS(CBC_SETTINGS, CBCSettingsRole.FORCE_COMMENTS, booleanType()),
    ALLOW_AREA_CONTROLS(CBC_SETTINGS, CBCSettingsRole.ALLOW_AREA_CONTROLS, booleanType()),
    ALLOW_SUBSTATION_CONTROLS(CBC_SETTINGS, CBCSettingsRole.ALLOW_SUBSTATION_CONTROLS, booleanType()),
    ALLOW_SUBBUS_CONTROLS(CBC_SETTINGS, CBCSettingsRole.ALLOW_SUBBUS_CONTROLS, booleanType()),
    ALLOW_FEEDER_CONTROLS(CBC_SETTINGS, CBCSettingsRole.ALLOW_FEEDER_CONTROLS, booleanType()),
    ALLOW_CAPBANK_CONTROLS(CBC_SETTINGS, CBCSettingsRole.ALLOW_CAPBANK_CONTROLS, booleanType()),
    CONTROL_WARNING(CBC_SETTINGS, CBCSettingsRole.CONTROL_WARNING, booleanType()),
    CAP_CONTROL_IMPORTER(CBC_SETTINGS, CBCSettingsRole.CAP_CONTROL_IMPORTER, booleanType()),
    
    RESIDENTIAL_CONSUMER_INFO_ACCOUNT_GENERAL(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.CONSUMER_INFO_ACCOUNT_GENERAL, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_PROGRAMS_ENROLLMENT(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_PROGRAMS_OPT_OUT(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_HARDWARES_THERMOSTAT(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.CONSUMER_INFO_HARDWARES_THERMOSTAT, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_QUESTIONS_UTIL(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_UTIL, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_QUESTIONS_FAQ(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_FAQ, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_USERNAME(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.CONSUMER_INFO_CHANGE_LOGIN_USERNAME, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_THERMOSTATS_ALL(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.CONSUMER_INFO_THERMOSTATS_ALL, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_PASSWORD(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.CONSUMER_INFO_CHANGE_LOGIN_PASSWORD, booleanType()),
    RESIDENTIAL_HIDE_OPT_OUT_BOX(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.HIDE_OPT_OUT_BOX, stringType()),
    RESIDENTIAL_OPT_OUT_PERIOD(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.OPT_OUT_PERIOD, stringType()),
    RESIDENTIAL_OPT_OUT_LIMITS(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.OPT_OUT_LIMITS, stringType()),
    RESIDENTIAL_WEB_LINK_FAQ(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.WEB_LINK_FAQ, stringType()),
    RESIDENTIAL_WEB_LINK_THERM_INSTRUCTIONS(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.WEB_LINK_THERM_INSTRUCTIONS, stringType()),
    RESIDENTIAL_CONTACTS_ACCESS(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.CONTACTS_ACCESS, booleanType()),
    RESIDENTIAL_OPT_OUT_TODAY_ONLY(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.OPT_OUT_TODAY_ONLY, booleanType()),
    RESIDENTIAL_SIGN_OUT_ENABLED(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.SIGN_OUT_ENABLED, booleanType()),
    RESIDENTIAL_CREATE_LOGIN_FOR_ACCOUNT(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.CREATE_LOGIN_FOR_ACCOUNT, booleanType()),
    RESIDENTIAL_OPT_OUT_ALL_DEVICES(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.OPT_OUT_ALL_DEVICES, booleanType()),
    RESIDENTIAL_ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY, booleanType()),
    RESIDENTIAL_ENROLLMENT_PER_DEVICE(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.ENROLLMENT_PER_DEVICE, booleanType()),
    RESIDENTIAL_AUTO_THERMOSTAT_MODE_ENABLED(RESIDENTIAL_CUSTOMER, ResidentialCustomerRole.AUTO_THERMOSTAT_MODE_ENABLED, booleanType()),
    
    LM_INDIVIDUAL_SWITCH(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.INDIVIDUAL_SWITCH, booleanType()),
    DEMAND_RESPONSE(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.DEMAND_RESPONSE, booleanType()),
    DIRECT_CONTROL(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.DIRECT_CONTROL, booleanType()),
    ALLOW_CHECK_CONSTRAINTS(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.ALLOW_CHECK_CONSTRAINTS, booleanType()),
    ALLOW_OBSERVE_CONSTRAINTS(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.ALLOW_OBSERVE_CONSTRAINTS, booleanType()),
    ALLOW_OVERRIDE_CONSTRAINT(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.ALLOW_OVERRIDE_CONSTRAINT, booleanType()),
    DEFAULT_CONSTRAINT_SELECTION(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.DEFAULT_CONSTRAINT_SELECTION, stringType()),
    ALLOW_STOP_GEAR_ACCESS(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.ALLOW_STOP_GEAR_ACCESS, booleanType()),
    IGNORE_PER_PAO_PERMISSIONS(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.IGNORE_PER_PAO_PERMISSIONS, booleanType()),
    SHOW_CONTROL_AREAS(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.SHOW_CONTROL_AREAS, booleanType()),
    SHOW_SCENARIOS(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.SHOW_SCENARIOS, booleanType()),
    START_NOW_CHECKED_BY_DEFAULT(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.START_NOW_CHECKED_BY_DEFAULT, booleanType()),
    CONTROL_DURATION_DEFAULT(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.CONTROL_DURATION_DEFAULT, integerType()),
    SCHEDULE_STOP_CHECKED_BY_DEFAULT(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.SCHEDULE_STOP_CHECKED_BY_DEFAULT, booleanType()),
    START_TIME_DEFAULT(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.START_TIME_DEFAULT, stringType()),
    
    CONTROL_AREA_STATE(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.CONTROL_AREA_STATE, booleanType()),
    CONTROL_AREA_VALUE_THRESHOLD(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.CONTROL_AREA_VALUE_THRESHOLD, booleanType()),
    CONTROL_AREA_PEAK_PROJECTION(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.CONTROL_AREA_PEAK_PROJECTION, booleanType()),
    CONTROL_AREA_ATKU(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.CONTROL_AREA_ATKU, booleanType()),
    CONTROL_AREA_PRIORITY(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.CONTROL_AREA_PRIORITY, booleanType()),
    CONTROL_AREA_TIME_WINDOW(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.CONTROL_AREA_TIME_WINDOW, booleanType()),
    CONTROL_AREA_LOAD_CAPACITY(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.CONTROL_AREA_LOAD_CAPACITY, booleanType()),

    PROGRAM_STATE(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.PROGRAM_STATE, booleanType()),
    PROGRAM_START(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.PROGRAM_START, booleanType()),
    PROGRAM_STOP(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.PROGRAM_STOP, booleanType()),
    PROGRAM_CURRENT_GEAR(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.PROGRAM_CURRENT_GEAR, booleanType()),
    PROGRAM_PRIORITY(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.PROGRAM_PRIORITY, booleanType()),
    PROGRAM_REDUCTION(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.PROGRAM_REDUCTION, booleanType()),
    PROGRAM_LOAD_CAPACITY(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.PROGRAM_LOAD_CAPACITY, booleanType()),

    LOAD_GROUP_STATE(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.LOAD_GROUP_STATE, booleanType()),
    LOAD_GROUP_LAST_ACTION(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.LOAD_GROUP_LAST_ACTION, booleanType()),
    LOAD_GROUP_CONTROL_STATISTICS(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.LOAD_GROUP_CONTROL_STATISTICS, booleanType()),
    LOAD_GROUP_REDUCTION(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.LOAD_GROUP_REDUCTION, booleanType()),
    LOAD_GROUP_LOAD_CAPACITY(LM_DIRECT_LOADCONTROL, DirectLoadcontrolRole.LOAD_GROUP_LOAD_CAPACITY, booleanType()),
    
    NUMBER_OF_CHANNELS(IVR, IvrRole.NUMBER_OF_CHANNELS, integerType()),
    IVR_URL_DIALER_TEMPLATE(IVR, IvrRole.IVR_URL_DIALER_TEMPLATE, stringType()),
    IVR_URL_DIALER_SUCCESS_MATCHER(IVR, IvrRole.IVR_URL_DIALER_SUCCESS_MATCHER, stringType()),
    
    TEMPLATE_ROOT(NOTIFICATION_CONFIGURATION, NotificationConfigurationRole.TEMPLATE_ROOT, stringType()),
    
    ADMIN_SUPER_USER(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_SUPER_USER, booleanType()),
    ADMIN_EDIT_ENERGY_COMPANY(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_EDIT_ENERGY_COMPANY, booleanType()),
    ADMIN_CREATE_DELETE_ENERGY_COMPANY(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_CREATE_DELETE_ENERGY_COMPANY, booleanType()),
    ADMIN_MANAGE_MEMBERS(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_MANAGE_MEMBERS, booleanType()),
    ADMIN_VIEW_BATCH_COMMANDS(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_VIEW_BATCH_COMMANDS, booleanType()),
    ADMIN_VIEW_OPT_OUT_EVENTS(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_VIEW_OPT_OUT_EVENTS, booleanType()),
    ADMIN_MEMBER_LOGIN_CNTRL(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_MEMBER_LOGIN_CNTRL, booleanType()),
    ADMIN_MEMBER_ROUTE_SELECT(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_MEMBER_ROUTE_SELECT, booleanType()),
    
    ADMIN_MULTI_WAREHOUSE(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_MULTI_WAREHOUSE, booleanType()),
    ADMIN_AUTO_PROCESS_BATCH_COMMANDS(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_AUTO_PROCESS_BATCH_COMMANDS, stringType()),
    ADMIN_MULTISPEAK_SETUP(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_MULTISPEAK_SETUP, booleanType()),
    ADMIN_LM_USER_ASSIGN(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_LM_USER_ASSIGN, booleanType()),
    ADMIN_EDIT_CONFIG(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_EDIT_CONFIG, booleanType()),
    ADMIN_VIEW_CONFIG(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_VIEW_CONFIG, booleanType()),
    ADMIN_MANAGE_INDEXES(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_MANAGE_INDEXES, booleanType()),
    ADMIN_VIEW_LOGS(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_VIEW_LOGS, booleanType()),
    ADMIN_DATABASE_MIGRATION(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_DATABASE_MIGRATION, booleanType()),
    ADMIN_EVENT_LOGS(OPERATOR_ADMINISTRATOR, AdministratorRole.ADMIN_EVENT_LOGS, booleanType()),
    
    CURTAILMENT_LABEL(CI_CURTAILMENT, CICurtailmentRole.CURTAILMENT_LABEL, stringType()),

    OPERATOR_CONSUMER_INFO_ACCOUNT_RESIDENCE(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_RESIDENCE, booleanType()),
    OPERATOR_CONSUMER_INFO_ACCOUNT_CALL_TRACKING(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_CALL_TRACKING, booleanType()),
    OPERATOR_CONSUMER_INFO_METERING_INTERVAL_DATA(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_METERING_INTERVAL_DATA, booleanType()),
    OPERATOR_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY, booleanType()),
    OPERATOR_CONSUMER_INFO_PROGRAMS_ENROLLMENT(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT, booleanType()),
    OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT, booleanType()),
    OPERATOR_CONSUMER_INFO_APPLIANCES(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_APPLIANCES, booleanType()),
    OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_APPLIANCES_CREATE, booleanType()),
    OPERATOR_CONSUMER_INFO_HARDWARES(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_HARDWARES, booleanType()),
    OPERATOR_CONSUMER_INFO_HARDWARES_CREATE(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_HARDWARES_CREATE, booleanType()),
    OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_HARDWARES_THERMOSTAT, booleanType()),
    OPERATOR_CONSUMER_INFO_WORK_ORDERS(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_WORK_ORDERS, booleanType()),
    OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_WS_LM_DATA_ACCESS, booleanType()),
    OPERATOR_CONSUMER_INFO_WS_LM_CONTROL_ACCESS(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_WS_LM_CONTROL_ACCESS, booleanType()),
    OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME, booleanType()),
    OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD, booleanType()),
    OPERATOR_CREATE_LOGIN_FOR_ACCOUNT(CONSUMER_INFO, ConsumerInfoRole.CREATE_LOGIN_FOR_ACCOUNT, booleanType()),
    OPERATOR_CONSUMER_INFO_ADMIN_FAQ(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_ADMIN_FAQ, booleanType()),
    OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_THERMOSTATS_ALL, booleanType()),
    OPERATOR_CONSUMER_INFO_METERING_CREATE(CONSUMER_INFO, ConsumerInfoRole.CONSUMER_INFO_METERING_CREATE, booleanType()),
    OPERATOR_NEW_ACCOUNT_WIZARD(CONSUMER_INFO, ConsumerInfoRole.NEW_ACCOUNT_WIZARD, booleanType()),
    OPERATOR_IMPORT_CUSTOMER_ACCOUNT(CONSUMER_INFO, ConsumerInfoRole.IMPORT_CUSTOMER_ACCOUNT, booleanType()),
    OPERATOR_INVENTORY_CHECKING(CONSUMER_INFO, ConsumerInfoRole.INVENTORY_CHECKING, booleanType()),
    OPERATOR_ORDER_NUMBER_AUTO_GEN(CONSUMER_INFO, ConsumerInfoRole.ORDER_NUMBER_AUTO_GEN, booleanType()),
    OPERATOR_CALL_NUMBER_AUTO_GEN(CONSUMER_INFO, ConsumerInfoRole.CALL_NUMBER_AUTO_GEN, stringType()),
    OPERATOR_OPT_OUT_PERIOD(CONSUMER_INFO, ConsumerInfoRole.OPT_OUT_PERIOD, stringType()),
    OPERATOR_DISABLE_SWITCH_SENDING(CONSUMER_INFO, ConsumerInfoRole.DISABLE_SWITCH_SENDING, booleanType()),
    OPERATOR_METER_SWITCH_ASSIGNMENT(CONSUMER_INFO, ConsumerInfoRole.METER_SWITCH_ASSIGNMENT, stringType()),
    OPERATOR_ALLOW_ACCOUNT_EDITING(CONSUMER_INFO, ConsumerInfoRole.ALLOW_ACCOUNT_EDITING, booleanType()),
    OPERATOR_WEB_LINK_FAQ(CONSUMER_INFO, ConsumerInfoRole.WEB_LINK_FAQ, stringType()),
    OPERATOR_WEB_LINK_THERM_INSTRUCTIONS(CONSUMER_INFO, ConsumerInfoRole.WEB_LINK_THERM_INSTRUCTIONS, stringType()),
    OPERATOR_INVENTORY_CHECKING_CREATE(CONSUMER_INFO, ConsumerInfoRole.INVENTORY_CHECKING_CREATE, booleanType()),
    OPERATOR_OPT_OUT_TODAY_ONLY(CONSUMER_INFO, ConsumerInfoRole.OPT_OUT_TODAY_ONLY, booleanType()),
    OPERATOR_OPT_OUT_ADMIN_STATUS(CONSUMER_INFO, ConsumerInfoRole.OPT_OUT_ADMIN_STATUS, booleanType()),
    OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE(CONSUMER_INFO, ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_ENABLE, booleanType()),
    OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT(CONSUMER_INFO, ConsumerInfoRole.OPT_OUT_ADMIN_CANCEL_CURRENT, booleanType()),
    OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS(CONSUMER_INFO, ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_COUNTS, booleanType()),
    OPERATOR_ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY(CONSUMER_INFO, ConsumerInfoRole.ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY, booleanType()),
    OPERATOR_ACCOUNT_SEARCH(CONSUMER_INFO, ConsumerInfoRole.ACCOUNT_SEARCH, booleanType()),
    OPERATOR_SURVEY_EDIT(CONSUMER_INFO, ConsumerInfoRole.SURVEY_EDIT, booleanType()),
    OPERATOR_OPT_OUT_SURVEY_EDIT(CONSUMER_INFO, ConsumerInfoRole.OPT_OUT_SURVEY_EDIT, booleanType()),

    BULK_IMPORT_OPERATION(DEVICE_ACTIONS, DeviceActionsRole.BULK_IMPORT_OPERATION, booleanType()),
    BULK_UPDATE_OPERATION(DEVICE_ACTIONS, DeviceActionsRole.BULK_UPDATE_OPERATION, booleanType()),
    DEVICE_GROUP_EDIT(DEVICE_ACTIONS, DeviceActionsRole.DEVICE_GROUP_EDIT, booleanType()),
    DEVICE_GROUP_MODIFY(DEVICE_ACTIONS, DeviceActionsRole.DEVICE_GROUP_MODIFY, booleanType()),
    GROUP_COMMANDER(DEVICE_ACTIONS, DeviceActionsRole.GROUP_COMMANDER, booleanType()),
    MASS_CHANGE(DEVICE_ACTIONS, DeviceActionsRole.MASS_CHANGE, booleanType()),
    LOCATE_ROUTE(DEVICE_ACTIONS, DeviceActionsRole.LOCATE_ROUTE, booleanType()),
    MASS_DELETE(DEVICE_ACTIONS, DeviceActionsRole.MASS_DELETE, booleanType()),
    ADD_REMOVE_POINTS(DEVICE_ACTIONS, DeviceActionsRole.ADD_REMOVE_POINTS, booleanType()),
    ASSIGN_CONFIG(DEVICE_ACTIONS, DeviceActionsRole.ASSIGN_CONFIG, booleanType()),
    SEND_READ_CONFIG(DEVICE_ACTIONS, DeviceActionsRole.SEND_READ_CONFIG, booleanType()),
    ARCHIVED_DATA_ANALYSIS(DEVICE_ACTIONS, DeviceActionsRole.ARCHIVED_DATA_ANALYSIS, booleanType()),   
    FDR_TRANSLATION_MANAGER(DEVICE_ACTIONS, DeviceActionsRole.FDR_TRANSLATION_MANAGER, booleanType()),
    ARCHIVED_DATA_EXPORT(DEVICE_ACTIONS, DeviceActionsRole.ARCHIVED_DATA_EXPORT, booleanType()),  
    
    
    OPERATOR_ESUBSTATION_DRAWINGS_VIEW(OPERATOR_ESUBSTATION_DRAWINGS, EsubDrawingsRole.VIEW, booleanType()),
    OPERATOR_ESUBSTATION_DRAWINGS_EDIT(OPERATOR_ESUBSTATION_DRAWINGS, EsubDrawingsRole.EDIT, booleanType()),
    OPERATOR_ESUBSTATION_DRAWINGS_CONTROL(OPERATOR_ESUBSTATION_DRAWINGS, EsubDrawingsRole.CONTROL, booleanType()),
    OPERATOR_ESUBSTATION_DRAWINGS_HOME_URL(OPERATOR_ESUBSTATION_DRAWINGS, EsubDrawingsRole.ESUB_HOME_URL, stringType()),
    
    INVENTORY_SHOW_ALL(INVENTORY, InventoryRole.INVENTORY_SHOW_ALL, booleanType()),
    SN_ADD_RANGE(INVENTORY, InventoryRole.SN_ADD_RANGE, booleanType()),
    SN_UPDATE_RANGE(INVENTORY, InventoryRole.SN_UPDATE_RANGE, booleanType()),
    SN_CONFIG_RANGE(INVENTORY, InventoryRole.SN_CONFIG_RANGE, booleanType()),
    SN_DELETE_RANGE(INVENTORY, InventoryRole.SN_DELETE_RANGE, booleanType()),
    INVENTORY_CREATE_HARDWARE(INVENTORY, InventoryRole.INVENTORY_CREATE_HARDWARE, booleanType()),
    EXPRESSCOM_TOOS_RESTORE_FIRST(INVENTORY, InventoryRole.EXPRESSCOM_TOOS_RESTORE_FIRST, booleanType()),
    ALLOW_MULTIPLE_WAREHOUSES(INVENTORY, InventoryRole.ALLOW_MULTIPLE_WAREHOUSES, booleanType()),
    PURCHASING_ACCESS(INVENTORY, InventoryRole.PURCHASING_ACCESS, booleanType()),
    DEVICE_RECONFIG(INVENTORY, InventoryRole.DEVICE_RECONFIG, booleanType()),
    INVENTORY_SEARCH(INVENTORY, InventoryRole.INVENTORY_SEARCH, booleanType()),
    
    IMPORTER_ENABLED(METERING, MeteringRole.IMPORTER_ENABLED, booleanType()),
    PROFILE_COLLECTION(METERING, MeteringRole.PROFILE_COLLECTION, booleanType()),
    MOVE_IN_MOVE_OUT_AUTO_ARCHIVING(METERING, MeteringRole.MOVE_IN_MOVE_OUT_AUTO_ARCHIVING, booleanType()),
    MOVE_IN_MOVE_OUT(METERING, MeteringRole.MOVE_IN_MOVE_OUT, booleanType()),
    PROFILE_COLLECTION_SCANNING(METERING, MeteringRole.PROFILE_COLLECTION_SCANNING, booleanType()),
    HIGH_BILL_COMPLAINT(METERING, MeteringRole.HIGH_BILL_COMPLAINT, booleanType()),
    CIS_DETAIL_WIDGET_ENABLED(METERING, MeteringRole.CIS_DETAIL_WIDGET_ENABLED, booleanType()),
    CIS_DETAIL_TYPE(METERING, MeteringRole.CIS_DETAIL_TYPE, InputTypeFactory.enumType(CisDetailRolePropertyEnum.class)),
    OUTAGE_PROCESSING(METERING, MeteringRole.OUTAGE_PROCESSING, booleanType()),
    TAMPER_FLAG_PROCESSING(METERING, MeteringRole.TAMPER_FLAG_PROCESSING, booleanType()),
    PHASE_DETECT(METERING, MeteringRole.PHASE_DETECT, booleanType()),
    VALIDATION_ENGINE(METERING, MeteringRole.VALIDATION_ENGINE, booleanType()),
    STATUS_POINT_MONITORING(METERING, MeteringRole.STATUS_POINT_MONITORING, booleanType()),
    PORTER_RESPONSE_MONITORING(METERING, MeteringRole.PORTER_RESPONSE_MONITORING, booleanType()),
    METER_EVENTS(METERING, MeteringRole.METER_EVENTS, booleanType()),
    ALLOW_DISCONNECT_CONTROL(METERING, MeteringRole.ALLOW_DISCONNECT_CONTROL, booleanType()),
    
    ODDS_FOR_CONTROL_LABEL(ODDS_FOR_CONTROL, OddsForControlRole.ODDS_FOR_CONTROL_LABEL, stringType()),
    
    ENABLE_DISABLE_SCRIPTS(SCHEDULER, SchedulerRole.ENABLE_DISABLE_SCRIPTS, booleanType()),
    MANAGE_SCHEDULES(SCHEDULER, SchedulerRole.MANAGE_SCHEDULES, booleanType()),
    
    WORK_ORDER_SHOW_ALL(WORK_ORDER, WorkOrderRole.WORK_ORDER_SHOW_ALL, booleanType()),
    WORK_ORDER_CREATE_NEW(WORK_ORDER, WorkOrderRole.WORK_ORDER_CREATE_NEW, stringType()),
    WORK_ORDER_REPORT(WORK_ORDER, WorkOrderRole.WORK_ORDER_REPORT, stringType()),
    ADDTL_ORDER_NUMBER_LABEL(WORK_ORDER, WorkOrderRole.ADDTL_ORDER_NUMBER_LABEL, stringType()),
    
    
    /* Use EnergyCompanyRolePropertyDao when dealing with these role properties */
    ADMIN_EMAIL_ADDRESS(ENERGY_COMPANY, EnergyCompanyRole.ADMIN_EMAIL_ADDRESS, stringType()),
    OPTOUT_NOTIFICATION_RECIPIENTS(ENERGY_COMPANY, EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS, stringType()),
    ENERGY_COMPANY_DEFAULT_TIME_ZONE(ENERGY_COMPANY, EnergyCompanyRole.DEFAULT_TIME_ZONE, stringType()),
    TRACK_HARDWARE_ADDRESSING(ENERGY_COMPANY, EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING, booleanType()),
    SINGLE_ENERGY_COMPANY(ENERGY_COMPANY, EnergyCompanyRole.SINGLE_ENERGY_COMPANY, booleanType()),
    OPTIONAL_PRODUCT_DEV(ENERGY_COMPANY, EnergyCompanyRole.OPTIONAL_PRODUCT_DEV, stringType()),
    DEFAULT_TEMPERATURE_UNIT(ENERGY_COMPANY, EnergyCompanyRole.DEFAULT_TEMPERATURE_UNIT, stringType()),
    METER_MCT_BASE_DESIGNATION(ENERGY_COMPANY, EnergyCompanyRole.METER_MCT_BASE_DESIGNATION, enumType(EnergyCompanyRole.MeteringType.class)),
    APPLICABLE_POINT_TYPE_KEY(ENERGY_COMPANY, EnergyCompanyRole.APPLICABLE_POINT_TYPE_KEY, stringType()),
    INHERIT_PARENT_APP_CATS(ENERGY_COMPANY, EnergyCompanyRole.INHERIT_PARENT_APP_CATS, booleanType()),
    AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS(ENERGY_COMPANY, EnergyCompanyRole.AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS, booleanType()),
    ACCOUNT_NUMBER_LENGTH(ENERGY_COMPANY, EnergyCompanyRole.ACCOUNT_NUMBER_LENGTH, integerType()),
    ROTATION_DIGIT_LENGTH(ENERGY_COMPANY, EnergyCompanyRole.ROTATION_DIGIT_LENGTH, integerType()),
    SERIAL_NUMBER_VALIDATION(ENERGY_COMPANY, EnergyCompanyRole.SERIAL_NUMBER_VALIDATION, InputTypeFactory.enumType(SerialNumberValidation.class)),
    AUTOMATIC_CONFIGURATION(ENERGY_COMPANY, EnergyCompanyRole.AUTOMATIC_CONFIGURATION, booleanType()),
    ADMIN_ALLOW_DESIGNATION_CODES(ENERGY_COMPANY, EnergyCompanyRole.ALLOW_DESIGNATION_CODES, booleanType()),
    ADMIN_ALLOW_THERMOSTAT_SCHEDULE_ALL(ENERGY_COMPANY, EnergyCompanyRole.ALLOW_THERMOSTAT_SCHEDULE_ALL, booleanType()),
    ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND(ENERGY_COMPANY, EnergyCompanyRole.ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND, booleanType()),
    ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_SATURDAY_SUNDAY(ENERGY_COMPANY, EnergyCompanyRole.ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_SATURDAY_SUNDAY, booleanType()),
    ADMIN_ALLOW_THERMOSTAT_SCHEDULE_7_DAY(ENERGY_COMPANY, EnergyCompanyRole.ALLOW_THERMOSTAT_SCHEDULE_7_DAY, booleanType()),
    ;
    
    private final YukonRole role;
    private final int propertyId;
    private final InputType<?> type;
    private final static ImmutableMap<Integer, YukonRoleProperty> lookup;
    
    static {
        Builder<Integer, YukonRoleProperty> builder = ImmutableMap.builder();
        for (YukonRoleProperty yukonRoleProperty : values()) {
            builder.put(yukonRoleProperty.propertyId, yukonRoleProperty);
        }
        lookup = builder.build();
    }
    
    public static YukonRoleProperty getForId(int rolePropertyId) throws IllegalArgumentException {
        YukonRoleProperty yukonRoleProperty = lookup.get(rolePropertyId);
        Validate.notNull(yukonRoleProperty, Integer.toString(rolePropertyId));
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
}

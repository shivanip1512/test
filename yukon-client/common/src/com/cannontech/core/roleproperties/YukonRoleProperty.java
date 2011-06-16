package com.cannontech.core.roleproperties;

import static com.cannontech.core.roleproperties.YukonRole.*;
import static com.cannontech.core.roleproperties.InputTypeFactory.*;

import java.math.RoundingMode;

import org.apache.commons.lang.Validate;

import com.cannontech.amr.meter.dao.impl.MeterDisplayFieldEnum;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.roleproperties.enums.CsrfTokenMode;
import com.cannontech.core.roleproperties.enums.SerialNumberValidation;
import com.cannontech.web.input.type.InputType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum YukonRoleProperty {
    DYNAMIC_BILLING_FILE_SETUP(APPLICATION_BILLING, com.cannontech.roles.application.BillingRole.DYNAMIC_BILLING_FILE_SETUP, booleanType()),
    
    INTERVAL(CALC_HISTORICAL, com.cannontech.roles.application.CalcHistoricalRole.INTERVAL, stringType()),
    BASELINE_CALCTIME(CALC_HISTORICAL, com.cannontech.roles.application.CalcHistoricalRole.BASELINE_CALCTIME, stringType()),
    DAYS_PREVIOUS_TO_COLLECT(CALC_HISTORICAL, com.cannontech.roles.application.CalcHistoricalRole.DAYS_PREVIOUS_TO_COLLECT, stringType()),
    
    COMMAND_MSG_PRIORITY(COMMANDER, com.cannontech.roles.application.CommanderRole.COMMAND_MSG_PRIORITY, stringType()),
    VERSACOM_SERIAL_MODEL(COMMANDER, com.cannontech.roles.application.CommanderRole.VERSACOM_SERIAL_MODEL, booleanType()),
    EXPRESSCOM_SERIAL_MODEL(COMMANDER, com.cannontech.roles.application.CommanderRole.EXPRESSCOM_SERIAL_MODEL, booleanType()),
    DCU_SA205_SERIAL_MODEL(COMMANDER, com.cannontech.roles.application.CommanderRole.DCU_SA205_SERIAL_MODEL, booleanType()),
    DCU_SA305_SERIAL_MODEL(COMMANDER, com.cannontech.roles.application.CommanderRole.DCU_SA305_SERIAL_MODEL, booleanType()),
    COMMANDS_GROUP(COMMANDER, com.cannontech.roles.application.CommanderRole.COMMANDS_GROUP, stringType()),
    READ_DEVICE(COMMANDER, com.cannontech.roles.application.CommanderRole.READ_DEVICE, booleanType()),
    WRITE_TO_DEVICE(COMMANDER, com.cannontech.roles.application.CommanderRole.WRITE_TO_DEVICE, booleanType()),
    CONTROL_DEVICE(COMMANDER, com.cannontech.roles.application.CommanderRole.CONTROL_DEVICE, booleanType()),
    READ_LM_DEVICE(COMMANDER, com.cannontech.roles.application.CommanderRole.READ_LM_DEVICE, booleanType()),
    WRITE_TO_LM_DEVICE(COMMANDER, com.cannontech.roles.application.CommanderRole.WRITE_TO_LM_DEVICE, booleanType()),
    CONTROL_LM_DEVICE(COMMANDER, com.cannontech.roles.application.CommanderRole.CONTROL_LM_DEVICE, booleanType()),
    READ_CAP_CONTROL_DEVICE(COMMANDER, com.cannontech.roles.application.CommanderRole.READ_CAP_CONTROL_DEVICE, booleanType()),
    WRITE_TO_CAP_CONTROL_DEVICE(COMMANDER, com.cannontech.roles.application.CommanderRole.WRITE_TO_CAP_CONTROL_DEVICE, booleanType()),
    CONTROL_CAP_CONTROL_DEVICE(COMMANDER, com.cannontech.roles.application.CommanderRole.CONTROL_CAP_CONTROL_DEVICE, booleanType()),
    EXECUTE_UNKNOWN_COMMAND(COMMANDER, com.cannontech.roles.application.CommanderRole.EXECUTE_UNKNOWN_COMMAND, booleanType()),
    EXECUTE_MANUAL_COMMAND(COMMANDER, com.cannontech.roles.application.CommanderRole.EXECUTE_MANUAL_COMMAND, booleanType()),
    ENABLE_WEB_COMMANDER(COMMANDER, com.cannontech.roles.application.CommanderRole.ENABLE_WEB_COMMANDER, booleanType()),
    ENABLE_CLIENT_COMMANDER(COMMANDER, com.cannontech.roles.application.CommanderRole.ENABLE_CLIENT_COMMANDER, booleanType()),
    
    POINT_ID_EDIT(DATABASE_EDITOR, com.cannontech.roles.application.DBEditorRole.POINT_ID_EDIT, booleanType()),
    DBEDITOR_LM(DATABASE_EDITOR, com.cannontech.roles.application.DBEditorRole.DBEDITOR_LM, stringType()),
    DBEDITOR_SYSTEM(DATABASE_EDITOR, com.cannontech.roles.application.DBEditorRole.DBEDITOR_SYSTEM, stringType()),
    UTILITY_ID_RANGE(DATABASE_EDITOR, com.cannontech.roles.application.DBEditorRole.UTILITY_ID_RANGE, stringType()),
    TRANS_EXCLUSION(DATABASE_EDITOR, com.cannontech.roles.application.DBEditorRole.TRANS_EXCLUSION, stringType()),
    PERMIT_LOGIN_EDIT(DATABASE_EDITOR, com.cannontech.roles.application.DBEditorRole.PERMIT_LOGIN_EDIT, stringType()),
    ALLOW_USER_ROLES(DATABASE_EDITOR, com.cannontech.roles.application.DBEditorRole.ALLOW_USER_ROLES, stringType()),
    DATABASE_EDITOR_OPTIONAL_PRODUCT_DEV(DATABASE_EDITOR, com.cannontech.roles.application.DBEditorRole.OPTIONAL_PRODUCT_DEV, stringType()),
    ALLOW_MEMBER_PROGRAMS(DATABASE_EDITOR, com.cannontech.roles.application.DBEditorRole.ALLOW_MEMBER_PROGRAMS, stringType()),
    
    ADMIN_REPORTS_GROUP(REPORTING, com.cannontech.roles.application.ReportingRole.ADMIN_REPORTS_GROUP, booleanType()),
    AMR_REPORTS_GROUP(REPORTING, com.cannontech.roles.application.ReportingRole.AMR_REPORTS_GROUP, booleanType()),
    STATISTICAL_REPORTS_GROUP(REPORTING, com.cannontech.roles.application.ReportingRole.STATISTICAL_REPORTS_GROUP, booleanType()),
    LOAD_MANAGEMENT_REPORTS_GROUP(REPORTING, com.cannontech.roles.application.ReportingRole.LOAD_MANAGEMENT_REPORTS_GROUP, booleanType()),
    CAP_CONTROL_REPORTS_GROUP(REPORTING, com.cannontech.roles.application.ReportingRole.CAP_CONTROL_REPORTS_GROUP, booleanType()),
    DATABASE_REPORTS_GROUP(REPORTING, com.cannontech.roles.application.ReportingRole.DATABASE_REPORTS_GROUP, booleanType()),
    STARS_REPORTS_GROUP(REPORTING, com.cannontech.roles.application.ReportingRole.STARS_REPORTS_GROUP, booleanType()),
    SETTLEMENT_REPORTS_GROUP(REPORTING, com.cannontech.roles.application.ReportingRole.SETTLEMENT_REPORTS_GROUP, booleanType()),
    CI_CURTAILMENT_REPORTS_GROUP(REPORTING, com.cannontech.roles.application.ReportingRole.CI_CURTAILMENT_REPORTS_GROUP, booleanType()),
    
    LOADCONTROL_EDIT(TABULAR_DISPLAY_CONSOLE, com.cannontech.roles.application.TDCRole.LOADCONTROL_EDIT, stringType()),
    MACS_EDIT(TABULAR_DISPLAY_CONSOLE, com.cannontech.roles.application.TDCRole.MACS_EDIT, stringType()),
    TDC_EXPRESS(TABULAR_DISPLAY_CONSOLE, com.cannontech.roles.application.TDCRole.TDC_EXPRESS, stringType()),
    TDC_MAX_ROWS(TABULAR_DISPLAY_CONSOLE, com.cannontech.roles.application.TDCRole.TDC_MAX_ROWS, stringType()),
    TDC_RIGHTS(TABULAR_DISPLAY_CONSOLE, com.cannontech.roles.application.TDCRole.TDC_RIGHTS, stringType()),
    TDC_ALARM_COUNT(TABULAR_DISPLAY_CONSOLE, com.cannontech.roles.application.TDCRole.TDC_ALARM_COUNT, stringType()),
    DECIMAL_PLACES(TABULAR_DISPLAY_CONSOLE, com.cannontech.roles.application.TDCRole.DECIMAL_PLACES, stringType()),
    LC_REDUCTION_COL(TABULAR_DISPLAY_CONSOLE, com.cannontech.roles.application.TDCRole.LC_REDUCTION_COL, stringType()),
    
    GRAPH_EDIT_GRAPHDEFINITION(TRENDING, com.cannontech.roles.application.TrendingRole.GRAPH_EDIT_GRAPHDEFINITION, stringType()),
    TRENDING_DISCLAIMER(TRENDING, com.cannontech.roles.application.TrendingRole.TRENDING_DISCLAIMER, stringType()),
    SCAN_NOW_ENABLED(TRENDING, com.cannontech.roles.application.TrendingRole.SCAN_NOW_ENABLED, stringType()),
    MINIMUM_SCAN_FREQUENCY(TRENDING, com.cannontech.roles.application.TrendingRole.MINIMUM_SCAN_FREQUENCY, stringType()),
    MAXIMUM_DAILY_SCANS(TRENDING, com.cannontech.roles.application.TrendingRole.MAXIMUM_DAILY_SCANS, stringType()),
    
    HOME_URL(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.HOME_URL, stringType()),
    STYLE_SHEET(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.STYLE_SHEET, stringType()),
    NAV_BULLET_SELECTED(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.NAV_BULLET_SELECTED, stringType()),
    NAV_BULLET_EXPAND(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.NAV_BULLET_EXPAND, stringType()),
    HEADER_LOGO(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.HEADER_LOGO, stringType()),
    LOG_IN_URL(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.LOG_IN_URL, stringType()),
    NAV_CONNECTOR_BOTTOM(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.NAV_CONNECTOR_BOTTOM, stringType()),
    NAV_CONNECTOR_MIDDLE(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.NAV_CONNECTOR_MIDDLE, stringType()),
    INBOUND_VOICE_HOME_URL(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.INBOUND_VOICE_HOME_URL, stringType()),
    JAVA_WEB_START_LAUNCHER_ENABLED(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.JAVA_WEB_START_LAUNCHER_ENABLED, booleanType()),
    SUPPRESS_ERROR_PAGE_DETAILS(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.SUPPRESS_ERROR_PAGE_DETAILS, booleanType()),
    DATA_UPDATER_DELAY_MS(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.DATA_UPDATER_DELAY_MS, stringType()),
    STD_PAGE_STYLE_SHEET(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.STD_PAGE_STYLE_SHEET, stringType()),
    THEME_NAME(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.THEME_NAME, stringType()),
    VIEW_ALARMS_AS_ALERTS(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.VIEW_ALARMS_AS_ALERTS, booleanType()),
    DEFAULT_TIMEZONE(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.DEFAULT_TIMEZONE, stringType()),
    SESSION_TIMEOUT(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.SESSION_TIMEOUT, integerType()),
    CSRF_TOKEN_MODE(WEB_CLIENT, com.cannontech.roles.application.WebClientRole.CSRF_TOKEN_MODE, InputTypeFactory.enumType(CsrfTokenMode.class)),
    
    HOME_DIRECTORY(WEB_GRAPH, com.cannontech.roles.application.WebGraphRole.HOME_DIRECTORY, stringType()),
    RUN_INTERVAL(WEB_GRAPH, com.cannontech.roles.application.WebGraphRole.RUN_INTERVAL, stringType()),
    
    ESUB_EDITOR_ROLE_EXITS(APPLICATION_ESUBSTATION_EDITOR, com.cannontech.roles.application.EsubEditorRole.ESUB_EDITOR_ROLE_EXITS, booleanType()),
    
    SUB_TARGET(CBC_ONELINE_SUB_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.SUB_TARGET, stringType()),
    SUB_VARLOAD(CBC_ONELINE_SUB_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.SUB_VARLOAD, stringType()),
    SUB_EST_VARLOAD(CBC_ONELINE_SUB_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.SUB_EST_VARLOAD, stringType()),
    SUB_POWER_FACTOR(CBC_ONELINE_SUB_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.SUB_POWER_FACTOR, stringType()),
    SUB_EST_POWER_FACTOR(CBC_ONELINE_SUB_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.SUB_EST_POWER_FACTOR, stringType()),
    SUB_WATTS(CBC_ONELINE_SUB_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.SUB_WATTS, stringType()),
    SUB_VOLTS(CBC_ONELINE_SUB_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.SUB_VOLTS, stringType()),
    SUB_DAILY_MAX_OPCNT(CBC_ONELINE_SUB_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.SUB_DAILY_MAX_OPCNT, stringType()),
    SUB_TIMESTAMP(CBC_ONELINE_SUB_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.SUB_TIMESTAMP, stringType()),
    SUB_THREE_PHASE(CBC_ONELINE_SUB_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.SUB_THREE_PHASE, stringType()),
    
    FDR_KVAR(CBC_ONELINE_FEEDER_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.FDR_KVAR, stringType()),
    FDR_PF(CBC_ONELINE_FEEDER_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.FDR_PF, stringType()),
    FDR_WATT(CBC_ONELINE_FEEDER_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.FDR_WATT, stringType()),
    FDR_OP_CNT(CBC_ONELINE_FEEDER_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.FDR_OP_CNT, stringType()),
    FDR_VOLT(CBC_ONELINE_FEEDER_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.FDR_VOLT, stringType()),
    FDR_TARGET(CBC_ONELINE_FEEDER_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.FDR_TARGET, stringType()),
    FDR_TIMESTAMP(CBC_ONELINE_FEEDER_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.FDR_TIMESTAMP, stringType()),
    FDR_WATT_VOLT(CBC_ONELINE_FEEDER_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.FDR_WATT_VOLT, stringType()),
    FDR_THREE_PHASE(CBC_ONELINE_FEEDER_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.FDR_THREE_PHASE, stringType()),
    
    CAP_BANK_SIZE(CBC_ONELINE_CAP_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.CAP_BANK_SIZE, stringType()),
    CAP_CBC_NAME(CBC_ONELINE_CAP_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.CAP_CBC_NAME, stringType()),
    CAP_TIMESTAMP(CBC_ONELINE_CAP_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.CAP_TIMESTAMP, stringType()),
    CAP_BANK_FIXED_TEXT(CBC_ONELINE_CAP_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.CAP_BANK_FIXED_TEXT, stringType()),
    CAP_DAILY_MAX_TOTAL_OPCNT(CBC_ONELINE_CAP_SETTINGS, com.cannontech.roles.capcontrol.CBCOnelineSettingsRole.CAP_DAILY_MAX_TOTAL_OPCNT, stringType()),
    
    CAP_CONTROL_ACCESS(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.ACCESS, booleanType()),
    HIDE_REPORTS(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.HIDE_REPORTS, booleanType()),
    HIDE_GRAPHS(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.HIDE_GRAPHS, booleanType()),
    HIDE_ONELINE(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.HIDE_ONELINE, booleanType()),
    CAP_CONTROL_INTERFACE(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.CAP_CONTROL_INTERFACE, stringType()),
    CBC_CREATION_NAME(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.CBC_CREATION_NAME, stringType()),
    PFACTOR_DECIMAL_PLACES(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.PFACTOR_DECIMAL_PLACES, stringType()),
    CBC_ALLOW_OVUV(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.CBC_ALLOW_OVUV, booleanType()),
    CBC_DATABASE_EDIT(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.CBC_DATABASE_EDIT, booleanType()),
    SHOW_FLIP_COMMAND(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.SHOW_FLIP_COMMAND, booleanType()),
    SHOW_CB_ADDINFO(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.SHOW_CB_ADDINFO, booleanType()),
    AVAILABLE_DEFINITION(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.AVAILABLE_DEFINITION, stringType()),
    UNAVAILABLE_DEFINITION(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.UNAVAILABLE_DEFINITION, stringType()),
    TRIPPED_DEFINITION(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.TRIPPED_DEFINITION, stringType()),
    CLOSED_DEFINITION(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.CLOSED_DEFINITION, stringType()),
    ADD_COMMENTS(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.ADD_COMMENTS, booleanType()),
    MODIFY_COMMENTS(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.MODIFY_COMMENTS, booleanType()),
    SYSTEM_WIDE_CONTROLS(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.SYSTEM_WIDE_CONTROLS, booleanType()),
    FORCE_COMMENTS(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.FORCE_COMMENTS, booleanType()),
    ALLOW_AREA_CONTROLS(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.ALLOW_AREA_CONTROLS, booleanType()),
    ALLOW_SUBSTATION_CONTROLS(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.ALLOW_SUBSTATION_CONTROLS, booleanType()),
    ALLOW_SUBBUS_CONTROLS(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.ALLOW_SUBBUS_CONTROLS, booleanType()),
    ALLOW_FEEDER_CONTROLS(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.ALLOW_FEEDER_CONTROLS, booleanType()),
    ALLOW_CAPBANK_CONTROLS(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.ALLOW_CAPBANK_CONTROLS, booleanType()),
    CONTROL_WARNING(CBC_SETTINGS, com.cannontech.roles.capcontrol.CBCSettingsRole.CONTROL_WARNING, booleanType()),
    
    RESIDENTIAL_CONSUMER_INFO_ACCOUNT_GENERAL(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.CONSUMER_INFO_ACCOUNT_GENERAL, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_PROGRAMS_ENROLLMENT(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_PROGRAMS_OPT_OUT(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_HARDWARES_THERMOSTAT(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.CONSUMER_INFO_HARDWARES_THERMOSTAT, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_QUESTIONS_UTIL(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_UTIL, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_QUESTIONS_FAQ(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_FAQ, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_USERNAME(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.CONSUMER_INFO_CHANGE_LOGIN_USERNAME, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_THERMOSTATS_ALL(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.CONSUMER_INFO_THERMOSTATS_ALL, booleanType()),
    RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_PASSWORD(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.CONSUMER_INFO_CHANGE_LOGIN_PASSWORD, booleanType()),
    RESIDENTIAL_HIDE_OPT_OUT_BOX(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.HIDE_OPT_OUT_BOX, stringType()),
    RESIDENTIAL_OPT_OUT_PERIOD(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.OPT_OUT_PERIOD, stringType()),
    RESIDENTIAL_OPT_OUT_LIMITS(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.OPT_OUT_LIMITS, stringType()),
    RESIDENTIAL_WEB_LINK_FAQ(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.WEB_LINK_FAQ, stringType()),
    RESIDENTIAL_WEB_LINK_THERM_INSTRUCTIONS(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.WEB_LINK_THERM_INSTRUCTIONS, stringType()),
    RESIDENTIAL_CONTACTS_ACCESS(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.CONTACTS_ACCESS, booleanType()),
    RESIDENTIAL_OPT_OUT_TODAY_ONLY(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.OPT_OUT_TODAY_ONLY, booleanType()),
    RESIDENTIAL_SIGN_OUT_ENABLED(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.SIGN_OUT_ENABLED, booleanType()),
    RESIDENTIAL_CREATE_LOGIN_FOR_ACCOUNT(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.CREATE_LOGIN_FOR_ACCOUNT, booleanType()),
    RESIDENTIAL_OPT_OUT_DEVICE_SELECTION(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.OPT_OUT_DEVICE_SELECTION, booleanType()),
    RESIDENTIAL_ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY, booleanType()),
    RESIDENTIAL_ENROLLMENT_PER_DEVICE(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.ENROLLMENT_PER_DEVICE, booleanType()),
    RESIDENTIAL_THERMOSTAT_SCHEDULE_5_2(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.THERMOSTAT_SCHEDULE_5_2, booleanType()),
    RESIDENTIAL_THERMOSTAT_SCHEDULE_7(RESIDENTIAL_CUSTOMER, com.cannontech.roles.consumer.ResidentialCustomerRole.THERMOSTAT_SCHEDULE_7, booleanType()),
    
    LM_INDIVIDUAL_SWITCH(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.INDIVIDUAL_SWITCH, booleanType()),
    DEMAND_RESPONSE(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.DEMAND_RESPONSE, booleanType()),
    DIRECT_CONTROL(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.DIRECT_CONTROL, booleanType()),
    ALLOW_CHECK_CONSTRAINTS(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.ALLOW_CHECK_CONSTRAINTS, booleanType()),
    ALLOW_OBSERVE_CONSTRAINTS(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.ALLOW_OBSERVE_CONSTRAINTS, booleanType()),
    ALLOW_OVERRIDE_CONSTRAINT(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.ALLOW_OVERRIDE_CONSTRAINT, booleanType()),
    DEFAULT_CONSTRAINT_SELECTION(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.DEFAULT_CONSTRAINT_SELECTION, stringType()),
    ALLOW_STOP_GEAR_ACCESS(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.ALLOW_STOP_GEAR_ACCESS, booleanType()),
    IGNORE_PER_PAO_PERMISSIONS(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.IGNORE_PER_PAO_PERMISSIONS, booleanType()),
    SHOW_CONTROL_AREAS(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.SHOW_CONTROL_AREAS, booleanType()),
    SHOW_SCENARIOS(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.SHOW_SCENARIOS, booleanType()),
    START_NOW_CHECKED_BY_DEFAULT(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.START_NOW_CHECKED_BY_DEFAULT, booleanType()),
    CONTROL_DURATION_DEFAULT(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.CONTROL_DURATION_DEFAULT, integerType()),
    SCHEDULE_STOP_CHECKED_BY_DEFAULT(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.SCHEDULE_STOP_CHECKED_BY_DEFAULT, booleanType()),
    START_TIME_DEFAULT(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.START_TIME_DEFAULT, stringType()),
    
    CONTROL_AREA_STATE(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.CONTROL_AREA_STATE, booleanType()),
    CONTROL_AREA_VALUE_THRESHOLD(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.CONTROL_AREA_VALUE_THRESHOLD, booleanType()),
    CONTROL_AREA_PEAK_PROJECTION(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.CONTROL_AREA_PEAK_PROJECTION, booleanType()),
    CONTROL_AREA_ATKU(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.CONTROL_AREA_ATKU, booleanType()),
    CONTROL_AREA_PRIORITY(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.CONTROL_AREA_PRIORITY, booleanType()),
    CONTROL_AREA_TIME_WINDOW(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.CONTROL_AREA_TIME_WINDOW, booleanType()),
    CONTROL_AREA_LOAD_CAPACITY(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.CONTROL_AREA_LOAD_CAPACITY, booleanType()),

    PROGRAM_STATE(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.PROGRAM_STATE, booleanType()),
    PROGRAM_START(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.PROGRAM_START, booleanType()),
    PROGRAM_STOP(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.PROGRAM_STOP, booleanType()),
    PROGRAM_CURRENT_GEAR(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.PROGRAM_CURRENT_GEAR, booleanType()),
    PROGRAM_PRIORITY(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.PROGRAM_PRIORITY, booleanType()),
    PROGRAM_REDUCTION(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.PROGRAM_REDUCTION, booleanType()),
    PROGRAM_LOAD_CAPACITY(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.PROGRAM_LOAD_CAPACITY, booleanType()),

    LOAD_GROUP_STATE(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.LOAD_GROUP_STATE, booleanType()),
    LOAD_GROUP_LAST_ACTION(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.LOAD_GROUP_LAST_ACTION, booleanType()),
    LOAD_GROUP_CONTROL_STATISTICS(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.LOAD_GROUP_CONTROL_STATISTICS, booleanType()),
    LOAD_GROUP_REDUCTION(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.LOAD_GROUP_REDUCTION, booleanType()),
    LOAD_GROUP_LOAD_CAPACITY(LM_DIRECT_LOADCONTROL, com.cannontech.roles.loadcontrol.DirectLoadcontrolRole.LOAD_GROUP_LOAD_CAPACITY, booleanType()),
    
    NUMBER_OF_CHANNELS(IVR, com.cannontech.roles.notifications.IvrRole.NUMBER_OF_CHANNELS, integerType()),
    IVR_URL_DIALER_TEMPLATE(IVR, com.cannontech.roles.notifications.IvrRole.IVR_URL_DIALER_TEMPLATE, stringType()),
    IVR_URL_DIALER_SUCCESS_MATCHER(IVR, com.cannontech.roles.notifications.IvrRole.IVR_URL_DIALER_SUCCESS_MATCHER, stringType()),
    
    TEMPLATE_ROOT(NOTIFICATION_CONFIGURATION, com.cannontech.roles.notifications.NotificationConfigurationRole.TEMPLATE_ROOT, stringType()),
    
    ADMIN_SUPER_USER(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_SUPER_USER, booleanType()),
    ADMIN_EDIT_ENERGY_COMPANY(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_EDIT_ENERGY_COMPANY, booleanType()),
    ADMIN_CREATE_DELETE_ENERGY_COMPANY(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_CREATE_DELETE_ENERGY_COMPANY, booleanType()),
    ADMIN_MANAGE_MEMBERS(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_MANAGE_MEMBERS, booleanType()),
    ADMIN_VIEW_BATCH_COMMANDS(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_VIEW_BATCH_COMMANDS, booleanType()),
    ADMIN_VIEW_OPT_OUT_EVENTS(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_VIEW_OPT_OUT_EVENTS, booleanType()),
    ADMIN_MEMBER_LOGIN_CNTRL(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_MEMBER_LOGIN_CNTRL, booleanType()),
    ADMIN_MEMBER_ROUTE_SELECT(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_MEMBER_ROUTE_SELECT, booleanType()),
    
    ADMIN_MULTI_WAREHOUSE(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_MULTI_WAREHOUSE, booleanType()),
    ADMIN_AUTO_PROCESS_BATCH_COMMANDS(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_AUTO_PROCESS_BATCH_COMMANDS, stringType()),
    ADMIN_MULTISPEAK_SETUP(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_MULTISPEAK_SETUP, booleanType()),
    ADMIN_LM_USER_ASSIGN(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_LM_USER_ASSIGN, booleanType()),
    ADMIN_EDIT_CONFIG(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_EDIT_CONFIG, booleanType()),
    ADMIN_VIEW_CONFIG(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_VIEW_CONFIG, booleanType()),
    ADMIN_MANAGE_INDEXES(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_MANAGE_INDEXES, booleanType()),
    ADMIN_VIEW_LOGS(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_VIEW_LOGS, booleanType()),
    ADMIN_DATABASE_MIGRATION(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_DATABASE_MIGRATION, booleanType()),
    ADMIN_EVENT_LOGS(OPERATOR_ADMINISTRATOR, com.cannontech.roles.operator.AdministratorRole.ADMIN_EVENT_LOGS, booleanType()),
    
    CURTAILMENT_LABEL(CI_CURTAILMENT, com.cannontech.roles.operator.CICurtailmentRole.CURTAILMENT_LABEL, stringType()),

    OPERATOR_CONSUMER_INFO_ACCOUNT_RESIDENCE(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_RESIDENCE, booleanType()),
    OPERATOR_CONSUMER_INFO_ACCOUNT_CALL_TRACKING(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_CALL_TRACKING, booleanType()),
    OPERATOR_CONSUMER_INFO_METERING_INTERVAL_DATA(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_METERING_INTERVAL_DATA, booleanType()),
    OPERATOR_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY, booleanType()),
    OPERATOR_CONSUMER_INFO_PROGRAMS_ENROLLMENT(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT, booleanType()),
    OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT, booleanType()),
    OPERATOR_CONSUMER_INFO_APPLIANCES(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_APPLIANCES, booleanType()),
    OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_APPLIANCES_CREATE, booleanType()),
    OPERATOR_CONSUMER_INFO_HARDWARES(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_HARDWARES, booleanType()),
    OPERATOR_CONSUMER_INFO_HARDWARES_CREATE(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_HARDWARES_CREATE, booleanType()),
    OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_HARDWARES_THERMOSTAT, booleanType()),
    OPERATOR_CONSUMER_INFO_WORK_ORDERS(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_WORK_ORDERS, booleanType()),
    OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_WS_LM_DATA_ACCESS, booleanType()),
    OPERATOR_CONSUMER_INFO_WS_LM_CONTROL_ACCESS(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_WS_LM_CONTROL_ACCESS, booleanType()),
    OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME, booleanType()),
    OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD, booleanType()),
    OPERATOR_CREATE_LOGIN_FOR_ACCOUNT(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CREATE_LOGIN_FOR_ACCOUNT, booleanType()),
    OPERATOR_CONSUMER_INFO_ADMIN_FAQ(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_ADMIN_FAQ, booleanType()),
    OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_THERMOSTATS_ALL, booleanType()),
    OPERATOR_CONSUMER_INFO_METERING_CREATE(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CONSUMER_INFO_METERING_CREATE, booleanType()),
    OPERATOR_NEW_ACCOUNT_WIZARD(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.NEW_ACCOUNT_WIZARD, booleanType()),
    OPERATOR_IMPORT_CUSTOMER_ACCOUNT(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.IMPORT_CUSTOMER_ACCOUNT, stringType()),
    OPERATOR_INVENTORY_CHECKING(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.INVENTORY_CHECKING, booleanType()),
    OPERATOR_ORDER_NUMBER_AUTO_GEN(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.ORDER_NUMBER_AUTO_GEN, booleanType()),
    OPERATOR_CALL_NUMBER_AUTO_GEN(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.CALL_NUMBER_AUTO_GEN, stringType()),
    OPERATOR_OPT_OUT_PERIOD(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.OPT_OUT_PERIOD, stringType()),
    OPERATOR_DISABLE_SWITCH_SENDING(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.DISABLE_SWITCH_SENDING, booleanType()),
    OPERATOR_METER_SWITCH_ASSIGNMENT(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.METER_SWITCH_ASSIGNMENT, stringType()),
    OPERATOR_ALLOW_ACCOUNT_EDITING(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.ALLOW_ACCOUNT_EDITING, booleanType()),
    OPERATOR_WEB_LINK_FAQ(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.WEB_LINK_FAQ, stringType()),
    OPERATOR_WEB_LINK_THERM_INSTRUCTIONS(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.WEB_LINK_THERM_INSTRUCTIONS, stringType()),
    OPERATOR_INVENTORY_CHECKING_CREATE(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.INVENTORY_CHECKING_CREATE, booleanType()),
    OPERATOR_OPT_OUT_TODAY_ONLY(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.OPT_OUT_TODAY_ONLY, booleanType()),
    OPERATOR_OPT_OUT_ADMIN_STATUS(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.OPT_OUT_ADMIN_STATUS, booleanType()),
    OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_ENABLE, booleanType()),
    OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.OPT_OUT_ADMIN_CANCEL_CURRENT, booleanType()),
    OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_COUNTS, booleanType()),
    OPERATOR_THERMOSTAT_SCHEDULE_5_2(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.THERMOSTAT_SCHEDULE_5_2, booleanType()),
    OPERATOR_THERMOSTAT_SCHEDULE_7(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.THERMOSTAT_SCHEDULE_7, booleanType()),
    OPERATOR_ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY, booleanType()),
    OPERATOR_ACCOUNT_SEARCH(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.ACCOUNT_SEARCH, booleanType()),
    OPERATOR_SURVEY_EDIT(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.SURVEY_EDIT, booleanType()),
    OPERATOR_OPT_OUT_SURVEY_EDIT(CONSUMER_INFO, com.cannontech.roles.operator.ConsumerInfoRole.OPT_OUT_SURVEY_EDIT, booleanType()),

    BULK_IMPORT_OPERATION(DEVICE_ACTIONS, com.cannontech.roles.operator.DeviceActionsRole.BULK_IMPORT_OPERATION, booleanType()),
    BULK_UPDATE_OPERATION(DEVICE_ACTIONS, com.cannontech.roles.operator.DeviceActionsRole.BULK_UPDATE_OPERATION, booleanType()),
    DEVICE_GROUP_EDIT(DEVICE_ACTIONS, com.cannontech.roles.operator.DeviceActionsRole.DEVICE_GROUP_EDIT, booleanType()),
    DEVICE_GROUP_MODIFY(DEVICE_ACTIONS, com.cannontech.roles.operator.DeviceActionsRole.DEVICE_GROUP_MODIFY, booleanType()),
    GROUP_COMMANDER(DEVICE_ACTIONS, com.cannontech.roles.operator.DeviceActionsRole.GROUP_COMMANDER, booleanType()),
    MASS_CHANGE(DEVICE_ACTIONS, com.cannontech.roles.operator.DeviceActionsRole.MASS_CHANGE, booleanType()),
    LOCATE_ROUTE(DEVICE_ACTIONS, com.cannontech.roles.operator.DeviceActionsRole.LOCATE_ROUTE, booleanType()),
    MASS_DELETE(DEVICE_ACTIONS, com.cannontech.roles.operator.DeviceActionsRole.MASS_DELETE, booleanType()),
    ADD_REMOVE_POINTS(DEVICE_ACTIONS, com.cannontech.roles.operator.DeviceActionsRole.ADD_REMOVE_POINTS, booleanType()),
    ASSIGN_CONFIG(DEVICE_ACTIONS, com.cannontech.roles.operator.DeviceActionsRole.ASSIGN_CONFIG, booleanType()),
    SEND_READ_CONFIG(DEVICE_ACTIONS, com.cannontech.roles.operator.DeviceActionsRole.SEND_READ_CONFIG, booleanType()),
    
    OPERATOR_ESUBSTATION_DRAWINGS_VIEW(OPERATOR_ESUBSTATION_DRAWINGS, com.cannontech.roles.operator.EsubDrawingsRole.VIEW, booleanType()),
    OPERATOR_ESUBSTATION_DRAWINGS_EDIT(OPERATOR_ESUBSTATION_DRAWINGS, com.cannontech.roles.operator.EsubDrawingsRole.EDIT, booleanType()),
    OPERATOR_ESUBSTATION_DRAWINGS_CONTROL(OPERATOR_ESUBSTATION_DRAWINGS, com.cannontech.roles.operator.EsubDrawingsRole.CONTROL, booleanType()),
    OPERATOR_ESUBSTATION_DRAWINGS_HOME_URL(OPERATOR_ESUBSTATION_DRAWINGS, com.cannontech.roles.operator.EsubDrawingsRole.ESUB_HOME_URL, stringType()),
    
    INVENTORY_SHOW_ALL(INVENTORY, com.cannontech.roles.operator.InventoryRole.INVENTORY_SHOW_ALL, booleanType()),
    SN_ADD_RANGE(INVENTORY, com.cannontech.roles.operator.InventoryRole.SN_ADD_RANGE, stringType()),
    SN_UPDATE_RANGE(INVENTORY, com.cannontech.roles.operator.InventoryRole.SN_UPDATE_RANGE, booleanType()),
    SN_CONFIG_RANGE(INVENTORY, com.cannontech.roles.operator.InventoryRole.SN_CONFIG_RANGE, booleanType()),
    SN_DELETE_RANGE(INVENTORY, com.cannontech.roles.operator.InventoryRole.SN_DELETE_RANGE, booleanType()),
    INVENTORY_CREATE_HARDWARE(INVENTORY, com.cannontech.roles.operator.InventoryRole.INVENTORY_CREATE_HARDWARE, stringType()),
    EXPRESSCOM_TOOS_RESTORE_FIRST(INVENTORY, com.cannontech.roles.operator.InventoryRole.EXPRESSCOM_TOOS_RESTORE_FIRST, booleanType()),
    ALLOW_MULTIPLE_WAREHOUSES(INVENTORY, com.cannontech.roles.operator.InventoryRole.ALLOW_MULTIPLE_WAREHOUSES, stringType()),
    PURCHASING_ACCESS(INVENTORY, com.cannontech.roles.operator.InventoryRole.PURCHASING_ACCESS, booleanType()),
    DEVICE_RECONFIG(INVENTORY, com.cannontech.roles.operator.InventoryRole.DEVICE_RECONFIG, booleanType()),
    INVENTORY_SEARCH(INVENTORY, com.cannontech.roles.operator.InventoryRole.INVENTORY_SEARCH, booleanType()),
    
    IMPORTER_ENABLED(METERING, com.cannontech.roles.operator.MeteringRole.IMPORTER_ENABLED, booleanType()),
    PROFILE_COLLECTION(METERING, com.cannontech.roles.operator.MeteringRole.PROFILE_COLLECTION, booleanType()),
    MOVE_IN_MOVE_OUT_AUTO_ARCHIVING(METERING, com.cannontech.roles.operator.MeteringRole.MOVE_IN_MOVE_OUT_AUTO_ARCHIVING, booleanType()),
    MOVE_IN_MOVE_OUT(METERING, com.cannontech.roles.operator.MeteringRole.MOVE_IN_MOVE_OUT, booleanType()),
    PROFILE_COLLECTION_SCANNING(METERING, com.cannontech.roles.operator.MeteringRole.PROFILE_COLLECTION_SCANNING, booleanType()),
    HIGH_BILL_COMPLAINT(METERING, com.cannontech.roles.operator.MeteringRole.HIGH_BILL_COMPLAINT, booleanType()),
    CIS_DETAIL_WIDGET_ENABLED(METERING, com.cannontech.roles.operator.MeteringRole.CIS_DETAIL_WIDGET_ENABLED, booleanType()),
    CIS_DETAIL_TYPE(METERING, com.cannontech.roles.operator.MeteringRole.CIS_DETAIL_TYPE, InputTypeFactory.enumType(CisDetailRolePropertyEnum.class)),
    OUTAGE_PROCESSING(METERING, com.cannontech.roles.operator.MeteringRole.OUTAGE_PROCESSING, booleanType()),
    TAMPER_FLAG_PROCESSING(METERING, com.cannontech.roles.operator.MeteringRole.TAMPER_FLAG_PROCESSING, booleanType()),
    PHASE_DETECT(METERING, com.cannontech.roles.operator.MeteringRole.PHASE_DETECT, booleanType()),
    VALIDATION_ENGINE(METERING, com.cannontech.roles.operator.MeteringRole.VALIDATION_ENGINE, booleanType()),
    STATUS_POINT_MONITORING(METERING, com.cannontech.roles.operator.MeteringRole.STATUS_POINT_MONITORING, booleanType()),
    PORTER_RESPONSE_MONITORING(METERING, com.cannontech.roles.operator.MeteringRole.PORTER_RESPONSE_MONITORING, booleanType()),
    
    ODDS_FOR_CONTROL_LABEL(ODDS_FOR_CONTROL, com.cannontech.roles.operator.OddsForControlRole.ODDS_FOR_CONTROL_LABEL, stringType()),
    
    ENABLE_DISABLE_SCRIPTS(SCHEDULER, com.cannontech.roles.operator.SchedulerRole.ENABLE_DISABLE_SCRIPTS, booleanType()),
    MANAGE_SCHEDULES(SCHEDULER, com.cannontech.roles.operator.SchedulerRole.MANAGE_SCHEDULES, booleanType()),
    
    WORK_ORDER_SHOW_ALL(WORK_ORDER, com.cannontech.roles.operator.WorkOrderRole.WORK_ORDER_SHOW_ALL, booleanType()),
    WORK_ORDER_CREATE_NEW(WORK_ORDER, com.cannontech.roles.operator.WorkOrderRole.WORK_ORDER_CREATE_NEW, stringType()),
    WORK_ORDER_REPORT(WORK_ORDER, com.cannontech.roles.operator.WorkOrderRole.WORK_ORDER_REPORT, stringType()),
    ADDTL_ORDER_NUMBER_LABEL(WORK_ORDER, com.cannontech.roles.operator.WorkOrderRole.ADDTL_ORDER_NUMBER_LABEL, stringType()),
    
    SERVER_ADDRESS(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.SERVER_ADDRESS, stringType()),
    AUTH_PORT(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.AUTH_PORT, stringType()),
    ACCT_PORT(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.ACCT_PORT, stringType()),
    SECRET_KEY(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.SECRET_KEY, stringType()),
    AUTH_METHOD(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.AUTH_METHOD, stringType()),
    AUTHENTICATION_MODE(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.AUTHENTICATION_MODE, stringType()),
    AUTH_TIMEOUT(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.AUTH_TIMEOUT, stringType()),
    DEFAULT_AUTH_TYPE(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.DEFAULT_AUTH_TYPE, InputTypeFactory.enumType(AuthType.class)),
    LDAP_DN(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.LDAP_DN, stringType()),
    LDAP_USER_SUFFIX(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.LDAP_USER_SUFFIX, stringType()),
    LDAP_USER_PREFIX(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.LDAP_USER_PREFIX, stringType()),
    LDAP_SERVER_ADDRESS(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.LDAP_SERVER_ADDRESS, stringType()),
    LDAP_SERVER_PORT(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.LDAP_SERVER_PORT, stringType()),
    LDAP_SERVER_TIMEOUT(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.LDAP_SERVER_TIMEOUT, stringType()),
    AD_SERVER_ADDRESS(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.AD_SERVER_ADDRESS, stringType()),
    AD_SERVER_PORT(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.AD_SERVER_PORT, stringType()),
    AD_SERVER_TIMEOUT(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.AD_SERVER_TIMEOUT, stringType()),
    AD_NTDOMAIN(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.AD_NTDOMAIN, stringType()),
    ENABLE_PASSWORD_RECOVERY(AUTHENTICATION, com.cannontech.roles.yukon.AuthenticationRole.ENABLE_PASSWORD_RECOVERY, booleanType()),
    
    WIZ_ACTIVATE(SYSTEM_BILLING, com.cannontech.roles.yukon.BillingRole.WIZ_ACTIVATE, stringType()),
    INPUT_FILE(SYSTEM_BILLING, com.cannontech.roles.yukon.BillingRole.INPUT_FILE, stringType()),
    DEFAULT_BILLING_FORMAT(SYSTEM_BILLING, com.cannontech.roles.yukon.BillingRole.DEFAULT_BILLING_FORMAT, stringType()),
    DEMAND_DAYS_PREVIOUS(SYSTEM_BILLING, com.cannontech.roles.yukon.BillingRole.DEMAND_DAYS_PREVIOUS, stringType()),
    ENERGY_DAYS_PREVIOUS(SYSTEM_BILLING, com.cannontech.roles.yukon.BillingRole.ENERGY_DAYS_PREVIOUS, stringType()),
    APPEND_TO_FILE(SYSTEM_BILLING, com.cannontech.roles.yukon.BillingRole.APPEND_TO_FILE, stringType()),
    REMOVE_MULTIPLIER(SYSTEM_BILLING, com.cannontech.roles.yukon.BillingRole.REMOVE_MULTIPLIER, stringType()),
    COOP_ID_CADP_ONLY(SYSTEM_BILLING, com.cannontech.roles.yukon.BillingRole.COOP_ID_CADP_ONLY, stringType()),
    DEFAULT_ROUNDING_MODE(SYSTEM_BILLING, com.cannontech.roles.yukon.BillingRole.DEFAULT_ROUNDING_MODE, InputTypeFactory.enumType(RoundingMode.class)),
    
    DEVICE_DISPLAY_TEMPLATE(SYSTEM_CONFIGURATION, com.cannontech.roles.yukon.ConfigurationRole.DEVICE_DISPLAY_TEMPLATE, InputTypeFactory.enumType(MeterDisplayFieldEnum.class)),
    ALERT_TIMEOUT_HOURS(SYSTEM_CONFIGURATION, com.cannontech.roles.yukon.ConfigurationRole.ALERT_TIMEOUT_HOURS, stringType()),
    CUSTOMER_INFO_IMPORTER_FILE_LOCATION(SYSTEM_CONFIGURATION, com.cannontech.roles.yukon.ConfigurationRole.CUSTOMER_INFO_IMPORTER_FILE_LOCATION, stringType()),
    SYSTEM_TIMEZONE(SYSTEM_CONFIGURATION, com.cannontech.roles.yukon.ConfigurationRole.SYSTEM_TIMEZONE, stringType()),
    OPT_OUTS_COUNT(SYSTEM_CONFIGURATION, com.cannontech.roles.yukon.ConfigurationRole.OPT_OUTS_COUNT, booleanType()),
    DATABASE_MIGRATION_FILE_LOCATION(SYSTEM_CONFIGURATION, com.cannontech.roles.yukon.ConfigurationRole.DATABASE_MIGRATION_FILE_LOCATION, stringType()),
    
    ADMIN_EMAIL_ADDRESS(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.ADMIN_EMAIL_ADDRESS, stringType()),
    OPTOUT_NOTIFICATION_RECIPIENTS(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS, stringType()),
    ENERGY_COMPANY_DEFAULT_TIME_ZONE(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.DEFAULT_TIME_ZONE, stringType()),
    CUSTOMER_GROUP_IDS(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.CUSTOMER_GROUP_IDS, stringType()),
    OPERATOR_GROUP_IDS(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.OPERATOR_GROUP_IDS, stringType()),
    TRACK_HARDWARE_ADDRESSING(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING, booleanType()),
    SINGLE_ENERGY_COMPANY(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.SINGLE_ENERGY_COMPANY, booleanType()),
    OPTIONAL_PRODUCT_DEV(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.OPTIONAL_PRODUCT_DEV, stringType()),
    DEFAULT_TEMPERATURE_UNIT(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.DEFAULT_TEMPERATURE_UNIT, stringType()),
    METER_MCT_BASE_DESIGNATION(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.METER_MCT_BASE_DESIGNATION, stringType()),
    APPLICABLE_POINT_TYPE_KEY(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.APPLICABLE_POINT_TYPE_KEY, stringType()),
    INHERIT_PARENT_APP_CATS(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.INHERIT_PARENT_APP_CATS, booleanType()),
    AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS, booleanType()),
    ACCOUNT_NUMBER_LENGTH(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.ACCOUNT_NUMBER_LENGTH, integerType()),
    ROTATION_DIGIT_LENGTH(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.ROTATION_DIGIT_LENGTH, integerType()),
    SERIAL_NUMBER_VALIDATION(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.SERIAL_NUMBER_VALIDATION, InputTypeFactory.enumType(SerialNumberValidation.class)),
    AUTOMATIC_CONFIGURATION(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.AUTOMATIC_CONFIGURATION, booleanType()),
    ADMIN_ALLOW_DESIGNATION_CODES(ENERGY_COMPANY, com.cannontech.roles.yukon.EnergyCompanyRole.ALLOW_DESIGNATION_CODES, booleanType()),
    
    MSP_PAONAME_ALIAS(MULTISPEAK, com.cannontech.roles.yukon.MultispeakRole.MSP_PAONAME_ALIAS, InputTypeFactory.enumType(MspPaoNameAliasEnum.class)),
    MSP_PRIMARY_CB_VENDORID(MULTISPEAK, com.cannontech.roles.yukon.MultispeakRole.MSP_PRIMARY_CB_VENDORID, integerType()),
    MSP_BILLING_CYCLE_PARENT_DEVICEGROUP(MULTISPEAK, com.cannontech.roles.yukon.MultispeakRole.MSP_BILLING_CYCLE_PARENT_DEVICEGROUP, stringType()),
    MSP_LM_MAPPING_SETUP(MULTISPEAK, com.cannontech.roles.yukon.MultispeakRole.MSP_LM_MAPPING_SETUP, booleanType()),
    MSP_METER_LOOKUP_FIELD(MULTISPEAK, com.cannontech.roles.yukon.MultispeakRole.MSP_METER_LOOKUP_FIELD, InputTypeFactory.enumType(MultispeakMeterLookupFieldEnum.class)),
    MSP_PAONAME_EXTENSION(MULTISPEAK, com.cannontech.roles.yukon.MultispeakRole.MSP_PAONAME_EXTENSION, stringType()),
    
    DISPATCH_MACHINE(SYSTEM, com.cannontech.roles.yukon.SystemRole.DISPATCH_MACHINE, stringType()),
    DISPATCH_PORT(SYSTEM, com.cannontech.roles.yukon.SystemRole.DISPATCH_PORT, integerType()),
    PORTER_MACHINE(SYSTEM, com.cannontech.roles.yukon.SystemRole.PORTER_MACHINE, stringType()),
    PORTER_PORT(SYSTEM, com.cannontech.roles.yukon.SystemRole.PORTER_PORT, integerType()),
    MACS_MACHINE(SYSTEM, com.cannontech.roles.yukon.SystemRole.MACS_MACHINE, stringType()),
    MACS_PORT(SYSTEM, com.cannontech.roles.yukon.SystemRole.MACS_PORT, integerType()),
    CAP_CONTROL_MACHINE(SYSTEM, com.cannontech.roles.yukon.SystemRole.CAP_CONTROL_MACHINE, stringType()),
    CAP_CONTROL_PORT(SYSTEM, com.cannontech.roles.yukon.SystemRole.CAP_CONTROL_PORT, integerType()),
    LOADCONTROL_MACHINE(SYSTEM, com.cannontech.roles.yukon.SystemRole.LOADCONTROL_MACHINE, stringType()),
    LOADCONTROL_PORT(SYSTEM, com.cannontech.roles.yukon.SystemRole.LOADCONTROL_PORT, integerType()),
    SMTP_HOST(SYSTEM, com.cannontech.roles.yukon.SystemRole.SMTP_HOST, stringType()),
    MAIL_FROM_ADDRESS(SYSTEM, com.cannontech.roles.yukon.SystemRole.MAIL_FROM_ADDRESS, stringType()),
    STARS_PRELOAD_DATA(SYSTEM, com.cannontech.roles.yukon.SystemRole.STARS_PRELOAD_DATA, stringType()),
    WEB_LOGO_URL(SYSTEM, com.cannontech.roles.yukon.SystemRole.WEB_LOGO_URL, stringType()),
    NOTIFICATION_HOST(SYSTEM, com.cannontech.roles.yukon.SystemRole.NOTIFICATION_HOST, stringType()),
    NOTIFICATION_PORT(SYSTEM, com.cannontech.roles.yukon.SystemRole.NOTIFICATION_PORT, integerType()),
    BATCHED_SWITCH_COMMAND_TOGGLE(SYSTEM, com.cannontech.roles.yukon.SystemRole.BATCHED_SWITCH_COMMAND_TOGGLE, stringType()),
    STARS_ACTIVATION(SYSTEM, com.cannontech.roles.yukon.SystemRole.STARS_ACTIVATION, stringType()),
    BULK_IMPORTER_COMMUNICATIONS_ENABLED(SYSTEM, com.cannontech.roles.yukon.SystemRole.BULK_IMPORTER_COMMUNICATIONS_ENABLED, stringType()),

    CALL_RESPONSE_TIMEOUT(VOICE_SERVER, com.cannontech.roles.yukon.VoiceServerRole.CALL_RESPONSE_TIMEOUT, integerType()),
    CALL_PREFIX(VOICE_SERVER, com.cannontech.roles.yukon.VoiceServerRole.CALL_PREFIX, stringType()),
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

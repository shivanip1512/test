package com.cannontech.web.support.development.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonGroup;

public class DevRolePropUpdaterService extends DevObjectCreationBase {
    @Autowired private YukonGroupDao yukonGroupDao;

    @Override
    protected void createAll() {
        // feel free to make this sys admin group thing below better
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroupByName("System Administrator Grp");
        updateAllRolePropertiesForGroup(group);
    }

    @Override
    protected void logFinalExecutionDetails() {
        log.info("Finished setting role propertys for System Administrator Grp");
    }

    private void updateAllRolePropertiesForGroup(LiteYukonGroup group) {

        // ODDS_FOR_CONTROL
        setRoleProperty(group, YukonRoleProperty.ODDS_FOR_CONTROL_LABEL, "Odds for Control");

        // CI_CURTAILMENT
        setRoleProperty(group, YukonRoleProperty.CURTAILMENT_LABEL, "CI Curtailment");

        // Demand Response
        setRoleProperty(group, YukonRoleProperty.DEMAND_RESPONSE,true);

        // APPLICATION_BILLING
        setRoleProperty(group, YukonRoleProperty.DYNAMIC_BILLING_FILE_SETUP,true);

        // COMMANDER
        setRoleProperty(group, YukonRoleProperty.VERSACOM_SERIAL_MODEL,true);
        setRoleProperty(group, YukonRoleProperty.EXPRESSCOM_SERIAL_MODEL,true);
        setRoleProperty(group, YukonRoleProperty.DCU_SA205_SERIAL_MODEL,true);
        setRoleProperty(group, YukonRoleProperty.DCU_SA305_SERIAL_MODEL,true);
        setRoleProperty(group, YukonRoleProperty.READ_DEVICE,true);
        setRoleProperty(group, YukonRoleProperty.WRITE_TO_DEVICE,true);
        setRoleProperty(group, YukonRoleProperty.CONTROL_DEVICE,true);
        setRoleProperty(group, YukonRoleProperty.READ_LM_DEVICE,true);
        setRoleProperty(group, YukonRoleProperty.WRITE_TO_LM_DEVICE,true);
        setRoleProperty(group, YukonRoleProperty.CONTROL_LM_DEVICE,true);
        setRoleProperty(group, YukonRoleProperty.READ_CAP_CONTROL_DEVICE,true);
        setRoleProperty(group, YukonRoleProperty.WRITE_TO_CAP_CONTROL_DEVICE,true);
        setRoleProperty(group, YukonRoleProperty.CONTROL_CAP_CONTROL_DEVICE,true);
        setRoleProperty(group, YukonRoleProperty.EXECUTE_UNKNOWN_COMMAND,true);
        setRoleProperty(group, YukonRoleProperty.EXECUTE_MANUAL_COMMAND,true);
        setRoleProperty(group, YukonRoleProperty.ENABLE_WEB_COMMANDER,true);
        setRoleProperty(group, YukonRoleProperty.ENABLE_CLIENT_COMMANDER,true);

        // DATABASE_EDITOR
        setRoleProperty(group, YukonRoleProperty.POINT_ID_EDIT,true);
        setRoleProperty(group, YukonRoleProperty.DBEDITOR_LM,true);
        setRoleProperty(group, YukonRoleProperty.DBEDITOR_SYSTEM,true);
        setRoleProperty(group, YukonRoleProperty.PERMIT_LOGIN_EDIT,true);
        setRoleProperty(group, YukonRoleProperty.ALLOW_MEMBER_PROGRAMS,true);

        // REPORTING
        setRoleProperty(group, YukonRoleProperty.ADMIN_REPORTS_GROUP,true);
        setRoleProperty(group, YukonRoleProperty.AMR_REPORTS_GROUP,true);
        setRoleProperty(group, YukonRoleProperty.STATISTICAL_REPORTS_GROUP,true);
        setRoleProperty(group, YukonRoleProperty.LOAD_MANAGEMENT_REPORTS_GROUP,true);
        setRoleProperty(group, YukonRoleProperty.CAP_CONTROL_REPORTS_GROUP,true);
        setRoleProperty(group, YukonRoleProperty.DATABASE_REPORTS_GROUP,true);
        setRoleProperty(group, YukonRoleProperty.STARS_REPORTS_GROUP,true);
        setRoleProperty(group, YukonRoleProperty.CI_CURTAILMENT_REPORTS_GROUP,true);

        // TABULAR_DISPLAY_CONSOLE
        setRoleProperty(group, YukonRoleProperty.TDC_MAX_ROWS, "500");

        // TRENDING
        setRoleProperty(group, YukonRoleProperty.GRAPH_EDIT_GRAPHDEFINITION,true);

        // WEB_CLIENT
        setRoleProperty(group, YukonRoleProperty.VIEW_ALARMS_AS_ALERTS,true);

        // APPLICATION_ESUBSTATION_EDITOR
        setRoleProperty(group, YukonRoleProperty.ESUB_EDITOR_ROLE_EXITS,true);

        // CBC_ONELINE_SUB_SETTINGS
        setRoleProperty(group, YukonRoleProperty.SUB_TARGET,true);
        setRoleProperty(group, YukonRoleProperty.SUB_POWER_FACTOR,true);
        setRoleProperty(group, YukonRoleProperty.SUB_EST_POWER_FACTOR,true);
        setRoleProperty(group, YukonRoleProperty.SUB_WATTS,true);
        setRoleProperty(group, YukonRoleProperty.SUB_VOLTS,true);
        setRoleProperty(group, YukonRoleProperty.SUB_DAILY_MAX_OPCNT,true);
        setRoleProperty(group, YukonRoleProperty.SUB_TIMESTAMP,true);
        setRoleProperty(group, YukonRoleProperty.SUB_THREE_PHASE,true);

        // CBC_ONELINE_FEEDER_SETTINGS
        setRoleProperty(group, YukonRoleProperty.FDR_KVAR,true);
        setRoleProperty(group, YukonRoleProperty.FDR_PF,true);
        setRoleProperty(group, YukonRoleProperty.FDR_WATT,true);
        setRoleProperty(group, YukonRoleProperty.FDR_OP_CNT,true);
        setRoleProperty(group, YukonRoleProperty.FDR_VOLT,true);
        setRoleProperty(group, YukonRoleProperty.FDR_TARGET,true);
        setRoleProperty(group, YukonRoleProperty.FDR_TIMESTAMP,true);
        setRoleProperty(group, YukonRoleProperty.FDR_WATT_VOLT,true);
        setRoleProperty(group, YukonRoleProperty.FDR_THREE_PHASE,true);

        // CBC_ONELINE_CAP_SETTINGS
        setRoleProperty(group, YukonRoleProperty.CAP_BANK_SIZE,true);
        setRoleProperty(group, YukonRoleProperty.CAP_CBC_NAME,true);
        setRoleProperty(group, YukonRoleProperty.CAP_TIMESTAMP,true);
        setRoleProperty(group, YukonRoleProperty.CAP_BANK_FIXED_TEXT, "Fixed");
        setRoleProperty(group, YukonRoleProperty.CAP_DAILY_MAX_TOTAL_OPCNT,true);

        // CBC_SETTINGS
        setRoleProperty(group, YukonRoleProperty.CAP_CONTROL_ACCESS,true);
        setRoleProperty(group, YukonRoleProperty.HIDE_REPORTS, false);
        setRoleProperty(group, YukonRoleProperty.HIDE_GRAPHS, false);
        setRoleProperty(group, YukonRoleProperty.HIDE_ONELINE, false);
        setRoleProperty(group, YukonRoleProperty.CBC_ALLOW_OVUV,true);
        setRoleProperty(group, YukonRoleProperty.CBC_DATABASE_EDIT,true);
        setRoleProperty(group, YukonRoleProperty.SHOW_FLIP_COMMAND,true);
        setRoleProperty(group, YukonRoleProperty.SHOW_CB_ADDINFO,true);
        setRoleProperty(group, YukonRoleProperty.ADD_COMMENTS,true);
        setRoleProperty(group, YukonRoleProperty.MODIFY_COMMENTS,true);
        setRoleProperty(group, YukonRoleProperty.SYSTEM_WIDE_CONTROLS,true);
        setRoleProperty(group, YukonRoleProperty.ALLOW_AREA_CONTROLS,true);
        setRoleProperty(group, YukonRoleProperty.ALLOW_SUBSTATION_CONTROLS,true);
        setRoleProperty(group, YukonRoleProperty.ALLOW_SUBBUS_CONTROLS,true);
        setRoleProperty(group, YukonRoleProperty.ALLOW_FEEDER_CONTROLS,true);
        setRoleProperty(group, YukonRoleProperty.ALLOW_CAPBANK_CONTROLS,true);

        // LM_DIRECT_LOADCONTROL
        setRoleProperty(group, YukonRoleProperty.LM_INDIVIDUAL_SWITCH,true);
        setRoleProperty(group, YukonRoleProperty.DIRECT_CONTROL,true);
        setRoleProperty(group, YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS,true);
        setRoleProperty(group, YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS,true);
        setRoleProperty(group, YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT,true);
        setRoleProperty(group, YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS,true);
        setRoleProperty(group, YukonRoleProperty.SHOW_CONTROL_AREAS,true);
        setRoleProperty(group, YukonRoleProperty.SHOW_SCENARIOS,true);

        // LM_DIRECT_LOADCONTROL
        setRoleProperty(group, YukonRoleProperty.CONTROL_AREA_STATE,true);
        setRoleProperty(group, YukonRoleProperty.CONTROL_AREA_VALUE_THRESHOLD,true);
        setRoleProperty(group, YukonRoleProperty.CONTROL_AREA_PEAK_PROJECTION,true);
        setRoleProperty(group, YukonRoleProperty.CONTROL_AREA_ATKU,true);
        setRoleProperty(group, YukonRoleProperty.CONTROL_AREA_PRIORITY,true);
        setRoleProperty(group, YukonRoleProperty.CONTROL_AREA_TIME_WINDOW,true);
        setRoleProperty(group, YukonRoleProperty.CONTROL_AREA_LOAD_CAPACITY,true);
        setRoleProperty(group, YukonRoleProperty.PROGRAM_STATE,true);
        setRoleProperty(group, YukonRoleProperty.PROGRAM_START,true);
        setRoleProperty(group, YukonRoleProperty.PROGRAM_STOP,true);
        setRoleProperty(group, YukonRoleProperty.PROGRAM_CURRENT_GEAR,true);
        setRoleProperty(group, YukonRoleProperty.PROGRAM_PRIORITY,true);
        setRoleProperty(group, YukonRoleProperty.PROGRAM_REDUCTION,true);
        setRoleProperty(group, YukonRoleProperty.PROGRAM_LOAD_CAPACITY,true);
        setRoleProperty(group, YukonRoleProperty.LOAD_GROUP_STATE,true);
        setRoleProperty(group, YukonRoleProperty.LOAD_GROUP_LAST_ACTION,true);
        setRoleProperty(group, YukonRoleProperty.LOAD_GROUP_CONTROL_STATISTICS,true);
        setRoleProperty(group, YukonRoleProperty.LOAD_GROUP_REDUCTION,true);
        setRoleProperty(group, YukonRoleProperty.LOAD_GROUP_LOAD_CAPACITY,true);

        // OPERATOR_ADMINISTRATOR
        setRoleProperty(group, YukonRoleProperty.ADMIN_SUPER_USER,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_CREATE_DELETE_ENERGY_COMPANY,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_MANAGE_MEMBERS,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_VIEW_BATCH_COMMANDS,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_VIEW_OPT_OUT_EVENTS,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_MEMBER_LOGIN_CNTRL,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_MEMBER_ROUTE_SELECT,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_ALLOW_DESIGNATION_CODES,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_MULTI_WAREHOUSE,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_AUTO_PROCESS_BATCH_COMMANDS,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_MULTISPEAK_SETUP,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_LM_USER_ASSIGN,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_EDIT_CONFIG,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_VIEW_CONFIG,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_MANAGE_INDEXES,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_VIEW_LOGS,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_DATABASE_MIGRATION,true);
        setRoleProperty(group, YukonRoleProperty.ADMIN_EVENT_LOGS,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_SURVEY_EDIT,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT,true);

        // CONSUMER_INFO
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_RESIDENCE,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_CALL_TRACKING,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_METERING_INTERVAL_DATA,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_ENROLLMENT,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_WORK_ORDERS,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_FAQ,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_INVENTORY_CHECKING,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_STATUS,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS,true);

        // DEVICE_ACTIONS
        setRoleProperty(group, YukonRoleProperty.BULK_IMPORT_OPERATION,true);
        setRoleProperty(group, YukonRoleProperty.BULK_UPDATE_OPERATION,true);
        setRoleProperty(group, YukonRoleProperty.DEVICE_GROUP_EDIT,true);
        setRoleProperty(group, YukonRoleProperty.DEVICE_GROUP_MODIFY,true);
        setRoleProperty(group, YukonRoleProperty.GROUP_COMMANDER,true);
        setRoleProperty(group, YukonRoleProperty.MASS_CHANGE,true);
        setRoleProperty(group, YukonRoleProperty.LOCATE_ROUTE,true);
        setRoleProperty(group, YukonRoleProperty.MASS_DELETE,true);
        setRoleProperty(group, YukonRoleProperty.ADD_REMOVE_POINTS,true);
        setRoleProperty(group, YukonRoleProperty.ASSIGN_CONFIG,true);
        setRoleProperty(group, YukonRoleProperty.SEND_READ_CONFIG,true);

        // OPERATOR_ESUBSTATION_DRAWINGS
        setRoleProperty(group, YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_VIEW,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_EDIT,true);
        setRoleProperty(group, YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_CONTROL,true);

        // INVENTORY
        setRoleProperty(group, YukonRoleProperty.INVENTORY_SHOW_ALL,true);
        setRoleProperty(group, YukonRoleProperty.SN_ADD_RANGE,true);
        setRoleProperty(group, YukonRoleProperty.SN_UPDATE_RANGE,true);
        setRoleProperty(group, YukonRoleProperty.SN_CONFIG_RANGE,true);
        setRoleProperty(group, YukonRoleProperty.SN_DELETE_RANGE,true);
        setRoleProperty(group, YukonRoleProperty.INVENTORY_CREATE_HARDWARE,true);
        setRoleProperty(group, YukonRoleProperty.EXPRESSCOM_TOOS_RESTORE_FIRST,true);
        setRoleProperty(group, YukonRoleProperty.ALLOW_MULTIPLE_WAREHOUSES,true);
        setRoleProperty(group, YukonRoleProperty.PURCHASING_ACCESS,true);
        setRoleProperty(group, YukonRoleProperty.DEVICE_RECONFIG,true);
        setRoleProperty(group, YukonRoleProperty.INVENTORY_SEARCH,true);

        // METERING
        setRoleProperty(group, YukonRoleProperty.IMPORTER_ENABLED,true);
        setRoleProperty(group, YukonRoleProperty.PROFILE_COLLECTION,true);
        setRoleProperty(group, YukonRoleProperty.MOVE_IN_MOVE_OUT_AUTO_ARCHIVING,true);
        setRoleProperty(group, YukonRoleProperty.MOVE_IN_MOVE_OUT,true);
        setRoleProperty(group, YukonRoleProperty.PROFILE_COLLECTION_SCANNING,true);
        setRoleProperty(group, YukonRoleProperty.HIGH_BILL_COMPLAINT,true);
        setRoleProperty(group, YukonRoleProperty.CIS_DETAIL_WIDGET_ENABLED,true);
        setRoleProperty(group, YukonRoleProperty.OUTAGE_PROCESSING,true);
        setRoleProperty(group, YukonRoleProperty.TAMPER_FLAG_PROCESSING,true);
        setRoleProperty(group, YukonRoleProperty.PHASE_DETECT,true);
        setRoleProperty(group, YukonRoleProperty.VALIDATION_ENGINE,true);
        setRoleProperty(group, YukonRoleProperty.STATUS_POINT_MONITORING,true);
        setRoleProperty(group, YukonRoleProperty.PORTER_RESPONSE_MONITORING,true);

        // SCHEDULER
        setRoleProperty(group, YukonRoleProperty.ENABLE_DISABLE_SCRIPTS,true);
        setRoleProperty(group, YukonRoleProperty.MANAGE_SCHEDULES,true);

        // WORK_ORDER
        setRoleProperty(group, YukonRoleProperty.WORK_ORDER_SHOW_ALL,true);
        setRoleProperty(group, YukonRoleProperty.WORK_ORDER_CREATE_NEW,true);
        setRoleProperty(group, YukonRoleProperty.WORK_ORDER_REPORT,true);
    }
}

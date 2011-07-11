package com.cannontech.web.support.development.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.web.support.development.database.service.DevRolePropUpdaterService;

public class DevRolePropUpdaterServiceImpl extends DevObjectCreationBase implements DevRolePropUpdaterService {
    private RoleDao roleDao;

    @Override
    public void createAll() {
        // feel free to make this sys admin group thing below better
        LiteYukonGroup group = new LiteYukonGroup(-2, "System Administrator Grp");
        updateAllRolePropertiesForGroup(group);
    }
    
    private void updateAllRolePropertiesForGroup(LiteYukonGroup group) {
        // OPERATOR_ADMINISTRATOR
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_VIEW_LOGS);

        // APPLICATION_BILLING
        setRoleTrue(group, YukonRole.APPLICATION_BILLING, YukonRoleProperty.DYNAMIC_BILLING_FILE_SETUP);

        // COMMANDER
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.VERSACOM_SERIAL_MODEL);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.EXPRESSCOM_SERIAL_MODEL);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.DCU_SA205_SERIAL_MODEL);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.DCU_SA305_SERIAL_MODEL);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.READ_DEVICE);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.WRITE_TO_DEVICE);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.CONTROL_DEVICE);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.READ_LM_DEVICE);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.WRITE_TO_LM_DEVICE);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.CONTROL_LM_DEVICE);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.READ_CAP_CONTROL_DEVICE);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.WRITE_TO_CAP_CONTROL_DEVICE);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.CONTROL_CAP_CONTROL_DEVICE);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.EXECUTE_UNKNOWN_COMMAND);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.EXECUTE_MANUAL_COMMAND);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.ENABLE_WEB_COMMANDER);
        setRoleTrue(group, YukonRole.COMMANDER, YukonRoleProperty.ENABLE_CLIENT_COMMANDER);

        // DATABASE_EDITOR
        setRoleTrue(group, YukonRole.DATABASE_EDITOR, YukonRoleProperty.POINT_ID_EDIT);
        setRoleTrue(group, YukonRole.DATABASE_EDITOR, YukonRoleProperty.DBEDITOR_LM);
        setRoleTrue(group, YukonRole.DATABASE_EDITOR, YukonRoleProperty.DBEDITOR_SYSTEM);
        setRoleTrue(group, YukonRole.DATABASE_EDITOR, YukonRoleProperty.PERMIT_LOGIN_EDIT);
        setRoleTrue(group, YukonRole.DATABASE_EDITOR, YukonRoleProperty.ALLOW_USER_ROLES);
        setRoleTrue(group, YukonRole.DATABASE_EDITOR, YukonRoleProperty.ALLOW_MEMBER_PROGRAMS);

        // REPORTING
        setRoleTrue(group, YukonRole.REPORTING, YukonRoleProperty.ADMIN_REPORTS_GROUP);
        setRoleTrue(group, YukonRole.REPORTING, YukonRoleProperty.AMR_REPORTS_GROUP);
        setRoleTrue(group, YukonRole.REPORTING, YukonRoleProperty.STATISTICAL_REPORTS_GROUP);
        setRoleTrue(group, YukonRole.REPORTING, YukonRoleProperty.LOAD_MANAGEMENT_REPORTS_GROUP);
        setRoleTrue(group, YukonRole.REPORTING, YukonRoleProperty.CAP_CONTROL_REPORTS_GROUP);
        setRoleTrue(group, YukonRole.REPORTING, YukonRoleProperty.DATABASE_REPORTS_GROUP);
        setRoleTrue(group, YukonRole.REPORTING, YukonRoleProperty.STARS_REPORTS_GROUP);
        setRoleTrue(group, YukonRole.REPORTING, YukonRoleProperty.CI_CURTAILMENT_REPORTS_GROUP);

        // TABULAR_DISPLAY_CONSOLE
        setRole(group, YukonRole.TABULAR_DISPLAY_CONSOLE, YukonRoleProperty.TDC_MAX_ROWS, "500");

        // TRENDING
        setRoleTrue(group, YukonRole.TRENDING, YukonRoleProperty.GRAPH_EDIT_GRAPHDEFINITION);

        // WEB_CLIENT
        setRoleTrue(group, YukonRole.WEB_CLIENT, YukonRoleProperty.VIEW_ALARMS_AS_ALERTS);

        // APPLICATION_ESUBSTATION_EDITOR
        setRoleTrue(group, YukonRole.APPLICATION_ESUBSTATION_EDITOR, YukonRoleProperty.ESUB_EDITOR_ROLE_EXITS);

        // CBC_ONELINE_SUB_SETTINGS
        setRoleTrue(group, YukonRole.CBC_ONELINE_SUB_SETTINGS, YukonRoleProperty.SUB_TARGET);
        setRoleTrue(group, YukonRole.CBC_ONELINE_SUB_SETTINGS, YukonRoleProperty.SUB_POWER_FACTOR);
        setRoleTrue(group, YukonRole.CBC_ONELINE_SUB_SETTINGS, YukonRoleProperty.SUB_EST_POWER_FACTOR);
        setRoleTrue(group, YukonRole.CBC_ONELINE_SUB_SETTINGS, YukonRoleProperty.SUB_WATTS);
        setRoleTrue(group, YukonRole.CBC_ONELINE_SUB_SETTINGS, YukonRoleProperty.SUB_VOLTS);
        setRoleTrue(group, YukonRole.CBC_ONELINE_SUB_SETTINGS, YukonRoleProperty.SUB_DAILY_MAX_OPCNT);
        setRoleTrue(group, YukonRole.CBC_ONELINE_SUB_SETTINGS, YukonRoleProperty.SUB_TIMESTAMP);
        setRoleTrue(group, YukonRole.CBC_ONELINE_SUB_SETTINGS, YukonRoleProperty.SUB_THREE_PHASE);

        // CBC_ONELINE_FEEDER_SETTINGS
        setRoleTrue(group, YukonRole.CBC_ONELINE_FEEDER_SETTINGS, YukonRoleProperty.FDR_KVAR);
        setRoleTrue(group, YukonRole.CBC_ONELINE_FEEDER_SETTINGS, YukonRoleProperty.FDR_PF);
        setRoleTrue(group, YukonRole.CBC_ONELINE_FEEDER_SETTINGS, YukonRoleProperty.FDR_WATT);
        setRoleTrue(group, YukonRole.CBC_ONELINE_FEEDER_SETTINGS, YukonRoleProperty.FDR_OP_CNT);
        setRoleTrue(group, YukonRole.CBC_ONELINE_FEEDER_SETTINGS, YukonRoleProperty.FDR_VOLT);
        setRoleTrue(group, YukonRole.CBC_ONELINE_FEEDER_SETTINGS, YukonRoleProperty.FDR_TARGET);
        setRoleTrue(group, YukonRole.CBC_ONELINE_FEEDER_SETTINGS, YukonRoleProperty.FDR_TIMESTAMP);
        setRoleTrue(group, YukonRole.CBC_ONELINE_FEEDER_SETTINGS, YukonRoleProperty.FDR_WATT_VOLT);
        setRoleTrue(group, YukonRole.CBC_ONELINE_FEEDER_SETTINGS, YukonRoleProperty.FDR_THREE_PHASE);

        // CBC_ONELINE_CAP_SETTINGS
        setRoleTrue(group, YukonRole.CBC_ONELINE_CAP_SETTINGS, YukonRoleProperty.CAP_BANK_SIZE);
        setRoleTrue(group, YukonRole.CBC_ONELINE_CAP_SETTINGS, YukonRoleProperty.CAP_CBC_NAME);
        setRoleTrue(group, YukonRole.CBC_ONELINE_CAP_SETTINGS, YukonRoleProperty.CAP_TIMESTAMP);
        setRole(group, YukonRole.CBC_ONELINE_CAP_SETTINGS, YukonRoleProperty.CAP_BANK_FIXED_TEXT, "Fixed");
        setRoleTrue(group, YukonRole.CBC_ONELINE_CAP_SETTINGS, YukonRoleProperty.CAP_DAILY_MAX_TOTAL_OPCNT);

        // CBC_SETTINGS
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.CAP_CONTROL_ACCESS);
        setRole(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.HIDE_REPORTS, "false");
        setRole(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.HIDE_GRAPHS, "false");
        setRole(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.HIDE_ONELINE, "false");
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.CBC_ALLOW_OVUV);
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.CBC_DATABASE_EDIT);
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.SHOW_FLIP_COMMAND);
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.SHOW_CB_ADDINFO);
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.ADD_COMMENTS);
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.MODIFY_COMMENTS);
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.SYSTEM_WIDE_CONTROLS);
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.ALLOW_AREA_CONTROLS);
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.ALLOW_SUBSTATION_CONTROLS);
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.ALLOW_SUBBUS_CONTROLS);
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.ALLOW_FEEDER_CONTROLS);
        setRoleTrue(group, YukonRole.CBC_SETTINGS, YukonRoleProperty.ALLOW_CAPBANK_CONTROLS);

        // LM_DIRECT_LOADCONTROL
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.LM_INDIVIDUAL_SWITCH);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.DIRECT_CONTROL);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.SHOW_CONTROL_AREAS);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.SHOW_SCENARIOS);

        // LM_DIRECT_LOADCONTROL
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.CONTROL_AREA_STATE);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.CONTROL_AREA_VALUE_THRESHOLD);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.CONTROL_AREA_PEAK_PROJECTION);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.CONTROL_AREA_ATKU);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.CONTROL_AREA_PRIORITY);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.CONTROL_AREA_TIME_WINDOW);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.CONTROL_AREA_LOAD_CAPACITY);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.PROGRAM_STATE);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.PROGRAM_START);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.PROGRAM_STOP);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.PROGRAM_CURRENT_GEAR);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.PROGRAM_PRIORITY);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.PROGRAM_REDUCTION);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.PROGRAM_LOAD_CAPACITY);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.LOAD_GROUP_STATE);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.LOAD_GROUP_LAST_ACTION);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.LOAD_GROUP_CONTROL_STATISTICS);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.LOAD_GROUP_REDUCTION);
        setRoleTrue(group, YukonRole.LM_DIRECT_LOADCONTROL, YukonRoleProperty.LOAD_GROUP_LOAD_CAPACITY);

        // OPERATOR_ADMINISTRATOR
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_SUPER_USER);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_CREATE_DELETE_ENERGY_COMPANY);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_MANAGE_MEMBERS);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_VIEW_BATCH_COMMANDS);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_VIEW_OPT_OUT_EVENTS);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_MEMBER_LOGIN_CNTRL);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_MEMBER_ROUTE_SELECT);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_ALLOW_DESIGNATION_CODES);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_MULTI_WAREHOUSE);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_AUTO_PROCESS_BATCH_COMMANDS);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_MULTISPEAK_SETUP);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_LM_USER_ASSIGN);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_EDIT_CONFIG);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_VIEW_CONFIG);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_MANAGE_INDEXES);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_VIEW_LOGS);
        setRoleTrue(group, YukonRole.OPERATOR_ADMINISTRATOR, YukonRoleProperty.ADMIN_DATABASE_MIGRATION);

        // CI_CURTAILMENT
        setRole(group, YukonRole.DATABASE_EDITOR, YukonRoleProperty.CURTAILMENT_LABEL, "CI Curtailment");

        // CONSUMER_INFO
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_RESIDENCE);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_CALL_TRACKING);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_METERING_INTERVAL_DATA);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_ENROLLMENT);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_WORK_ORDERS);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_FAQ);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_INVENTORY_CHECKING);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_STATUS);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT);
        setRoleTrue(group, YukonRole.CONSUMER_INFO, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS);

        // DEVICE_ACTIONS
        setRoleTrue(group, YukonRole.DEVICE_ACTIONS, YukonRoleProperty.BULK_IMPORT_OPERATION);
        setRoleTrue(group, YukonRole.DEVICE_ACTIONS, YukonRoleProperty.BULK_UPDATE_OPERATION);
        setRoleTrue(group, YukonRole.DEVICE_ACTIONS, YukonRoleProperty.DEVICE_GROUP_EDIT);
        setRoleTrue(group, YukonRole.DEVICE_ACTIONS, YukonRoleProperty.DEVICE_GROUP_MODIFY);
        setRoleTrue(group, YukonRole.DEVICE_ACTIONS, YukonRoleProperty.GROUP_COMMANDER);
        setRoleTrue(group, YukonRole.DEVICE_ACTIONS, YukonRoleProperty.MASS_CHANGE);
        setRoleTrue(group, YukonRole.DEVICE_ACTIONS, YukonRoleProperty.LOCATE_ROUTE);
        setRoleTrue(group, YukonRole.DEVICE_ACTIONS, YukonRoleProperty.MASS_DELETE);
        setRoleTrue(group, YukonRole.DEVICE_ACTIONS, YukonRoleProperty.ADD_REMOVE_POINTS);
        setRoleTrue(group, YukonRole.DEVICE_ACTIONS, YukonRoleProperty.ASSIGN_CONFIG);
        setRoleTrue(group, YukonRole.DEVICE_ACTIONS, YukonRoleProperty.SEND_READ_CONFIG);

        // OPERATOR_ESUBSTATION_DRAWINGS
        setRoleTrue(group, YukonRole.OPERATOR_ESUBSTATION_DRAWINGS, YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_VIEW);
        setRoleTrue(group, YukonRole.OPERATOR_ESUBSTATION_DRAWINGS, YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_EDIT);
        setRoleTrue(group, YukonRole.OPERATOR_ESUBSTATION_DRAWINGS, YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_CONTROL);

        // INVENTORY
        setRoleTrue(group, YukonRole.INVENTORY, YukonRoleProperty.INVENTORY_SHOW_ALL);
        setRoleTrue(group, YukonRole.INVENTORY, YukonRoleProperty.SN_ADD_RANGE);
        setRoleTrue(group, YukonRole.INVENTORY, YukonRoleProperty.SN_UPDATE_RANGE);
        setRoleTrue(group, YukonRole.INVENTORY, YukonRoleProperty.SN_CONFIG_RANGE);
        setRoleTrue(group, YukonRole.INVENTORY, YukonRoleProperty.SN_DELETE_RANGE);
        setRoleTrue(group, YukonRole.INVENTORY, YukonRoleProperty.INVENTORY_CREATE_HARDWARE);
        setRoleTrue(group, YukonRole.INVENTORY, YukonRoleProperty.EXPRESSCOM_TOOS_RESTORE_FIRST);
        setRoleTrue(group, YukonRole.INVENTORY, YukonRoleProperty.ALLOW_MULTIPLE_WAREHOUSES);
        setRoleTrue(group, YukonRole.INVENTORY, YukonRoleProperty.PURCHASING_ACCESS);
        setRoleTrue(group, YukonRole.INVENTORY, YukonRoleProperty.DEVICE_RECONFIG);
        setRoleTrue(group, YukonRole.INVENTORY, YukonRoleProperty.INVENTORY_SEARCH);

        // METERING
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.IMPORTER_ENABLED);
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.PROFILE_COLLECTION);
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.MOVE_IN_MOVE_OUT_AUTO_ARCHIVING);
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.MOVE_IN_MOVE_OUT);
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.PROFILE_COLLECTION_SCANNING);
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.HIGH_BILL_COMPLAINT);
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.CIS_DETAIL_WIDGET_ENABLED);
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.OUTAGE_PROCESSING);
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.TAMPER_FLAG_PROCESSING);
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.PHASE_DETECT);
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.VALIDATION_ENGINE);
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.STATUS_POINT_MONITORING);
        setRoleTrue(group, YukonRole.METERING, YukonRoleProperty.PORTER_RESPONSE_MONITORING);

        // ODDS_FOR_CONTROL
        setRole(group, YukonRole.DATABASE_EDITOR, YukonRoleProperty.ODDS_FOR_CONTROL_LABEL, "Odds for Control");

        // SCHEDULER
        setRoleTrue(group, YukonRole.SCHEDULER, YukonRoleProperty.ENABLE_DISABLE_SCRIPTS);
        setRoleTrue(group, YukonRole.SCHEDULER, YukonRoleProperty.MANAGE_SCHEDULES);

        // WORK_ORDER
        setRoleTrue(group, YukonRole.WORK_ORDER, YukonRoleProperty.WORK_ORDER_SHOW_ALL);
        setRoleTrue(group, YukonRole.WORK_ORDER, YukonRoleProperty.WORK_ORDER_CREATE_NEW);
        setRoleTrue(group, YukonRole.WORK_ORDER, YukonRoleProperty.WORK_ORDER_REPORT);
    }

    private void setRole(LiteYukonGroup group, YukonRole role, YukonRoleProperty property, String newVal) {
        log.info("Setting YukonRole " + role.name() + " and YukonRoleProperty " + property.name() + " to " + newVal);
        roleDao.updateGroupRoleProperty(group, role.getRoleId(), property.getPropertyId(), newVal);
    }

    private void setRoleTrue(LiteYukonGroup group, YukonRole role, YukonRoleProperty property) {
        log.info("Setting YukonRole " + role.name() + " and YukonRoleProperty " + property.name() + " to true");
        roleDao.updateGroupRoleProperty(group, role.getRoleId(), property.getPropertyId(), "true");
    }
    
    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
}

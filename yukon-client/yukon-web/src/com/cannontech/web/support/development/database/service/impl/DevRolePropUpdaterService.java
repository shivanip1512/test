package com.cannontech.web.support.development.database.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.roleproperties.GroupRolePropertyValueCollection;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyEditorDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.user.UserUtils;

public class DevRolePropUpdaterService extends DevObjectCreationBase {
    private RoleDao roleDao;
    private RolePropertyEditorDao rolePropertyEditorDao;

    @Override
    protected void createAll() {
        // feel free to make this sys admin group thing below better
        LiteYukonGroup group = new LiteYukonGroup(UserUtils.USER_YUKON_ID, "System Administrator Grp");
        updateAllRolePropertiesForGroup(group);
    }

    @Override
    protected void logFinalExecutionDetails() {
    }

    private void updateAllRolePropertiesForGroup(LiteYukonGroup group) {

        // ODDS_FOR_CONTROL
        setRole(group, YukonRoleProperty.ODDS_FOR_CONTROL_LABEL, "Odds for Control");

        // CI_CURTAILMENT
        setRole(group, YukonRoleProperty.CURTAILMENT_LABEL, "CI Curtailment");

        // Demand Response
        setRoleTrue(group, YukonRoleProperty.DEMAND_RESPONSE);

        // APPLICATION_BILLING
        setRoleTrue(group, YukonRoleProperty.DYNAMIC_BILLING_FILE_SETUP);

        // COMMANDER
        setRoleTrue(group, YukonRoleProperty.VERSACOM_SERIAL_MODEL);
        setRoleTrue(group, YukonRoleProperty.EXPRESSCOM_SERIAL_MODEL);
        setRoleTrue(group, YukonRoleProperty.DCU_SA205_SERIAL_MODEL);
        setRoleTrue(group, YukonRoleProperty.DCU_SA305_SERIAL_MODEL);
        setRoleTrue(group, YukonRoleProperty.READ_DEVICE);
        setRoleTrue(group, YukonRoleProperty.WRITE_TO_DEVICE);
        setRoleTrue(group, YukonRoleProperty.CONTROL_DEVICE);
        setRoleTrue(group, YukonRoleProperty.READ_LM_DEVICE);
        setRoleTrue(group, YukonRoleProperty.WRITE_TO_LM_DEVICE);
        setRoleTrue(group, YukonRoleProperty.CONTROL_LM_DEVICE);
        setRoleTrue(group, YukonRoleProperty.READ_CAP_CONTROL_DEVICE);
        setRoleTrue(group, YukonRoleProperty.WRITE_TO_CAP_CONTROL_DEVICE);
        setRoleTrue(group, YukonRoleProperty.CONTROL_CAP_CONTROL_DEVICE);
        setRoleTrue(group, YukonRoleProperty.EXECUTE_UNKNOWN_COMMAND);
        setRoleTrue(group, YukonRoleProperty.EXECUTE_MANUAL_COMMAND);
        setRoleTrue(group, YukonRoleProperty.ENABLE_WEB_COMMANDER);
        setRoleTrue(group, YukonRoleProperty.ENABLE_CLIENT_COMMANDER);

        // DATABASE_EDITOR
        setRoleTrue(group, YukonRoleProperty.POINT_ID_EDIT);
        setRoleTrue(group, YukonRoleProperty.DBEDITOR_LM);
        setRoleTrue(group, YukonRoleProperty.DBEDITOR_SYSTEM);
        setRoleTrue(group, YukonRoleProperty.PERMIT_LOGIN_EDIT);
        setRoleTrue(group, YukonRoleProperty.ALLOW_USER_ROLES);
        setRoleTrue(group, YukonRoleProperty.ALLOW_MEMBER_PROGRAMS);

        // REPORTING
        setRoleTrue(group, YukonRoleProperty.ADMIN_REPORTS_GROUP);
        setRoleTrue(group, YukonRoleProperty.AMR_REPORTS_GROUP);
        setRoleTrue(group, YukonRoleProperty.STATISTICAL_REPORTS_GROUP);
        setRoleTrue(group, YukonRoleProperty.LOAD_MANAGEMENT_REPORTS_GROUP);
        setRoleTrue(group, YukonRoleProperty.CAP_CONTROL_REPORTS_GROUP);
        setRoleTrue(group, YukonRoleProperty.DATABASE_REPORTS_GROUP);
        setRoleTrue(group, YukonRoleProperty.STARS_REPORTS_GROUP);
        setRoleTrue(group, YukonRoleProperty.CI_CURTAILMENT_REPORTS_GROUP);

        // TABULAR_DISPLAY_CONSOLE
        setRole(group, YukonRoleProperty.TDC_MAX_ROWS, "500");

        // TRENDING
        setRoleTrue(group, YukonRoleProperty.GRAPH_EDIT_GRAPHDEFINITION);

        // WEB_CLIENT
        setRoleTrue(group, YukonRoleProperty.VIEW_ALARMS_AS_ALERTS);

        // APPLICATION_ESUBSTATION_EDITOR
        setRoleTrue(group, YukonRoleProperty.ESUB_EDITOR_ROLE_EXITS);

        // CBC_ONELINE_SUB_SETTINGS
        setRoleTrue(group, YukonRoleProperty.SUB_TARGET);
        setRoleTrue(group, YukonRoleProperty.SUB_POWER_FACTOR);
        setRoleTrue(group, YukonRoleProperty.SUB_EST_POWER_FACTOR);
        setRoleTrue(group, YukonRoleProperty.SUB_WATTS);
        setRoleTrue(group, YukonRoleProperty.SUB_VOLTS);
        setRoleTrue(group, YukonRoleProperty.SUB_DAILY_MAX_OPCNT);
        setRoleTrue(group, YukonRoleProperty.SUB_TIMESTAMP);
        setRoleTrue(group, YukonRoleProperty.SUB_THREE_PHASE);

        // CBC_ONELINE_FEEDER_SETTINGS
        setRoleTrue(group, YukonRoleProperty.FDR_KVAR);
        setRoleTrue(group, YukonRoleProperty.FDR_PF);
        setRoleTrue(group, YukonRoleProperty.FDR_WATT);
        setRoleTrue(group, YukonRoleProperty.FDR_OP_CNT);
        setRoleTrue(group, YukonRoleProperty.FDR_VOLT);
        setRoleTrue(group, YukonRoleProperty.FDR_TARGET);
        setRoleTrue(group, YukonRoleProperty.FDR_TIMESTAMP);
        setRoleTrue(group, YukonRoleProperty.FDR_WATT_VOLT);
        setRoleTrue(group, YukonRoleProperty.FDR_THREE_PHASE);

        // CBC_ONELINE_CAP_SETTINGS
        setRoleTrue(group, YukonRoleProperty.CAP_BANK_SIZE);
        setRoleTrue(group, YukonRoleProperty.CAP_CBC_NAME);
        setRoleTrue(group, YukonRoleProperty.CAP_TIMESTAMP);
        setRole(group, YukonRoleProperty.CAP_BANK_FIXED_TEXT, "Fixed");
        setRoleTrue(group, YukonRoleProperty.CAP_DAILY_MAX_TOTAL_OPCNT);

        // CBC_SETTINGS
        setRoleTrue(group, YukonRoleProperty.CAP_CONTROL_ACCESS);
        setRole(group, YukonRoleProperty.HIDE_REPORTS, "false");
        setRole(group, YukonRoleProperty.HIDE_GRAPHS, "false");
        setRole(group, YukonRoleProperty.HIDE_ONELINE, "false");
        setRoleTrue(group, YukonRoleProperty.CBC_ALLOW_OVUV);
        setRoleTrue(group, YukonRoleProperty.CBC_DATABASE_EDIT);
        setRoleTrue(group, YukonRoleProperty.SHOW_FLIP_COMMAND);
        setRoleTrue(group, YukonRoleProperty.SHOW_CB_ADDINFO);
        setRoleTrue(group, YukonRoleProperty.ADD_COMMENTS);
        setRoleTrue(group, YukonRoleProperty.MODIFY_COMMENTS);
        setRoleTrue(group, YukonRoleProperty.SYSTEM_WIDE_CONTROLS);
        setRoleTrue(group, YukonRoleProperty.ALLOW_AREA_CONTROLS);
        setRoleTrue(group, YukonRoleProperty.ALLOW_SUBSTATION_CONTROLS);
        setRoleTrue(group, YukonRoleProperty.ALLOW_SUBBUS_CONTROLS);
        setRoleTrue(group, YukonRoleProperty.ALLOW_FEEDER_CONTROLS);
        setRoleTrue(group, YukonRoleProperty.ALLOW_CAPBANK_CONTROLS);

        // LM_DIRECT_LOADCONTROL
        setRoleTrue(group, YukonRoleProperty.LM_INDIVIDUAL_SWITCH);
        setRoleTrue(group, YukonRoleProperty.DIRECT_CONTROL);
        setRoleTrue(group, YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS);
        setRoleTrue(group, YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS);
        setRoleTrue(group, YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT);
        setRoleTrue(group, YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS);
        setRoleTrue(group, YukonRoleProperty.SHOW_CONTROL_AREAS);
        setRoleTrue(group, YukonRoleProperty.SHOW_SCENARIOS);

        // LM_DIRECT_LOADCONTROL
        setRoleTrue(group, YukonRoleProperty.CONTROL_AREA_STATE);
        setRoleTrue(group, YukonRoleProperty.CONTROL_AREA_VALUE_THRESHOLD);
        setRoleTrue(group, YukonRoleProperty.CONTROL_AREA_PEAK_PROJECTION);
        setRoleTrue(group, YukonRoleProperty.CONTROL_AREA_ATKU);
        setRoleTrue(group, YukonRoleProperty.CONTROL_AREA_PRIORITY);
        setRoleTrue(group, YukonRoleProperty.CONTROL_AREA_TIME_WINDOW);
        setRoleTrue(group, YukonRoleProperty.CONTROL_AREA_LOAD_CAPACITY);
        setRoleTrue(group, YukonRoleProperty.PROGRAM_STATE);
        setRoleTrue(group, YukonRoleProperty.PROGRAM_START);
        setRoleTrue(group, YukonRoleProperty.PROGRAM_STOP);
        setRoleTrue(group, YukonRoleProperty.PROGRAM_CURRENT_GEAR);
        setRoleTrue(group, YukonRoleProperty.PROGRAM_PRIORITY);
        setRoleTrue(group, YukonRoleProperty.PROGRAM_REDUCTION);
        setRoleTrue(group, YukonRoleProperty.PROGRAM_LOAD_CAPACITY);
        setRoleTrue(group, YukonRoleProperty.LOAD_GROUP_STATE);
        setRoleTrue(group, YukonRoleProperty.LOAD_GROUP_LAST_ACTION);
        setRoleTrue(group, YukonRoleProperty.LOAD_GROUP_CONTROL_STATISTICS);
        setRoleTrue(group, YukonRoleProperty.LOAD_GROUP_REDUCTION);
        setRoleTrue(group, YukonRoleProperty.LOAD_GROUP_LOAD_CAPACITY);

        // OPERATOR_ADMINISTRATOR
        setRoleTrue(group, YukonRoleProperty.ADMIN_SUPER_USER);
        setRoleTrue(group, YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY);
        setRoleTrue(group, YukonRoleProperty.ADMIN_CREATE_DELETE_ENERGY_COMPANY);
        setRoleTrue(group, YukonRoleProperty.ADMIN_MANAGE_MEMBERS);
        setRoleTrue(group, YukonRoleProperty.ADMIN_VIEW_BATCH_COMMANDS);
        setRoleTrue(group, YukonRoleProperty.ADMIN_VIEW_OPT_OUT_EVENTS);
        setRoleTrue(group, YukonRoleProperty.ADMIN_MEMBER_LOGIN_CNTRL);
        setRoleTrue(group, YukonRoleProperty.ADMIN_MEMBER_ROUTE_SELECT);
        setRoleTrue(group, YukonRoleProperty.ADMIN_ALLOW_DESIGNATION_CODES);
        setRoleTrue(group, YukonRoleProperty.ADMIN_MULTI_WAREHOUSE);
        setRoleTrue(group, YukonRoleProperty.ADMIN_AUTO_PROCESS_BATCH_COMMANDS);
        setRoleTrue(group, YukonRoleProperty.ADMIN_MULTISPEAK_SETUP);
        setRoleTrue(group, YukonRoleProperty.ADMIN_LM_USER_ASSIGN);
        setRoleTrue(group, YukonRoleProperty.ADMIN_EDIT_CONFIG);
        setRoleTrue(group, YukonRoleProperty.ADMIN_VIEW_CONFIG);
        setRoleTrue(group, YukonRoleProperty.ADMIN_MANAGE_INDEXES);
        setRoleTrue(group, YukonRoleProperty.ADMIN_VIEW_LOGS);
        setRoleTrue(group, YukonRoleProperty.ADMIN_DATABASE_MIGRATION);
        setRoleTrue(group, YukonRoleProperty.ADMIN_EVENT_LOGS);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_SURVEY_EDIT);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT);

        // CONSUMER_INFO
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_RESIDENCE);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_CALL_TRACKING);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_METERING_INTERVAL_DATA);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_ENROLLMENT);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_WORK_ORDERS);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_FAQ);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_INVENTORY_CHECKING);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_STATUS);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS);

        // DEVICE_ACTIONS
        setRoleTrue(group, YukonRoleProperty.BULK_IMPORT_OPERATION);
        setRoleTrue(group, YukonRoleProperty.BULK_UPDATE_OPERATION);
        setRoleTrue(group, YukonRoleProperty.DEVICE_GROUP_EDIT);
        setRoleTrue(group, YukonRoleProperty.DEVICE_GROUP_MODIFY);
        setRoleTrue(group, YukonRoleProperty.GROUP_COMMANDER);
        setRoleTrue(group, YukonRoleProperty.MASS_CHANGE);
        setRoleTrue(group, YukonRoleProperty.LOCATE_ROUTE);
        setRoleTrue(group, YukonRoleProperty.MASS_DELETE);
        setRoleTrue(group, YukonRoleProperty.ADD_REMOVE_POINTS);
        setRoleTrue(group, YukonRoleProperty.ASSIGN_CONFIG);
        setRoleTrue(group, YukonRoleProperty.SEND_READ_CONFIG);

        // OPERATOR_ESUBSTATION_DRAWINGS
        setRoleTrue(group, YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_VIEW);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_EDIT);
        setRoleTrue(group, YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_CONTROL);

        // INVENTORY
        setRoleTrue(group, YukonRoleProperty.INVENTORY_SHOW_ALL);
        setRoleTrue(group, YukonRoleProperty.SN_ADD_RANGE);
        setRoleTrue(group, YukonRoleProperty.SN_UPDATE_RANGE);
        setRoleTrue(group, YukonRoleProperty.SN_CONFIG_RANGE);
        setRoleTrue(group, YukonRoleProperty.SN_DELETE_RANGE);
        setRoleTrue(group, YukonRoleProperty.INVENTORY_CREATE_HARDWARE);
        setRoleTrue(group, YukonRoleProperty.EXPRESSCOM_TOOS_RESTORE_FIRST);
        setRoleTrue(group, YukonRoleProperty.ALLOW_MULTIPLE_WAREHOUSES);
        setRoleTrue(group, YukonRoleProperty.PURCHASING_ACCESS);
        setRoleTrue(group, YukonRoleProperty.DEVICE_RECONFIG);
        setRoleTrue(group, YukonRoleProperty.INVENTORY_SEARCH);

        // METERING
        setRoleTrue(group, YukonRoleProperty.IMPORTER_ENABLED);
        setRoleTrue(group, YukonRoleProperty.PROFILE_COLLECTION);
        setRoleTrue(group, YukonRoleProperty.MOVE_IN_MOVE_OUT_AUTO_ARCHIVING);
        setRoleTrue(group, YukonRoleProperty.MOVE_IN_MOVE_OUT);
        setRoleTrue(group, YukonRoleProperty.PROFILE_COLLECTION_SCANNING);
        setRoleTrue(group, YukonRoleProperty.HIGH_BILL_COMPLAINT);
        setRoleTrue(group, YukonRoleProperty.CIS_DETAIL_WIDGET_ENABLED);
        setRoleTrue(group, YukonRoleProperty.OUTAGE_PROCESSING);
        setRoleTrue(group, YukonRoleProperty.TAMPER_FLAG_PROCESSING);
        setRoleTrue(group, YukonRoleProperty.PHASE_DETECT);
        setRoleTrue(group, YukonRoleProperty.VALIDATION_ENGINE);
        setRoleTrue(group, YukonRoleProperty.STATUS_POINT_MONITORING);
        setRoleTrue(group, YukonRoleProperty.PORTER_RESPONSE_MONITORING);

        // SCHEDULER
        setRoleTrue(group, YukonRoleProperty.ENABLE_DISABLE_SCRIPTS);
        setRoleTrue(group, YukonRoleProperty.MANAGE_SCHEDULES);

        // WORK_ORDER
        setRoleTrue(group, YukonRoleProperty.WORK_ORDER_SHOW_ALL);
        setRoleTrue(group, YukonRoleProperty.WORK_ORDER_CREATE_NEW);
        setRoleTrue(group, YukonRoleProperty.WORK_ORDER_REPORT);
    }

    private void setRole(LiteYukonGroup group, YukonRoleProperty property, String newVal) {
        roleDao.updateGroupRoleProperty(group, property.getRole().getRoleId(), property.getPropertyId(), newVal);
        log.info("YukonRole " + property.getRole().name() + " and YukonRoleProperty " + property.name() + " set to " + newVal);
    }

    private void setRoleTrue(LiteYukonGroup group, YukonRoleProperty property) {
        GroupRolePropertyValueCollection propertyValues = rolePropertyEditorDao.getForGroupAndRole(group, property.getRole(), true);
        Map<YukonRoleProperty, Object> valueMap = propertyValues.getValueMap();
        valueMap.put(property, true);
        propertyValues.putAll(valueMap);
        rolePropertyEditorDao.save(propertyValues);
        log.info("YukonRole " + property.getRole().name() + " and YukonRoleProperty " + property.name() + " set to true");
    }
    
    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
    
    @Autowired
    public void setRolePropertyEditorDao(RolePropertyEditorDao rolePropertyEditorDao) {
        this.rolePropertyEditorDao = rolePropertyEditorDao;
    }
}

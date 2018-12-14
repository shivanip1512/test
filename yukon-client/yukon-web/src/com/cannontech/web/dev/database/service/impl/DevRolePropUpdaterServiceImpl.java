package com.cannontech.web.dev.database.service.impl;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.CapControlCommandsAccessLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.development.service.impl.DevObjectCreationBase;
import com.cannontech.web.dev.database.objects.DevRoleProperties;
import com.cannontech.web.dev.database.service.DevRolePropUpdaterService;
import com.google.common.collect.Maps;

public class DevRolePropUpdaterServiceImpl extends DevObjectCreationBase implements DevRolePropUpdaterService {
    @Autowired private YukonGroupDao yukonGroupDao;
    private static final ReentrantLock _lock = new ReentrantLock();

    @Override
    public boolean isRunning() {
        return _lock.isLocked();
    }

    @Override
    @Transactional
    public Map<YukonRole, Boolean> executeSetup(DevRoleProperties devRoleProperties) {
        Map<YukonRole,Boolean> results = Maps.newHashMap();
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroupByName(devRoleProperties.getGroupName());
        
        if (_lock.tryLock()) {
            try {
                if (canAddRole(group, YukonRole.ODDS_FOR_CONTROL)) {
                    setRoleProperty(group, YukonRoleProperty.ODDS_FOR_CONTROL_ROLE_EXISTS, true);
                    results.put(YukonRole.ODDS_FOR_CONTROL, true);
                } else {
                    results.put(YukonRole.ODDS_FOR_CONTROL, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.ODDS_FOR_CONTROL);
                }

                if (canAddRole(group, YukonRole.CI_CURTAILMENT)) {
                    setRoleProperty(group, YukonRoleProperty.CURTAILMENT_IS_OPERATOR, true);
                    results.put(YukonRole.CI_CURTAILMENT, true);
                } else {
                    results.put(YukonRole.CI_CURTAILMENT, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.CI_CURTAILMENT);
                }

                if (canAddRole(group, YukonRole.APPLICATION_BILLING)) {
                    setRoleProperty(group, YukonRoleProperty.DYNAMIC_BILLING_FILE_SETUP,true);
                    results.put(YukonRole.APPLICATION_BILLING, true);
                } else {
                    results.put(YukonRole.APPLICATION_BILLING, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.APPLICATION_BILLING);
                }

                if (canAddRole(group, YukonRole.COMMANDER)) {
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
                    results.put(YukonRole.COMMANDER, true);
                } else {
                    results.put(YukonRole.COMMANDER, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.COMMANDER);
                }

                if (canAddRole(group, YukonRole.DATABASE_EDITOR)) {
                    setRoleProperty(group, YukonRoleProperty.DBEDITOR_LM,true);
                    setRoleProperty(group, YukonRoleProperty.DBEDITOR_SYSTEM,true);
                    setRoleProperty(group, YukonRoleProperty.PERMIT_LOGIN_EDIT,true);
                    setRoleProperty(group, YukonRoleProperty.ALLOW_MEMBER_PROGRAMS,true);
                    results.put(YukonRole.DATABASE_EDITOR, true);
                } else {
                    results.put(YukonRole.DATABASE_EDITOR, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.DATABASE_EDITOR);
                }

                if (canAddRole(group, YukonRole.REPORTING)) {
                    setRoleProperty(group, YukonRoleProperty.ADMIN_REPORTS_GROUP,true);
                    setRoleProperty(group, YukonRoleProperty.AMR_REPORTS_GROUP,true);
                    setRoleProperty(group, YukonRoleProperty.STATISTICAL_REPORTS_GROUP,true);
                    setRoleProperty(group, YukonRoleProperty.LOAD_MANAGEMENT_REPORTS_GROUP,true);
                    setRoleProperty(group, YukonRoleProperty.CAP_CONTROL_REPORTS_GROUP,true);
                    setRoleProperty(group, YukonRoleProperty.DATABASE_REPORTS_GROUP,true);
                    setRoleProperty(group, YukonRoleProperty.STARS_REPORTS_GROUP,true);
                    setRoleProperty(group, YukonRoleProperty.CI_CURTAILMENT_REPORTS_GROUP,true);
                    results.put(YukonRole.REPORTING, true);
                } else {
                    results.put(YukonRole.REPORTING, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.REPORTING);
                }

                if (canAddRole(group, YukonRole.TABULAR_DISPLAY_CONSOLE)) {
                    setRoleProperty(group, YukonRoleProperty.TDC_MAX_ROWS, "500");
                    results.put(YukonRole.TABULAR_DISPLAY_CONSOLE, true);
                } else {
                    results.put(YukonRole.TABULAR_DISPLAY_CONSOLE, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.TABULAR_DISPLAY_CONSOLE);
                }

                if (canAddRole(group, YukonRole.TRENDING)) {
                    setRoleProperty(group, YukonRoleProperty.GRAPH_EDIT_GRAPHDEFINITION,true);
                    setRoleProperty(group, YukonRoleProperty.VIEW_ALARMS_AS_ALERTS,true);
                    setRoleProperty(group, YukonRoleProperty.SUPPRESS_ERROR_PAGE_DETAILS,false);
                    results.put(YukonRole.TRENDING, true);
                } else {
                    results.put(YukonRole.TRENDING, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.TRENDING);
                }

               

                if (canAddRole(group, YukonRole.CBC_ONELINE_CAP_SETTINGS)) {
                    setRoleProperty(group, YukonRoleProperty.CAP_BANK_FIXED_TEXT, "Fixed");
                    results.put(YukonRole.CBC_ONELINE_CAP_SETTINGS, true);
                } else {
                    results.put(YukonRole.CBC_ONELINE_CAP_SETTINGS, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.CBC_ONELINE_CAP_SETTINGS);
                }

                if (canAddRole(group, YukonRole.CBC_SETTINGS)) {
                    setRoleProperty(group, YukonRoleProperty.CAP_CONTROL_ACCESS,true);
                    setRoleProperty(group, YukonRoleProperty.HIDE_REPORTS, false);
                    setRoleProperty(group, YukonRoleProperty.HIDE_GRAPHS, false);
                    setRoleProperty(group, YukonRoleProperty.CBC_ALLOW_OVUV,true);
                    setRoleProperty(group, YukonRoleProperty.CBC_DATABASE_EDIT,true);
                    setRoleProperty(group, YukonRoleProperty.SHOW_FLIP_COMMAND,true);
                    setRoleProperty(group, YukonRoleProperty.SHOW_CB_ADDINFO,true);
                    setRoleProperty(group, YukonRoleProperty.ADD_COMMENTS,true);
                    setRoleProperty(group, YukonRoleProperty.MODIFY_COMMENTS,true);
                    setRoleProperty(group, YukonRoleProperty.SYSTEM_WIDE_CONTROLS,true);
                    setRoleProperty(group, YukonRoleProperty.AREA_COMMANDS_AND_ACTIONS,CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS.name());
                    setRoleProperty(group, YukonRoleProperty.SUBSTATION_COMMANDS_AND_ACTIONS,CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS.name());
                    setRoleProperty(group, YukonRoleProperty.SUBBUS_COMMANDS_AND_ACTIONS,CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS.name());
                    setRoleProperty(group, YukonRoleProperty.FEEDER_COMMANDS_AND_ACTIONS,CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS.name());
                    setRoleProperty(group, YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS,CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS.name());
                    setRoleProperty(group, YukonRoleProperty.CAP_CONTROL_IMPORTER, true);
                    results.put(YukonRole.CBC_SETTINGS, true);
                } else {
                    results.put(YukonRole.CBC_SETTINGS, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.CBC_SETTINGS);
                }

                if (canAddRole(group, YukonRole.DEMAND_RESPONSE)) {
                    setRoleProperty(group, YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS,true);
                    setRoleProperty(group, YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS,true);
                    setRoleProperty(group, YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT,true);
                    setRoleProperty(group, YukonRoleProperty.ALLOW_STOP_GEAR_ACCESS,true);
                    setRoleProperty(group, YukonRoleProperty.SHOW_CONTROL_AREAS,true);
                    setRoleProperty(group, YukonRoleProperty.SHOW_SCENARIOS,true);
                    setRoleProperty(group, YukonRoleProperty.SHOW_ASSET_AVAILABILITY,true);
                    setRoleProperty(group, YukonRoleProperty.SHOW_ECOBEE, true);
                    setRoleProperty(group, YukonRoleProperty.DR_VIEW_CONTROL_AREA_TRIGGER_INFO, true);                    
                    setRoleProperty(group, YukonRoleProperty.DR_VIEW_PRIORITY, true);
                    setRoleProperty(group, YukonRoleProperty.DR_VIEW_REDUCTION, true);
                    setRoleProperty(group, YukonRoleProperty.USE_PAO_PERMISSIONS, true);
                    results.put(YukonRole.DEMAND_RESPONSE, true);
                } else {
                    results.put(YukonRole.DEMAND_RESPONSE, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.DEMAND_RESPONSE);
                }


                if (canAddRole(group, YukonRole.OPERATOR_ADMINISTRATOR)) {
                    setRoleProperty(group, YukonRoleProperty.ADMIN_SUPER_USER,true);
                    setRoleProperty(group, YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY,true);                    
                    setRoleProperty(group, YukonRoleProperty.ADMIN_MANAGE_MEMBERS,true);
                    setRoleProperty(group, YukonRoleProperty.ADMIN_VIEW_BATCH_COMMANDS,true);
                    setRoleProperty(group, YukonRoleProperty.ADMIN_VIEW_OPT_OUT_EVENTS,true);
                    setRoleProperty(group, YukonRoleProperty.ADMIN_MEMBER_LOGIN_CNTRL,true);
                    setRoleProperty(group, YukonRoleProperty.ADMIN_MEMBER_ROUTE_SELECT,true);
                    setRoleProperty(group, YukonRoleProperty.ADMIN_MULTI_WAREHOUSE,true);
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
                    results.put(YukonRole.OPERATOR_ADMINISTRATOR, true);
                } else {
                    results.put(YukonRole.OPERATOR_ADMINISTRATOR, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.OPERATOR_ADMINISTRATOR);
                }

                if (canAddRole(group, YukonRole.CONSUMER_INFO)) {
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
                    setRoleProperty(group, YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,true);
                    setRoleProperty(group, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_STATUS,true);
                    setRoleProperty(group, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE,true);
                    setRoleProperty(group, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT,true);
                    setRoleProperty(group, YukonRoleProperty.OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS,true);
                    results.put(YukonRole.CONSUMER_INFO, true);
                } else {
                    results.put(YukonRole.CONSUMER_INFO, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.CONSUMER_INFO);
                }

                if (canAddRole(group, YukonRole.DEVICE_ACTIONS)) {
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
                    results.put(YukonRole.DEVICE_ACTIONS, true);
                } else {
                    results.put(YukonRole.DEVICE_ACTIONS, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.DEVICE_ACTIONS);
                }

               

                if (canAddRole(group, YukonRole.INVENTORY)) {
                    setRoleProperty(group, YukonRoleProperty.SN_ADD_RANGE,true);
                    setRoleProperty(group, YukonRoleProperty.SN_UPDATE_RANGE,true);
                    setRoleProperty(group, YukonRoleProperty.SN_DELETE_RANGE,true);
                    setRoleProperty(group, YukonRoleProperty.INVENTORY_CREATE_HARDWARE,true);
                    setRoleProperty(group, YukonRoleProperty.EXPRESSCOM_TOOS_RESTORE_FIRST,true);
                    setRoleProperty(group, YukonRoleProperty.PURCHASING_ACCESS,true);
                    setRoleProperty(group, YukonRoleProperty.DEVICE_RECONFIG,true);
                    setRoleProperty(group, YukonRoleProperty.INVENTORY_SEARCH,true);
                    results.put(YukonRole.INVENTORY, true);
                } else {
                    results.put(YukonRole.INVENTORY, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.INVENTORY);
                }

                if (canAddRole(group, YukonRole.METERING)) {
                    setRoleProperty(group, YukonRoleProperty.IMPORTER_ENABLED,true);
                    setRoleProperty(group, YukonRoleProperty.PROFILE_COLLECTION,true);
                    setRoleProperty(group, YukonRoleProperty.MOVE_IN_MOVE_OUT_AUTO_ARCHIVING,true);
                    setRoleProperty(group, YukonRoleProperty.MOVE_IN_MOVE_OUT,true);
                    setRoleProperty(group, YukonRoleProperty.PROFILE_COLLECTION_SCANNING,true);
                    setRoleProperty(group, YukonRoleProperty.HIGH_BILL_COMPLAINT,true);
                    setRoleProperty(group, YukonRoleProperty.OUTAGE_PROCESSING,true);
                    setRoleProperty(group, YukonRoleProperty.TAMPER_FLAG_PROCESSING,true);
                    setRoleProperty(group, YukonRoleProperty.PHASE_DETECT,true);
                    setRoleProperty(group, YukonRoleProperty.VALIDATION_ENGINE,true);
                    setRoleProperty(group, YukonRoleProperty.STATUS_POINT_MONITORING,true);
                    setRoleProperty(group, YukonRoleProperty.PORTER_RESPONSE_MONITORING,true);
                    setRoleProperty(group, YukonRoleProperty.DEVICE_DATA_MONITORING,true);
                    setRoleProperty(group, YukonRoleProperty.METER_EVENTS,true);
                    setRoleProperty(group, YukonRoleProperty.WATER_LEAK_REPORT,true);
                    results.put(YukonRole.METERING, true);
                } else {
                    results.put(YukonRole.METERING, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.METERING);
                }

                if (canAddRole(group, YukonRole.SCHEDULER)) {
                    setRoleProperty(group, YukonRoleProperty.MANAGE_SCHEDULES,true);
                    results.put(YukonRole.SCHEDULER, true);
                } else {
                    results.put(YukonRole.SCHEDULER, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.SCHEDULER);
                }

                if (canAddRole(group, YukonRole.WORK_ORDER)) {
                    setRoleProperty(group, YukonRoleProperty.WORK_ORDER_SHOW_ALL,true);
                    setRoleProperty(group, YukonRoleProperty.WORK_ORDER_CREATE_NEW,true);
                    setRoleProperty(group, YukonRoleProperty.WORK_ORDER_REPORT,true);
                    results.put(YukonRole.WORK_ORDER, true);
                } else {
                    results.put(YukonRole.WORK_ORDER, false);
                    log.info("Failed due to role conflict for group " + group.getGroupName() + " with YukonRole " + YukonRole.WORK_ORDER);
                }     
            } finally {
                _lock.unlock();
            }
        }

        return results;
    }
}

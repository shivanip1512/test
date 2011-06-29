package com.cannontech.web.support.development.database.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.cbc.service.CapControlCreationService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PaoScheduleDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.CCU711;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.database.data.port.TerminalServerDirectPort;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.port.PortSettings;
import com.cannontech.database.db.port.PortTerminalServer;
import com.cannontech.database.db.route.CarrierRoute;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.hardware.model.HardwareHistory;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.objects.DevAMR;
import com.cannontech.web.support.development.database.objects.DevCCU;
import com.cannontech.web.support.development.database.objects.DevCapControl;
import com.cannontech.web.support.development.database.objects.DevCommChannel;
import com.cannontech.web.support.development.database.objects.DevHardwareType;
import com.cannontech.web.support.development.database.objects.DevMeter;
import com.cannontech.web.support.development.database.objects.DevPaoType;
import com.cannontech.web.support.development.database.objects.DevStars;
import com.cannontech.web.support.development.database.service.DevDatabasePopulationService;
import com.google.common.collect.Lists;

public class DevDatabasePopulationServiceImpl implements DevDatabasePopulationService {
    private DeviceCreationService deviceCreationService;
    private DBPersistentDao dbPersistentDao;
    private PaoDao paoDao;
    private PaoDefinitionService paoDefinitionService;
    private DeviceDao deviceDao;
    private RoleDao roleDao;
    private static final Logger log = YukonLogManager.getLogger(DevDatabasePopulationServiceImpl.class);
    private CapControlCreationService capControlCreationService;
    private PaoScheduleDao paoScheduleDao;
    private StrategyDao strategyDao;
    private AccountService accountService;
    private HardwareUiService hardwareUiService;
    private CustomerAccountDao customerAccountDao;
    private YukonUserDao yukonUserDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private DevDatabaseExecutor devDatabaseExecutor;
    
    private class DevDatabaseExecutor {
        private DevDbSetupTask devDbSetupTask;
        boolean isCancelled = false;
        
        public DevDatabaseExecutor(DevDbSetupTask devDbSetupTask) {
            this.devDbSetupTask = devDbSetupTask;
        }

        public DevDbSetupTask getDevDbSetupTask() {
            return devDbSetupTask;
        }

        @Transactional
        public void doPopulate() {
            try {
                devDbSetupTask.setRunning(true);
                if (devDbSetupTask.isUpdateRoleProperties()) {
                    // feel free to make this sys admin group thing below better
                    LiteYukonGroup group = new LiteYukonGroup(-2, "System Administrator Grp");
                    updateAllRolePropertiesForGroup(group);
                }
                if (devDbSetupTask.getDevAMR().isCreate()) {
                    createAllCommChannels();
                    createAllCCUs();
                    createMeters(devDbSetupTask.getDevAMR());
                }
                if (devDbSetupTask.getDevCapControl().isCreate()) {
                    createCapControl(devDbSetupTask.getDevCapControl());
                }
                if (devDbSetupTask.getDevStars().isCreate()) {
                    createStars(devDbSetupTask.getDevStars());
                }

            } catch (Exception e) {
                devDbSetupTask.setHasRun(false);
                devDbSetupTask.setRunning(false);
                throw new RuntimeException("Error populating dev database. ", e.getCause());
            }
            devDbSetupTask.setHasRun(true);
            devDbSetupTask.setRunning(false);
        }
    }

    public synchronized void executeFullDatabasePopulation(DevDbSetupTask devDbSetupTask) {
        if (devDatabaseExecutor != null && devDatabaseExecutor.devDbSetupTask != null
            && devDatabaseExecutor.devDbSetupTask.isRunning()) {
            throw new RuntimeException("Already executing database population...");
        }
        devDatabaseExecutor = new DevDatabaseExecutor(devDbSetupTask);
        devDatabaseExecutor.doPopulate();
    }
    
    public DevDbSetupTask getExecuting() {
        if (devDatabaseExecutor == null) {
            return null;
        }
        return devDatabaseExecutor.getDevDbSetupTask();
    }
    
    public void cancelExecution() {
        devDatabaseExecutor.isCancelled = true;
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

    private void createAllCCUs() {
        log.info("Creating CCUs ...");
        DevCCU[] ccus = DevCCU.values();
        for (DevCCU ccu : ccus) {
            createCCU(ccu);
        }
    }

    private void createCCU(DevCCU devCCU) {
        try {
            // if this device exists already, then return
            LiteYukonPAObject liteYukonPAObject =
                deviceDao.getLiteYukonPAObject(devCCU.getName(),
                                               PaoCategory.DEVICE.getCategoryId(),
                                               PaoClass.TRANSMITTER.getPaoClassId(),
                                               PaoType.CCU711.getDeviceTypeId());
            if (liteYukonPAObject != null) {
                log.info("CCU with name " + devCCU.getName() + " already exists. Skipping.");
                return;
            }
        } catch (NotFoundException e) {
            // ignoring and continuing to create this ccu
        }

        LiteYukonPAObject commChan =
            paoDao.getLiteYukonPAObject(devCCU.getCommChannel().getName(),
                                        PaoCategory.PORT.getCategoryId(), PaoClass.PORT.getPaoClassId(),
                                        PaoType.TSERVER_SHARED.getDeviceTypeId());

        Integer portID = new Integer(commChan.getYukonID());

        CCU711 ccu = new CCU711();

        ccu.setPAOCategory(PaoCategory.DEVICE.toString());
        ccu.setPAOClass(PaoClass.TRANSMITTER.toString());
        ccu.setPAOName(devCCU.getName());
        ccu.setDeviceType(PaoType.CCU711.getDbString());

        ((RemoteBase) ccu).getDeviceDirectCommSettings().setPortID(portID);

        DirectPort port = (DirectPort) LiteFactory.createDBPersistent((LiteBase) commChan);
        Transaction<DirectPort> t = Transaction.createTransaction(Transaction.RETRIEVE, port);

        try {
            port = t.execute();
        } catch (TransactionException e) {
            throw new RuntimeException(e);
        }

        ((RemoteBase) ccu).getDeviceDialupSettings().setBaudRate(port.getPortSettings().getBaudRate());
        ((IDLCBase) ccu).getDeviceIDLCRemote().setPostCommWait(new Integer(0));
        ((IDLCBase) ccu).getDeviceIDLCRemote().setAddress(new Integer(devCCU.getAddress()));

        ((DeviceBase) ccu).setDeviceID(paoDao.getNextPaoId());

        String routeType = RouteTypes.STRING_CCU;

        RouteBase route = RouteFactory.createRoute(routeType);
        Integer routeID = paoDao.getNextPaoId();

        // make sure the name will fit in the DB!!
        route.setRouteName((((DeviceBase) ccu).getPAOName().length() <= TextFieldDocument.MAX_ROUTE_NAME_LENGTH
                ? ((DeviceBase) ccu).getPAOName()
                : ((DeviceBase) ccu).getPAOName()
                                    .substring(0, TextFieldDocument.MAX_ROUTE_NAME_LENGTH)));

        // set default values for route tables
        route.setDeviceID(((DeviceBase) ccu).getDevice().getDeviceID());
        route.setDefaultRoute(CtiUtilities.getTrueCharacter().toString());

        if (routeType.equalsIgnoreCase(RouteTypes.STRING_CCU)) {
            ((CCURoute) route).setCarrierRoute(new CarrierRoute(routeID));
        }

        SmartMultiDBPersistent newVal = createSmartDBPersistent((DeviceBase) ccu);
        newVal.addDBPersistent(route);

        dbPersistentDao.performDBChange(newVal, Transaction.INSERT);
        log.info("CCU with name " + devCCU.getName() + " created.");
    }

    private void createAllCommChannels() {
        log.info("Creating Comm Channels ...");
        DevCommChannel[] commChannels = DevCommChannel.values();
        for (DevCommChannel commChannel : commChannels) {
            createCommChannel(commChannel);
        }
    }

    private void createCommChannel(DevCommChannel commChannel) throws NotFoundException {
        try {
            // if this comm channel exists already, then return
            LiteYukonPAObject existingCommChannel = paoDao.getLiteYukonPAObject(commChannel.getName(), PaoCategory.PORT.getCategoryId(), PaoClass.PORT.getPaoClassId(), PaoType.TSERVER_SHARED.getDeviceTypeId());
            if (existingCommChannel != null) {
                log.info("Comm Channel with name " + commChannel.getName() + " already exists. Skipping.");
                return;
            }
        } catch (NotFoundException e) {
            // ignoring and continuing to create this comm channel
        }

        DirectPort directPort = PortFactory.createPort(commChannel.getPaoType().getDeviceTypeId());
        directPort.getCommPort().setCommonProtocol("IDLC");

        PortSettings portSettings = new PortSettings();
        portSettings.setPortID(directPort.getCommPort().getPortID());
        portSettings.setBaudRate(commChannel.getBaudRate());

        PortTerminalServer portTerminalServer =
            new PortTerminalServer(directPort.getCommPort().getPortID(),
                                               commChannel.getIpAddress(), commChannel.getPort());

        TerminalServerDirectPort terminalServerSharedPort = new TerminalServerDirectPort();
        terminalServerSharedPort.setCommPort(directPort.getCommPort());
        terminalServerSharedPort.setPortSettings(portSettings);
        terminalServerSharedPort.setPortTerminalServer(portTerminalServer);
        terminalServerSharedPort.setPAOCategory(PaoCategory.PORT.toString());
        terminalServerSharedPort.setPAOClass(PaoClass.PORT.toString());
        terminalServerSharedPort.setPAOName(commChannel.getName());
        terminalServerSharedPort.setPortType(PaoType.TSERVER_SHARED.getPaoTypeName());
        terminalServerSharedPort.setPortID(directPort.getCommPort().getPortID());
        terminalServerSharedPort.setPortName(commChannel.getName());

        dbPersistentDao.performDBChange(terminalServerSharedPort, Transaction.INSERT);
        log.info("Comm Channel with name " + commChannel.getName() + " created.");
    }

    private void createMeters(DevAMR devAMR) {
        if (devAMR.isCreateCartObjects()) {
            DevMeter[] meters = DevMeter.values();
            for (DevMeter meter : meters) {
                createMeter(devAMR, meter);
            }
        }
        int addressCount = 0;
        int address = devAMR.getAddressRangeMin();
        for (DevPaoType meterType: devAMR.getMeterTypes()) {
            if (meterType.isCreate()) {
                for (int i = 0; i < devAMR.getNumAdditionalMeters(); i++) {
                    address = devAMR.getAddressRangeMin() + addressCount;
                    String meterName = meterType.getPaoType().getPaoTypeName() + " " + address;
                    createMeter(devAMR, meterType.getPaoType().getDeviceTypeId(), meterName, address, devAMR.getRouteId(), true);
                    addressCount++;
                }
            }
        }
    }
    
    private void createMeter(DevAMR devAMR, DevMeter meter) {
        Integer routeId = paoDao.getRouteIdForRouteName(DevCCU.CCU_711_SIM.getName());
        if (routeId == null) {
            throw new RuntimeException("Couldn't find route with name " + DevCCU.CCU_711_SIM.getName());
        }
        createMeter(devAMR, meter.getPaoType().getDeviceTypeId(), meter.getName(), meter.getAddress(), routeId, true);
    }

    private void createMeter(DevAMR devAMR, int type, String name, int address, int routeId, boolean createPoints) {
        checkIsCancelled();
        if (address >= devAMR.getAddressRangeMax()) {
            log.info("Meter with name " + name + " has address greater than max address range of "
                     + devAMR.getAddressRangeMax() + " . Skipping.");
            devAMR.incrementFailureCount();
            return;
        }
        try {
            // if this device exists already, then return
            SimpleDevice existingMeter = deviceDao.findYukonDeviceObjectByName(name);
            if (existingMeter != null) {
                log.info("Meter with name " + name + " already exists. Skipping.");
                devAMR.incrementFailureCount();
                return;
            }
        } catch (NotFoundException e) {
            // ignoring and continuing to create this meter
        }

        deviceCreationService.createCarrierDeviceByDeviceType(type, name, address, routeId, createPoints);
        devAMR.incrementSuccessCount();
        log.info("Meter with name " + name + " created.");
    }
    
    // This is a pretty terrible way of accomplishing this... but good enough for now
    private void checkIsCancelled() {
        if (devDatabaseExecutor.isCancelled) {
            devDatabaseExecutor.isCancelled = false;
            log.info("Development database setup cancelled.");
            throw new RuntimeException("Execution cancelled.");
        }
    }

    private void createStars(DevStars devStars) {
        int inventoryIdIterator = devStars.getDevStarsHardware().getSerialNumMin();
        // Account Hardware
        for (UpdatableAccount updatableAccount: devStars.getDevStarsAccounts().getAccounts()) {
            if (createAccount(devStars, updatableAccount)) {
                for (int i = 0; i < devStars.getDevStarsHardware().getNumPerAccount(); i++) {
                    for (DevHardwareType devHardwareType: devStars.getDevStarsHardware().getHardwareTypes()) {
                        if (devHardwareType.isCreate()) {
                            CustomerAccount customerAccount = customerAccountDao.getByAccountNumber(updatableAccount.getAccountNumber(), devStars.getEnergyCompany().getEnergyCompanyId());
                            createHardware(devStars, devHardwareType, customerAccount.getAccountId(), inventoryIdIterator);
                            inventoryIdIterator++;
                        }
                    }
                }
            }
        }
        // Warehouse (extra) Hardware
        for (int i = 0; i < devStars.getDevStarsHardware().getNumExtra(); i++) {
            for (DevHardwareType devHardwareType: devStars.getDevStarsHardware().getHardwareTypes()) {
                if (devHardwareType.isCreate()) {
                    createHardware(devStars, devHardwareType, 0, inventoryIdIterator);
                    inventoryIdIterator++;
                }
            }
        }
    }

    private boolean createAccount(DevStars devStars, UpdatableAccount updatableAccount) {
        checkIsCancelled();
        LiteStarsEnergyCompany energyCompany = devStars.getEnergyCompany();
        if (!canAddStarsAccount(devStars, updatableAccount, energyCompany.getEnergyCompanyId())) {
            devStars.addToFailureCount(devStars.getDevStarsHardware().getNumHardwarePerAccount() + 1);
            return false;
        }
        accountService.addAccount(updatableAccount, energyCompany.getUser());
        log.info("STARS Account added: " + updatableAccount.getAccountNumber() + " to EC: " + energyCompany.getName());
        devStars.incrementSuccessCount();
        return true;
    }
    
    private void createHardware(DevStars devStars, DevHardwareType devHardwareType, int accountId, int inventoryIdIterator) {
        checkIsCancelled();
        LiteStarsEnergyCompany energyCompany = devStars.getEnergyCompany();
        HardwareDto hardwareDto = getHardwareDto(devHardwareType, energyCompany, inventoryIdIterator);
        if (!canAddStarsHardware(devStars, hardwareDto)) {
            devStars.incrementFailureCount();
            return;
        }
        try {
            hardwareUiService.createHardware(hardwareDto, accountId, energyCompany.getUser());
            devStars.incrementSuccessCount();
            if (accountId == 0) {
                log.info("STARS Hardware added: " + hardwareDto.getDisplayName() + " to warehouse");
            } else {
                log.info("STARS Hardware added: " + hardwareDto.getDisplayName() + " to accountId: " + accountId);
            }
        } catch (ObjectInOtherEnergyCompanyException e) {
            // We should have caught this already in canAddStarsHardware, but createHardware requires it
            log.info("Hardware Object " + hardwareDto.getDisplayName() + " is in another energy company.");
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            devStars.incrementFailureCount();
        }
    }
    
    private HardwareDto getHardwareDto(DevHardwareType devHardwareType, LiteStarsEnergyCompany energyCompany, int inventoryId) {
        String inventoryIdIteratorString = String.valueOf(inventoryId);
        HardwareType hardwareType = devHardwareType.getHardwareType();
        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setInventoryId(inventoryId);
        hardwareDto.setDeviceId(0);
        String hardwareName = devHardwareType.getHardwareType().name() + " " + inventoryIdIteratorString;
        hardwareDto.setDisplayName(hardwareName);
        hardwareDto.setDisplayLabel(hardwareName);
        hardwareDto.setDisplayType(hardwareName);
        hardwareDto.setSerialNumber(inventoryIdIteratorString);
        hardwareDto.setDeviceNotes("Device Notes for inventoryId: " + inventoryIdIteratorString);
        /* Non-ZigBee two way LCR fields */
        if (hardwareType.isTwoWay() && !hardwareType.isZigbee()) {
            hardwareDto.setTwoWayDeviceName("twoWayDeviceName" + inventoryIdIteratorString);
            hardwareDto.setCreatingNewTwoWayDevice(true);
        }
        String ccuName = DevCCU.CCU_711_SIM.getName();
        Integer routeId = paoDao.getRouteIdForRouteName(ccuName);
        if (routeId == null) {
            throw new RuntimeException("Couldn't find route with name " + ccuName);
        }
        hardwareDto.setRouteId(routeId);
        hardwareDto.setEnergyCompanyId(energyCompany.getEnergyCompanyId());
        hardwareDto.setHardwareType(hardwareType);
        YukonListEntry typeEntry = energyCompany.getYukonListEntry(hardwareType.getDefinitionId());
        hardwareDto.setHardwareTypeEntryId(typeEntry.getEntryID());
        hardwareDto.setDisplayType(hardwareType.name());
        hardwareDto.setCategoryName("Category");
        hardwareDto.setAltTrackingNumber("4");
//            private Integer voltageEntryId;
        hardwareDto.setFieldInstallDate(new Date());
//            private Date fieldReceiveDate;
//            private Date fieldRemoveDate;
//            private Integer serviceCompanyId;
//            private Integer warehouseId;
        hardwareDto.setInstallNotes("Install notes: This hardware went in super easy.");
//            private Integer deviceStatusEntryId;
//            private Integer originalDeviceStatusEntryId;
        HardwareHistory hardwareHistory = new HardwareHistory();
        hardwareHistory.setAction("Install performed.");
        hardwareHistory.setDate(new Date());
        hardwareDto.setHardwareHistory(Lists.newArrayList(hardwareHistory));
        
        /* Non-ZigBee two way LCR fields */
        if (hardwareType.isTwoWay() && !hardwareType.isZigbee()) {
            hardwareDto.setCreatingNewTwoWayDevice(true);
        }
        return hardwareDto;
    }
    
    private boolean canAddStarsHardware(DevStars devStars, HardwareDto hardwareDto) {
        int serialNum = Integer.valueOf(hardwareDto.getSerialNumber());
        if (serialNum >= devStars.getDevStarsHardware().getSerialNumMax()) {
            log.info("Hardware Object " + hardwareDto.getDisplayName() + " cannot be added. Max of "
                     + devStars.getDevStarsHardware().getSerialNumMax() + " reached.");
            return false;
        }
        // check energy company
        boolean isSerialNumInUse = hardwareUiService.isSerialNumberInEC(hardwareDto);
        if (isSerialNumInUse) {
            log.info("Hardware Object " + hardwareDto.getDisplayName() + " already exists in the database assigned to ec: "
                     + hardwareDto.getEnergyCompanyId());
            return false;
        }
        // check warehouse
        try {
            starsInventoryBaseDao.getByInventoryId(hardwareDto.getInventoryId());
        } catch (NotFoundException e) {
            return true;
        }
        
        log.info("Hardware Object " + hardwareDto.getDisplayName() + " already exists in the database (warehouse).");
        return false;
    }
    
    private boolean canAddStarsAccount(DevStars devStars, UpdatableAccount updatableAccount, int energyCompanyId) {
        String accountNumber = updatableAccount.getAccountNumber();
        Integer acctNumInt = Integer.valueOf(accountNumber);
        if (acctNumInt >= devStars.getDevStarsAccounts().getAccountNumMax()) {
            log.info("Account " + accountNumber + " could not be added: Reached max account number of ." + devStars.getDevStarsAccounts().getAccountNumMax());
            return false;
        }
        // Checks to see if the account number is already being used.
        try {
            CustomerAccount customerAccount = customerAccountDao.getByAccountNumber(accountNumber, energyCompanyId);
            if (customerAccount != null){
                log.info("Account " + accountNumber + " could not be added: The provided account number already exists.");
                return false;
            }
        } catch (NotFoundException e ) {
            // Account doesn't exist
        }
        if(yukonUserDao.findUserByUsername( updatableAccount.getAccountDto().getUserName() ) != null) {
            log.info("Account " + accountNumber + " could not be added: The provided username already exists.");
            return false;
        }
        return true;
    }
    
    private void createCapControlObject(DevCapControl devCapControl, int type, String name, boolean disabled, int portId) {
        try {
            LiteYukonPAObject litePao = getPaoByName(name);
            if (litePao != null) {
                log.info("CapControl object with name " + name + " already exists. Skipping");
                devCapControl.incrementFailureCount();
                return;
            }
            capControlCreationService.create(type, name, disabled, portId);
            devCapControl.incrementSuccessCount();
            log.info("CapControl object with name " + name + " created.");
        } catch(RuntimeException e) {
            log.info("CapControl object with name " + name + " already exists. Skipping");
            devCapControl.incrementFailureCount();
        }
    }
    
    private void createCapControl(DevCapControl devCapControl) {
        int offset = devCapControl.getOffset();
        for (int areaIndex = offset; areaIndex  < offset + devCapControl.getNumAreas(); areaIndex++) { // Areas
            String areaName = createArea(devCapControl, areaIndex);
            for (int subIndex = offset; subIndex < offset + devCapControl.getNumSubs(); subIndex++) { // Substations
                String subName = createSubstation(devCapControl, areaIndex, areaName, subIndex);
                for (int subBusIndex = offset; subBusIndex < offset + devCapControl.getNumSubBuses(); subBusIndex++) { // Substations Buses
                    String subBusName = createAndAssignSubstationBus(devCapControl, areaIndex, subIndex, subName, subBusIndex);
                    for (int feederIndex = offset; feederIndex < offset + devCapControl.getNumFeeders(); feederIndex++) { // Feeders
                        String feederName = createAndAssignFeeder(devCapControl, areaIndex, subIndex, subBusIndex, subBusName, feederIndex);
                        for (int capBankIndex = offset; capBankIndex < offset + devCapControl.getNumCapBanks(); capBankIndex++) { // CapBanks
                            String capBankName = createAndAssignCapBank(devCapControl, areaIndex, subIndex, subBusIndex, feederIndex, feederName, capBankIndex);
                            for (int cbcIndex = offset; cbcIndex < offset + devCapControl.getNumCBCs(); cbcIndex++) { // CBCs
                                createAndAssignCBCs(devCapControl, areaIndex, subIndex, subBusIndex, feederIndex, feederName, capBankIndex, capBankName, cbcIndex);
                            }
                        }
                    }
                }
            }
        }
        
        for (int regIndex = offset; regIndex < offset + devCapControl.getNumRegulators(); regIndex++) { //Regulators
            createRegulators(devCapControl, regIndex);
        }
    }

    private void logCapControlAddignment(String child, String parent) {
        log.info(child + " assigned to " + parent);
    }
    
    private void createRegulators(DevCapControl devCapControl, int regIndex) {
        for (DevPaoType regType: devCapControl.getRegulatorTypes()) {
            if (regType.isCreate()) {
                String regName = regType.getPaoType().getPaoTypeName() + " " + Integer.toString(regIndex);
                createCapControlObject(devCapControl, regType.getPaoType().getDeviceTypeId(), regName, false, 0);
            }
        }
    }
    
    private void createAndAssignCBCs(DevCapControl devCapControl, int areaIndex, int subIndex, int subBusIndex, int feederIndex, String feederName,
                             int capBankIndex, String capBankName, int cbcIndex) {
        for (DevPaoType cbcType: devCapControl.getCbcTypes()) {
            if (cbcType.isCreate()) {
                String cbcName = cbcType.getPaoType().getPaoTypeName() + " "+ Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex) + Integer.toString(capBankIndex) + Integer.toString(cbcIndex);
                createCapControlCBC(devCapControl, cbcType.getPaoType().getDeviceTypeId(), cbcName, false, DevCommChannel.COMM_CHANNEL_1);
                int cbcPaoId = getPaoIdByName(cbcName);
                capControlCreationService.assignController(cbcPaoId, cbcType.getPaoType(), capBankName);
                logCapControlAddignment(cbcName, capBankName);
            }
        }
    }

    private String createAndAssignCapBank(DevCapControl devCapControl, int areaIndex, int subIndex, int subBusIndex, int feederIndex, String feederName,
                             int capBankIndex) {
        String capBankName = "CapBank " + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex) + Integer.toString(capBankIndex);
        createCapControlObject(devCapControl, CapControlTypes.CAP_CONTROL_CAPBANK, capBankName, false, 0);
        int capBankPaoId = getPaoIdByName(capBankName);
        capControlCreationService.assignCapbank(capBankPaoId, feederName);
        logCapControlAddignment(capBankName, feederName);
        return capBankName;
    }

    private String createAndAssignFeeder(DevCapControl devCapControl, int areaIndex, int subIndex, int subBusIndex, String subBusName, int feederIndex) {
        String feederName = "Feeder " + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex);
        createCapControlObject(devCapControl, CapControlTypes.CAP_CONTROL_FEEDER, feederName, false, 0);
        int feederPaoId = getPaoIdByName(feederName);
        capControlCreationService.assignFeeder(feederPaoId, subBusName);
        logCapControlAddignment(feederName, subBusName);
        return feederName;
    }

    private String createAndAssignSubstationBus(DevCapControl devCapControl, int areaIndex, int subIndex, String subName, int subBusIndex) {
        String subBusName = "Substation Bus " + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex);
        createCapControlObject(devCapControl, CapControlTypes.CAP_CONTROL_SUBBUS, subBusName, false, 0);
        int subBusPaoId = getPaoIdByName(subBusName);
        capControlCreationService.assignSubstationBus(subBusPaoId, subName);
        logCapControlAddignment(subBusName, subName);
        return subBusName;
    }

    private String createSubstation(DevCapControl devCapControl, int areaIndex, String areaName, int subIndex) {
        String subName = "Substation " + Integer.toString(areaIndex) + Integer.toString(subIndex);
        createCapControlObject(devCapControl, CapControlTypes.CAP_CONTROL_SUBSTATION, subName, false, 0);
        int subPaoId = getPaoIdByName(subName);
        capControlCreationService.assignSubstation(subPaoId, areaName);
        logCapControlAddignment(subName, areaName);
        return subName;
    }

    private String createArea(DevCapControl devCapControl, int areaIndex) {
        String areaName = "Area " + Integer.toString(areaIndex);
        createCapControlObject(devCapControl, CapControlTypes.CAP_CONTROL_AREA, areaName, false, 0);
        return areaName;
    }
    
    private void createCapControlCBC(DevCapControl devCapControl, int type, String name, boolean disabled, DevCommChannel commChannel) {
        checkIsCancelled();
        List<LiteYukonPAObject> commChannels = paoDao.getLiteYukonPaoByName(commChannel.getName(), false);
        if (commChannels.size() != 1) {
            throw new RuntimeException("Couldn't find comm channel " + commChannel.getName());
        }
        int portId = commChannels.get(0).getPaoIdentifier().getPaoId();
        createCapControlObject(devCapControl, type, name, disabled, portId);
    }
    
    private void createCapControlSchedule(DevCapControl devCapControl, int type, String name, boolean disabled) {
        List<PAOSchedule> allPaoScheduleNames = paoScheduleDao.getAllPaoScheduleNames();
        for (PAOSchedule paoSchedule : allPaoScheduleNames) {
            if (paoSchedule.getScheduleName().equalsIgnoreCase(name)) {
                log.info("CapControl object with name " + name + " already exists. Skipping");
                return;
            }
        }
        createCapControlObject(devCapControl, type, name, disabled, 0);
    }
    
    private void createCapControlStrategy(DevCapControl devCapControl, int type, String name, boolean disabled) {
        List<LiteCapControlStrategy> allLiteStrategies = strategyDao.getAllLiteStrategies();
        for (LiteCapControlStrategy liteStrategy : allLiteStrategies) {
            if (liteStrategy.getStrategyName().equalsIgnoreCase(name)) {
                log.info("CapControl object with name " + name + " already exists. Skipping");
                return;
            }
        }
        createCapControlObject(devCapControl, type, name, false, 0);
    }
    
    private SmartMultiDBPersistent createSmartDBPersistent(DeviceBase deviceBase) {
        if (deviceBase == null)
            return null;

        SmartMultiDBPersistent smartDB = new SmartMultiDBPersistent();
        smartDB.addOwnerDBPersistent(deviceBase);

        SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(deviceBase);
        List<PointBase> defaultPoints = paoDefinitionService.createDefaultPointsForPao(yukonDevice);

        for (PointBase point : defaultPoints) {
            smartDB.addDBPersistent(point);
        }

        return smartDB;
    }

    private LiteYukonPAObject getPaoByName(String paoName) {
        List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(paoName, false);
        
        if (paos.size() != 1) {
            return null;
        }
        LiteYukonPAObject litePao = paos.get(0);
        
        return litePao;
    }
    
    private int getPaoIdByName(String paoName) {
        LiteYukonPAObject litePao = getPaoByName(paoName);
        
        if (litePao == null) {
            return -1;
        }
        
        return litePao.getYukonID();
    }

    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    @Autowired
    public void setDeviceCreationService(
                                         DeviceCreationService deviceCreationService) {
        this.deviceCreationService = deviceCreationService;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Autowired
    public void setPaoDefinitionService(
                                        PaoDefinitionService paoDefinitionService) {
        this.paoDefinitionService = paoDefinitionService;
    }

    @Autowired
    public void setCapControlCreationService(CapControlCreationService capControlCreationService) {
        this.capControlCreationService = capControlCreationService;
    }
    
    @Autowired
    public void setPaoScheduleDao(PaoScheduleDao paoScheduleDao) {
        this.paoScheduleDao = paoScheduleDao;
    }
    
    @Autowired
    public void setStrategyDao(StrategyDao strategyDao) {
        this.strategyDao = strategyDao;
    }
    
    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    @Autowired
    public void setHardwareUiService(HardwareUiService hardwareUiService) {
        this.hardwareUiService = hardwareUiService;
    }
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
}

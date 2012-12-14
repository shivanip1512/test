package com.cannontech.web.support.development.database.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.loggers.CommandRequestExecutorEventLogService;
import com.cannontech.common.events.loggers.CommandScheduleEventLogService;
import com.cannontech.common.events.loggers.DatabaseMigrationEventLogService;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.events.loggers.InventoryConfigEventLogService;
import com.cannontech.common.events.loggers.MeteringEventLogService;
import com.cannontech.common.events.loggers.OutageEventLogService;
import com.cannontech.common.events.loggers.RfnDeviceEventLogService;
import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.events.loggers.ValidationEventLogService;
import com.cannontech.common.events.loggers.VeeReviewEventLogService;
import com.cannontech.common.events.loggers.ZigbeeEventLogService;
import com.cannontech.common.i18n.Displayable;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.support.development.database.objects.DevEventLog;

public class DevEventLogCreationService {

    protected static final Logger log = YukonLogManager.getLogger(DevEventLogCreationService.class);

    private static AccountEventLogService accountEventLogService;
    private static CommandRequestExecutorEventLogService commandRequestExecutorEventLogService;
    private static CommandScheduleEventLogService commandScheduleEventLogService;
    private static DatabaseMigrationEventLogService databaseMigrationEventLogService;
    private static DemandResponseEventLogService demandResponseEventLogService;
    private static HardwareEventLogService hardwareEventLogService;
    private static InventoryConfigEventLogService inventoryConfigEventLogService;
    private static MeteringEventLogService meteringEventLogService;
    private static OutageEventLogService outageEventLogService;
    private static RfnDeviceEventLogService rfnDeviceEventLogService;
    private static StarsEventLogService starsEventLogService;
    private static SystemEventLogService systemEventLogService;
    private static ValidationEventLogService validationEventLogService;
    private static VeeReviewEventLogService veeReviewEventLogService;
    private static ZigbeeEventLogService zigbeeEventLogService;

    public interface DevEventLogExecutable {
        public void execute(DevEventLog devEventLog);
        public int numMethods();
    }

    public static enum LogType implements Displayable { 
        // Number of calls per service.
        ACCOUNT_EVENT_LOG("AccountEventLogService", AccountEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());
                String accountNumber = devEventLog.getIndicatorString() + "Account_42";
                String oldAccountNumber = devEventLog.getIndicatorString() + "Old_Account_152";
                String newAccountNumber = devEventLog.getIndicatorString() + "New_Account_124";
                String serialNumber = devEventLog.getIndicatorString() + "Serial_#_82";
                String applianceType = devEventLog.getIndicatorString() + "Toaster Appliance";
                String deviceName = devEventLog.getIndicatorString() + "aDevice";
                String programName = devEventLog.getIndicatorString() + "SudevEventLog.getIndicatorString() + mmerProgram";
                String oldCompanyName = "Old Electric Inc";
                String newCompanyName = devEventLog.getIndicatorString() + "New Electric Inc";
                String contactName = devEventLog.getIndicatorString() + "Roberto";
                String oldContactName = devEventLog.getIndicatorString() + "Old Henry";
                String newContactName = devEventLog.getIndicatorString() + "New Henry";
                String oldCustomerType = devEventLog.getIndicatorString() + "CustomerTypeOld";
                String newcustomerType = devEventLog.getIndicatorString() + "CustomerTypeNew";
                String loadGroupName = devEventLog.getIndicatorString() + "Load Group 1";
                Instant startDate = Instant.now().minus(Duration.standardDays(5));
                Instant stopDate = Instant.now();
                String workOrderNumber = devEventLog.getIndicatorString() + "8675309";
                String scheduleName = devEventLog.getIndicatorString() + "Schedule 1";
                Double heatTemperature = 90.0;
                Double coolTemperature = 70.0;
                String mode = devEventLog.getIndicatorString() + "OperationMode";
                boolean holdTemperature = true;
                String oldThermostatLabel = devEventLog.getIndicatorString() + "OldThermo";
                String newThermostatLabel = devEventLog.getIndicatorString() + "NewThermo";
                String newScheduleName = devEventLog.getIndicatorString() + "OldSchedule 1";
                String oldScheduleName = devEventLog.getIndicatorString() + "New Schedule 1";
                int optOutsAdded = 100;
                ReadableInstant optOutStartDate = Instant.now().minus(Duration.standardDays(5));
                ReadableInstant optOutStopDate = Instant.now();

                accountEventLogService.accountCreationAttempted(yukonUser, accountNumber, devEventLog.getEventSource());
                accountEventLogService.accountUpdateCreationAttempted(yukonUser, accountNumber, devEventLog.getEventSource());
                accountEventLogService.accountDeletionAttempted(yukonUser, accountNumber, devEventLog.getEventSource());
                accountEventLogService.accountUpdateAttempted(yukonUser, accountNumber, devEventLog.getEventSource());

                //AccountServiceLevel
                accountEventLogService.accountAdded(yukonUser, accountNumber);
                accountEventLogService.accountUpdated(yukonUser, accountNumber);
                accountEventLogService.accountDeleted(yukonUser, accountNumber);
                accountEventLogService.accountNumberChanged(yukonUser, oldAccountNumber, newAccountNumber);

                //*CONTACTINFO*
                accountEventLogService.contactAdded(yukonUser, accountNumber, contactName);
                accountEventLogService.contactUpdated(yukonUser, accountNumber, contactName);
                accountEventLogService.contactRemoved(yukonUser, accountNumber, contactName);
                accountEventLogService.contactNameChanged(yukonUser, accountNumber, oldContactName, newContactName);
                accountEventLogService.customerTypeChanged(yukonUser, accountNumber, oldCustomerType, newcustomerType);
                accountEventLogService.companyNameChanged(yukonUser, accountNumber, oldCompanyName, newCompanyName);

                //Enrollment
                accountEventLogService.enrollmentAttempted(yukonUser, accountNumber, deviceName, programName, loadGroupName, devEventLog.getEventSource());
                accountEventLogService.enrollmentModificationAttempted(yukonUser, accountNumber, devEventLog.getEventSource());
                accountEventLogService.unenrollmentAttempted(yukonUser, accountNumber, deviceName, programName, loadGroupName, devEventLog.getEventSource());

                //EnrollmentServiceLevel
                accountEventLogService.deviceEnrolled(yukonUser, accountNumber, deviceName, programName, loadGroupName);
                accountEventLogService.deviceUnenrolled(yukonUser, accountNumber, deviceName, programName, loadGroupName);

                //OptOuts
                accountEventLogService.optOutLimitReductionAttempted(yukonUser, accountNumber, serialNumber, devEventLog.getEventSource());
                accountEventLogService.optOutLimitIncreaseAttempted(yukonUser, accountNumber, serialNumber, devEventLog.getEventSource());
                accountEventLogService.optOutLimitResetAttempted(yukonUser, accountNumber, serialNumber, devEventLog.getEventSource());
                accountEventLogService.optOutResendAttempted(yukonUser, accountNumber, serialNumber, devEventLog.getEventSource());

                accountEventLogService.optOutAttempted(yukonUser, accountNumber, serialNumber, startDate, devEventLog.getEventSource());
                accountEventLogService.optOutCancelAttempted(yukonUser, accountNumber, serialNumber, optOutStartDate, optOutStopDate, devEventLog.getEventSource());
                accountEventLogService.scheduledOptOutCancelAttempted(yukonUser, accountNumber, serialNumber, devEventLog.getEventSource());
                accountEventLogService.activeOptOutCancelAttempted(yukonUser, accountNumber, serialNumber, devEventLog.getEventSource());

                //OptOutServiceLevel
                accountEventLogService.optOutLimitIncreased(yukonUser, accountNumber, deviceName, optOutsAdded);
                accountEventLogService.optOutLimitReset(yukonUser, accountNumber, deviceName);
                accountEventLogService.optOutResent(yukonUser, accountNumber, deviceName);

                accountEventLogService.deviceOptedOut(yukonUser, accountNumber, deviceName, startDate, stopDate);
                accountEventLogService.optOutCanceled(yukonUser, accountNumber, deviceName);

                //Appliance
                accountEventLogService.applianceAdditionAttempted(yukonUser, accountNumber, applianceType, deviceName, programName, devEventLog.getEventSource());
                accountEventLogService.applianceUpdateAttempted(yukonUser, accountNumber, applianceType, deviceName, programName, devEventLog.getEventSource());
                accountEventLogService.applianceDeletionAttempted(yukonUser, accountNumber, applianceType, deviceName, programName, devEventLog.getEventSource());

                //ApplianceServiceLevel
                accountEventLogService.applianceAdded(yukonUser, accountNumber, applianceType, deviceName, programName);
                accountEventLogService.applianceUpdated(yukonUser, accountNumber, applianceType, deviceName, programName);
                accountEventLogService.applianceDeleted(yukonUser, accountNumber, applianceType, deviceName, programName);

                //WorkOrder
                accountEventLogService.workOrderCreationAttempted(yukonUser, accountNumber, workOrderNumber, devEventLog.getEventSource());
                accountEventLogService.workOrderUpdateAttempted(yukonUser, accountNumber, workOrderNumber, devEventLog.getEventSource());
                accountEventLogService.workOrderDeletionAttempted(yukonUser, accountNumber, workOrderNumber, devEventLog.getEventSource());

                //WorkOrderServiceLevel
                accountEventLogService.workOrderCreated(yukonUser, accountNumber, workOrderNumber);
                accountEventLogService.workOrderUpdated(yukonUser, accountNumber, workOrderNumber);
                accountEventLogService.workOrderDeleted(yukonUser, accountNumber, workOrderNumber);

                //ThermostatSchedule
                accountEventLogService.thermostatScheduleSavingAttempted(yukonUser, accountNumber, serialNumber, scheduleName, devEventLog.getEventSource());
                accountEventLogService.thermostatScheduleSendDefaultAttempted(yukonUser, serialNumber, devEventLog.getEventSource());
                accountEventLogService.thermostatScheduleSendAttempted(yukonUser, serialNumber, scheduleName, devEventLog.getEventSource());
                accountEventLogService.thermostatScheduleDeleteAttempted(yukonUser, accountNumber, scheduleName, devEventLog.getEventSource());
                accountEventLogService.thermostatRunProgramAttempted(yukonUser, serialNumber, devEventLog.getEventSource());
                accountEventLogService.thermostatRunProgramAttempted(yukonUser, accountNumber, serialNumber, devEventLog.getEventSource());
                accountEventLogService.thermostatManualSetAttempted(yukonUser, accountNumber, serialNumber, heatTemperature, coolTemperature, mode, holdTemperature, devEventLog.getEventSource());
                accountEventLogService.thermostatLabelChangeAttempted(yukonUser, serialNumber, oldThermostatLabel, newThermostatLabel, devEventLog.getEventSource());

                //ThermostatScheduleServiceLevel
                accountEventLogService.thermostatScheduleSaved(accountNumber, scheduleName);
                accountEventLogService.thermostatScheduleDeleted(accountNumber, scheduleName);
                accountEventLogService.thermostatManuallySet(yukonUser, serialNumber);
                accountEventLogService.thermostatLabelChanged(yukonUser, accountNumber, serialNumber, oldThermostatLabel, newThermostatLabel);

                accountEventLogService.thermostatScheduleNameChanged(yukonUser, oldScheduleName, newScheduleName);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 57;}
        }),
        COMMAND_REQUEST_EXECUTOR_EVENT_LOG("CommandRequestExecutorEventLogService", CommandRequestExecutorEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                int creId = 10;
                int contextId = 10;
                String currentCommandString = devEventLog.getIndicatorString() + "CurrentCommand";
                String error = devEventLog.getIndicatorString() + "error";
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());

                commandRequestExecutorEventLogService.commandFailedToTransmit(creId, contextId, DeviceRequestType.GROUP_COMMAND, currentCommandString, error, yukonUser);
                commandRequestExecutorEventLogService.foundFailedCre(creId);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 2;}
        }),
        COMMAND_SCHEDULE_EVENT_LOG("CommandScheduleEventLogService", CommandScheduleEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());
                int commandScheduleId = 12;

                commandScheduleEventLogService.allSchedulesDisabled(yukonUser);
                commandScheduleEventLogService.scheduleCreated(yukonUser, commandScheduleId);
                commandScheduleEventLogService.scheduleDeleted(yukonUser, commandScheduleId);
                commandScheduleEventLogService.scheduleDisabled(yukonUser, commandScheduleId);
                commandScheduleEventLogService.scheduleEnabled(yukonUser, commandScheduleId);
                commandScheduleEventLogService.scheduleUpdated(yukonUser, commandScheduleId);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 6;}
        }),
        DATABASE_MIGRATION_EVENT_LOG("DatabaseMigrationEventLogService", DatabaseMigrationEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());
                String fileName = "fakeFile.csv";

                databaseMigrationEventLogService.startingExport(yukonUser, fileName);
                databaseMigrationEventLogService.startingImport(yukonUser, fileName);
                databaseMigrationEventLogService.startingValidation(yukonUser, fileName);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 3;}
        }),
        
        //36
        DEMAND_RESPONSE_EVENT_LOG("DemandResponseEventLogService", DemandResponseEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());
                String scenarioName = devEventLog.getIndicatorString() + "ScenarioName";
                Instant startIns = Instant.now().minus(Duration.standardDays(5));
                Instant stopIns = Instant.now();
                String controlAreaName = devEventLog.getIndicatorString() + "ControlAreaName";

                Date startDate = startIns.toDate();
                Date stopDate = stopIns.toDate();

                Double threshold1 = 1.0;
                Double threshold2 = 2.0;
                Double offset1 = 3.0;
                Double offset2 = 4.0;

                int startSeconds = 10;
                int stopSeconds = 10;
                int shedSeconds = 10;

                String programName = devEventLog.getIndicatorString() + "ProgramName";
                String gearName = devEventLog.getIndicatorString() + "GearName";
                String loadGroupName =  devEventLog.getIndicatorString() + "LoadGroupName";

                boolean overrideConstraints = true;
                boolean stopScheduled = true;

                demandResponseEventLogService.threeTierScenarioStarted(yukonUser, scenarioName);
                demandResponseEventLogService.threeTierScenarioStopped(yukonUser, scenarioName);
                //ControlArealogging
                demandResponseEventLogService.controlAreaStarted(controlAreaName, startDate);
                demandResponseEventLogService.controlAreaStopped(controlAreaName, stopDate);
                demandResponseEventLogService.threeTierControlAreaTriggersChanged(yukonUser, controlAreaName, threshold1, offset1, threshold2, offset2);
                demandResponseEventLogService.controlAreaTriggersChanged(controlAreaName, threshold1, offset1, threshold2, offset2);
                demandResponseEventLogService.threeTierControlAreaTimeWindowChanged(yukonUser, controlAreaName, startSeconds, stopSeconds);
                demandResponseEventLogService.controlAreaTimeWindowChanged(controlAreaName, startSeconds, stopSeconds);
                demandResponseEventLogService.threeTierControlAreaStarted(yukonUser, controlAreaName);
                demandResponseEventLogService.threeTierControlAreaStopped(yukonUser, controlAreaName);
                demandResponseEventLogService.threeTierControlAreaEnabled(yukonUser, controlAreaName);
                demandResponseEventLogService.controlAreaEnabled(controlAreaName);
                demandResponseEventLogService.threeTierControlAreaDisabled(yukonUser, controlAreaName);
                demandResponseEventLogService.controlAreaDisabled(controlAreaName);
                demandResponseEventLogService.threeTierControlAreaPeakReset(yukonUser, controlAreaName);
                demandResponseEventLogService.controlAreaPeakReset(controlAreaName);
                //Programlogging
                demandResponseEventLogService.threeTierProgramScheduled(yukonUser, programName, startDate);
                demandResponseEventLogService.programScheduled(programName, overrideConstraints, gearName, startDate, stopScheduled, stopDate);
                demandResponseEventLogService.threeTierProgramStopped(yukonUser, programName, stopDate);
                demandResponseEventLogService.programStopped(programName, stopDate, gearName);
                demandResponseEventLogService.threeTierProgramStopScheduled(yukonUser, programName, stopDate);
                demandResponseEventLogService.programStopScheduled(programName, stopDate, gearName);
                demandResponseEventLogService.threeTierProgramChangeGear(yukonUser, programName);
                demandResponseEventLogService.programChangeGear(programName, gearName);
                demandResponseEventLogService.threeTierProgramEnabled(yukonUser, programName);
                demandResponseEventLogService.programEnabled(programName);
                demandResponseEventLogService.threeTierProgramDisabled(yukonUser, programName);
                demandResponseEventLogService.programDisabled(programName);
                //LoadGrouplogging
                demandResponseEventLogService.threeTierLoadGroupShed(yukonUser, loadGroupName, shedSeconds);
                demandResponseEventLogService.loadGroupShed(loadGroupName, shedSeconds);
                demandResponseEventLogService.threeTierLoadGroupRestore(yukonUser, loadGroupName);
                demandResponseEventLogService.loadGroupRestore(loadGroupName);
                demandResponseEventLogService.threeTierLoadGroupEnabled(yukonUser, loadGroupName);
                demandResponseEventLogService.loadGroupEnabled(loadGroupName);
                demandResponseEventLogService.threeTierLoadGroupDisabled(yukonUser, loadGroupName);
                demandResponseEventLogService.loadGroupDisabled(loadGroupName);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 36;}
        }),
        
        
        //22
        HARDWARE_EVENT_LOG("HardwareEventLogService", HardwareEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());

                String meterName = devEventLog.getIndicatorString() + "MeterName";
                String oldMeterName = devEventLog.getIndicatorString() + "oldMeterName";
                String newMeterName = devEventLog.getIndicatorString() + "newMeterName";
                String deviceName = devEventLog.getIndicatorString() + "DeviceName";
                String deviceLabel = devEventLog.getIndicatorString() + "DeviceLabel";

                String serialNumber = "Serial_#_67";
                String oldSerialNumber = "old_SN_8675309";
                String newSerialNumber = "new_SN_8675309";
                String accountNumber = "Account_12";

                hardwareEventLogService.hardwareMeterCreationAttempted(yukonUser, meterName, devEventLog.getEventSource());
                hardwareEventLogService.hardwareChangeOutForMeterAttempted(yukonUser, oldMeterName, newMeterName, devEventLog.getEventSource());
                hardwareEventLogService.hardwareChangeOutAttempted(yukonUser, oldSerialNumber, newSerialNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareCreationAttempted(yukonUser, accountNumber, serialNumber, devEventLog.getEventSource());
                hardwareEventLogService.twoWayHardwareCreationAttempted(yukonUser, deviceName, devEventLog.getEventSource());
                hardwareEventLogService.assigningHardwareAttempted(yukonUser, accountNumber, serialNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareAdditionAttempted(yukonUser, accountNumber, serialNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareUpdateAttempted(yukonUser, accountNumber, serialNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareDeletionAttempted(yukonUser, deviceLabel, devEventLog.getEventSource());
                hardwareEventLogService.hardwareRemovalAttempted(yukonUser, accountNumber, serialNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareCreated(yukonUser, deviceLabel);
                hardwareEventLogService.hardwareAdded(yukonUser, deviceLabel, accountNumber);
                hardwareEventLogService.hardwareUpdated(yukonUser, deviceLabel);
                hardwareEventLogService.hardwareRemoved(yukonUser, deviceLabel, accountNumber);
                hardwareEventLogService.hardwareDeleted(yukonUser, deviceLabel);
                hardwareEventLogService.serialNumberChanged(yukonUser, oldSerialNumber, newSerialNumber);
                hardwareEventLogService.hardwareConfigAttempted(yukonUser, serialNumber, accountNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareDisableAttempted(yukonUser, serialNumber, accountNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareEnableAttempted(yukonUser, serialNumber, accountNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareConfigUpdated(yukonUser, serialNumber, accountNumber);
                hardwareEventLogService.hardwareDisabled(yukonUser, serialNumber, accountNumber);
                hardwareEventLogService.hardwareEnabled(yukonUser, serialNumber, accountNumber);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 22;}
        }),
        INVENTORY_CONFIG_EVENT_LOG("InventoryConfigEventLogService", InventoryConfigEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());

                String taskName = devEventLog.getIndicatorString() + "TaskName";
                String serialNumber = "1234432";
                int inventoryId = 123321;
                int commandRequestExecutionIdentifier = 123;

                inventoryConfigEventLogService.taskCreated(yukonUser, taskName);
                inventoryConfigEventLogService.taskDeleted(yukonUser, taskName);
                inventoryConfigEventLogService.itemConfigSucceeded(yukonUser, serialNumber, inventoryId, commandRequestExecutionIdentifier);
                inventoryConfigEventLogService.itemConfigFailed(yukonUser, serialNumber, inventoryId, commandRequestExecutionIdentifier);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 4;}
        }),
        METERING_EVENT_LOG("MeteringEventLogService", MeteringEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser user = new LiteYukonUser(0, devEventLog.getUsername());
                String scheduleName = "fakeScheduleName";
                int paoId = 123;

                meteringEventLogService.readNowPushedForReadingsWidget(user,paoId);
                meteringEventLogService.scheduleDeleted(user,scheduleName);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 2;}
        }),
        OUTAGE_EVENT_LOG("OutageEventLogService", OutageEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());

                String messageSource = devEventLog.getIndicatorString() + "MessageSource";
                String eventType = devEventLog.getIndicatorString() + "EventType";
                String objectId = "4565";
                String deviceType = devEventLog.getIndicatorString() + "DeviceType";
                String mspVendor = devEventLog.getIndicatorString() + "MspVendor";
                String statusPointMonitorName = devEventLog.getIndicatorString() + "PointMonitorName";
                String groupName = devEventLog.getIndicatorString() + "GroupName";
                String attribute = devEventLog.getIndicatorString() + "Attribute";
                String stateGroup = devEventLog.getIndicatorString() + "StateGroup";
                String evaluatorStatus = devEventLog.getIndicatorString() + "EvaluatorStatus";
                String monitorName = devEventLog.getIndicatorString() + "MonitorName";

                Date eventDateTime = new Date();

                int statusPointMonitorId = 10;
                int monitorId = 16;

                outageEventLogService.mspMessageSentToVendor(messageSource,eventType,objectId,deviceType,mspVendor);
                outageEventLogService.outageEventGenerated(eventType,eventDateTime,deviceType,objectId);
                outageEventLogService.statusPointMonitorCreated(statusPointMonitorId,statusPointMonitorName,groupName,attribute,stateGroup,evaluatorStatus,yukonUser);
                outageEventLogService.statusPointMonitorDeleted(statusPointMonitorId,statusPointMonitorName,groupName,attribute,stateGroup,evaluatorStatus,yukonUser);
                outageEventLogService.statusPointMonitorUpdated(statusPointMonitorId,statusPointMonitorName,groupName,attribute,stateGroup,evaluatorStatus,yukonUser);
                outageEventLogService.statusPointMonitorEnableDisable(statusPointMonitorId,evaluatorStatus,yukonUser);
                outageEventLogService.porterResponseMonitorCreated(monitorId,monitorName,attribute,stateGroup,evaluatorStatus,yukonUser);
                outageEventLogService.porterResponseMonitorDeleted(monitorId,monitorName,attribute,stateGroup,evaluatorStatus,yukonUser);
                outageEventLogService.porterResponseMonitorUpdated(monitorId,monitorName,attribute,stateGroup,evaluatorStatus,yukonUser);
                outageEventLogService.porterResponseMonitorEnableDisable(monitorId,evaluatorStatus,yukonUser);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 10;}
        }),
        RFN_DEVICE_EVENT_LOG("RfnDeviceEventLogService", RfnDeviceEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                long paoId = 67;

                String rfnIdentifier = devEventLog.getIndicatorString() + "RfnIdentifier";
                String templateName = devEventLog.getIndicatorString() + "TemplateName";
                String deviceName = devEventLog.getIndicatorString() + "DeviceName";
                String sensorManufacturer = devEventLog.getIndicatorString() + "SensorManufacturer";
                String sensorModel = devEventLog.getIndicatorString() + "SensorModel";
                String sensorSerialNumber = "45666545";

                rfnDeviceEventLogService.createdNewDeviceAutomatically(paoId, rfnIdentifier, templateName, deviceName);
                rfnDeviceEventLogService.receivedDataForUnkownDeviceTemplate(templateName);
                rfnDeviceEventLogService.unableToCreateDeviceFromTemplate(templateName, sensorManufacturer, sensorModel, sensorSerialNumber);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 3;}
        }),

        STARS_EVENT_LOG("StarsEventLogService", StarsEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser user = new LiteYukonUser(0, devEventLog.getUsername());

                String yukonEnergyCompany = devEventLog.getIndicatorString() + "YukonEnergyCompany";
                String energyCompanyName = devEventLog.getIndicatorString() + "EnergyCompanyName";
                String warehouseName = devEventLog.getIndicatorString() + "WarehouseName";
                String programName = devEventLog.getIndicatorString() + "Program";

                int oldRouteId = 10;
                int newRouteId = 15;
                boolean optOutsCount = true;

                starsEventLogService.deleteEnergyCompanyAttempted(user,yukonEnergyCompany,devEventLog.getEventSource());
                starsEventLogService.deleteEnergyCompany(user,yukonEnergyCompany);
                starsEventLogService.energyCompanyDefaultRouteChanged(user,energyCompanyName,oldRouteId,newRouteId);
                starsEventLogService.addWarehouseAttempted(user,warehouseName,devEventLog.getEventSource());
                starsEventLogService.updateWarehouseAttempted(user,warehouseName,devEventLog.getEventSource());
                starsEventLogService.deleteWarehouseAttempted(user,warehouseName,devEventLog.getEventSource());
                starsEventLogService.addWarehouse(warehouseName);
                starsEventLogService.updateWarehouse(warehouseName);
                starsEventLogService.deleteWarehouse(warehouseName);
                starsEventLogService.cancelCurrentOptOutsAttempted(user,devEventLog.getEventSource());
                starsEventLogService.cancelCurrentOptOutsByProgramAttempted(user,programName,devEventLog.getEventSource());
                starsEventLogService.cancelCurrentOptOuts(user);
                starsEventLogService.cancelCurrentOptOutsByProgram(user,programName);
                starsEventLogService.enablingOptOutUsageForTodayAttempted(user,devEventLog.getEventSource());
                starsEventLogService.enablingOptOutUsageForTodayByProgramAttempted(user,programName,devEventLog.getEventSource());
                starsEventLogService.disablingOptOutUsageForTodayAttempted(user,devEventLog.getEventSource());
                starsEventLogService.disablingOptOutUsageForTodayByProgramAttempted(user,programName,devEventLog.getEventSource());
                starsEventLogService.optOutUsageEnabledToday(user,true,true);
                starsEventLogService.optOutUsageEnabledTodayForProgram(user,programName,true,true);
                starsEventLogService.countTowardOptOutLimitTodayAttempted(user,devEventLog.getEventSource());
                starsEventLogService.countTowardOptOutLimitTodayByProgramAttempted(user,programName,devEventLog.getEventSource());
                starsEventLogService.doNotCountTowardOptOutLimitTodayAttempted(user,devEventLog.getEventSource());
                starsEventLogService.doNotCountTowardOptOutLimitTodayByProgramAttempted(user,programName,devEventLog.getEventSource());
                starsEventLogService.countTowardOptOutLimitToday(user,optOutsCount);
                starsEventLogService.countTowardOptOutLimitTodayForProgram(user,programName,optOutsCount);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 25;}
        }),
        SYSTEM_EVENT_LOG("SystemEventLogService", SystemEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser user = new LiteYukonUser(0, devEventLog.getUsername());

                String newUsername = devEventLog.getIndicatorString() + "NewUsername";
                String oldUsername = devEventLog.getIndicatorString() + "OldUsername";
                String username = devEventLog.getIndicatorString() + "Username";
                String remoteAddress = devEventLog.getIndicatorString() + "RemoteAddr";

                Instant start = Instant.now().minus(Duration.standardDays(5));
                Instant finish = Instant.now();

                int rowsDeleted = 10;

                systemEventLogService.loginPasswordChangeAttempted(user, devEventLog.getEventSource());
                systemEventLogService.loginUsernameChangeAttempted(user, newUsername, devEventLog.getEventSource());
                systemEventLogService.loginChangeAttempted(user, username, devEventLog.getEventSource());
                systemEventLogService.usernameChanged(user, oldUsername, newUsername);
                systemEventLogService.loginWeb(user, remoteAddress);
                systemEventLogService.loginClient(user, remoteAddress);
                systemEventLogService.loginInboundVoice(user, remoteAddress);
                systemEventLogService.loginOutboundVoice(user, remoteAddress);
                systemEventLogService.rphDeleteDuplicates(rowsDeleted, start, finish);
                systemEventLogService.rphDeleteDanglingEntries(rowsDeleted, start, finish);
                systemEventLogService.systemLogDeleteDanglingEntries(rowsDeleted, start, finish);
                systemEventLogService.globalSettingChanged(user, GlobalSettingType.ACCT_PORT, "abc");
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 12;}
        }),
        VALIDATION_EVENT_LOG("ValidationEventLogService", ValidationEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser user = new LiteYukonUser(0, devEventLog.getUsername());

                int paoId = 10;
                int pointId = 12;
                int pointOffset = 14;
                int tagsCleared = 16;
                int changeId = 18;

                long lastChangeIdProcessed = Instant.now().getMillis();

                DateTime validationResetDate = new DateTime();

                PointType pointType = PointType.Analog;
                PaoType paoType = PaoType.ALPHA_A1;

                String paoName = devEventLog.getIndicatorString() + "PaoName";
                String tagSet = devEventLog.getIndicatorString() + "TagSet";

                validationEventLogService.unreasonableValueCausedReRead(paoId, paoName, paoType, pointId, pointType, pointOffset);
                validationEventLogService.validationEngineStartup(lastChangeIdProcessed, tagsCleared);
                validationEventLogService.changedQualityOnPeakedValue(changeId, paoId, paoName, paoType, pointId, pointType, pointOffset);
                validationEventLogService.validationEngineReset(user);
                validationEventLogService.validationEnginePartialReset(validationResetDate, user);
                validationEventLogService.deletedAllTaggedRows(tagSet, user);
                validationEventLogService.acceptedAllTaggedRows(tagSet, user);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 7;}
        }),
        VEE_REVIEW_EVENT_LOG("VeeReviewEventLogService", VeeReviewEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser user = new LiteYukonUser(0, devEventLog.getUsername());

                int changeId = 18;
                int pointId = 12;
                int pointOffset = 14;

                double value = 55.5;

                Date timestamp = new Date();

                PointType pointType = PointType.Analog;
                PaoType paoType = PaoType.ALPHA_A1;

                String paoName = devEventLog.getIndicatorString() + "PaoName";

                veeReviewEventLogService.deletePointValue(changeId, value, timestamp, paoName, paoType, pointId, pointType, pointOffset, user);
                veeReviewEventLogService.acceptPointValue(changeId, value, timestamp, paoName, paoType, pointId, pointType, pointOffset, user);
                veeReviewEventLogService.updateQuestionableQuality(changeId, value, timestamp, paoName, paoType, pointId, pointType, pointOffset, user);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 3;}
        }),
        ZIGBEE_EVENT_LOG("ZigbeeEventLogService", ZigbeeEventLogService.class.getMethods().length, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());

                String paoName = devEventLog.getIndicatorString() + "PaoName";
                String message = devEventLog.getIndicatorString() + "Message";
                String gatewayName = devEventLog.getIndicatorString() + "GatewayName";

                zigbeeEventLogService.zigbeeDeviceCommission(yukonUser,paoName,devEventLog.getEventSource());
                zigbeeEventLogService.zigbeeDeviceCommissioned(paoName);
                zigbeeEventLogService.zigbeeDeviceDecommission(yukonUser,paoName,devEventLog.getEventSource());
                zigbeeEventLogService.zigbeeDeviceDecommissioned(paoName);
                zigbeeEventLogService.zigbeeDeviceRefresh(yukonUser,paoName,devEventLog.getEventSource());
                zigbeeEventLogService.zigbeeDeviceRefreshed(paoName);
                zigbeeEventLogService.zigbeeSendText(yukonUser,paoName,message,devEventLog.getEventSource());
                zigbeeEventLogService.zigbeeSentText(paoName,message);
                zigbeeEventLogService.zigbeeDeviceAssign(yukonUser,paoName,gatewayName,devEventLog.getEventSource());
                zigbeeEventLogService.zigbeeDeviceAssigned(paoName,gatewayName);
                zigbeeEventLogService.zigbeeDeviceUnassign(yukonUser,paoName,gatewayName,devEventLog.getEventSource());
                zigbeeEventLogService.zigbeeDeviceUnassigned(paoName,gatewayName);
            }
            // This should represent the number of method calls to this logging service
            @Override public int numMethods() {return 12;}
        }),
        ;

        private final String logServiceName;
        private final int numberOfMethods;
        private DevEventLogExecutable devEventLogExecutable = null;
        
        LogType(String logServiceName, int numberOfMethods, DevEventLogExecutable devEventLogExecutable) {
            this.logServiceName = logServiceName;
            this.numberOfMethods = numberOfMethods;
            this.devEventLogExecutable = devEventLogExecutable;
            
        }

        public int getNumberOfMethods() {
            return numberOfMethods;
        }
        
        public void execute(DevEventLog devEventLog) {
            devEventLogExecutable.execute(devEventLog);
        }

        @Override
        public MessageSourceResolvable getMessage() {
            String message = logServiceName + "  (" + numberOfMethods + " log methods)";
            if (numberOfMethods > devEventLogExecutable.numMethods()) {
                message += "  <span class='error'>Note: " + (numberOfMethods - devEventLogExecutable.numMethods()) + " methods from this service are not included in this tool.</span>";
            }
            return YukonMessageSourceResolvable.createDefaultWithoutCode(message);
        }
    }

    private static int complete ;
    private static int total;
    private static final ReentrantLock _lock = new ReentrantLock();

    public boolean isRunning() {
        return _lock.isLocked();
    }
    
    public void execute(DevEventLog devEventLog) {
        
        if (_lock.tryLock()) try {
            int iterations = devEventLog.getIterations();
            
            complete = 0;
            total = devEventLog.getTotal();

            Map<LogType, Boolean> eventLogTypes = devEventLog.getEventLogTypes();
            for (LogType logType :LogType.values()) {
                if (eventLogTypes.get(logType)) {
                    for (int i=0;i<iterations;i++) {
                        logType.execute(devEventLog);
                        complete += logType.getNumberOfMethods();
                        log.info(": Inserting EventLog entries for " + logType);
                    }
                }
            }

        } finally {
            _lock.unlock();
        }
    }

    public int getPercentComplete() {
        if (total >= 1) {
            return (complete*100) / total;
        } else { 
            return 0;
        }
    }
    
    @Autowired 
    private void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        DevEventLogCreationService.accountEventLogService = accountEventLogService;
    }
    
    @Autowired
    public void setCommandRequestExecutorEventLogService(CommandRequestExecutorEventLogService commandRequestExecutorEventLogService) {
        DevEventLogCreationService.commandRequestExecutorEventLogService =commandRequestExecutorEventLogService;
    }

    @Autowired
    public void setCommandScheduleEventLogService(CommandScheduleEventLogService commandScheduleEventLogService) {
        DevEventLogCreationService.commandScheduleEventLogService = commandScheduleEventLogService;
    }
    
    @Autowired
    public void setDatabaseMigrationEventLogService(DatabaseMigrationEventLogService databaseMigrationEventLogService) {
        DevEventLogCreationService.databaseMigrationEventLogService = databaseMigrationEventLogService;
    }

    @Autowired
    public void setDemandResponseEventLogService(DemandResponseEventLogService demandResponseEventLogService) {
        DevEventLogCreationService.demandResponseEventLogService = demandResponseEventLogService;
    }

    @Autowired
    public void setHardwareEventLogService(HardwareEventLogService hardwareEventLogService) {
        DevEventLogCreationService.hardwareEventLogService = hardwareEventLogService;
    }

    @Autowired
    public void setInventoryConfigEventLogService(InventoryConfigEventLogService inventoryConfigEventLogService) {
        DevEventLogCreationService.inventoryConfigEventLogService = inventoryConfigEventLogService;
    }

    @Autowired
    public void setMeteringEventLogService(MeteringEventLogService meteringEventLogService) {
        DevEventLogCreationService.meteringEventLogService = meteringEventLogService;
    }

    @Autowired
    public void setOutageEventLogService(OutageEventLogService outageEventLogService) {
        DevEventLogCreationService.outageEventLogService = outageEventLogService;
    }

    @Autowired
    public void setRfnDeviceEventLogService(RfnDeviceEventLogService rfnDeviceEventLogService) {
        DevEventLogCreationService.rfnDeviceEventLogService = rfnDeviceEventLogService;
    }

    @Autowired
    public void setStarsEventLogService(StarsEventLogService starsEventLogService) {
        DevEventLogCreationService.starsEventLogService = starsEventLogService;
    }

    @Autowired
    public void setSystemEventLogService(SystemEventLogService systemEventLogService) {
        DevEventLogCreationService.systemEventLogService = systemEventLogService;
    }

    @Autowired
    public void setValidationEventLogService(ValidationEventLogService validationEventLogService) {
        DevEventLogCreationService.validationEventLogService = validationEventLogService;
    }

    @Autowired
    public void setVeeReviewEventLogService(VeeReviewEventLogService veeReviewEventLogService) {
        DevEventLogCreationService.veeReviewEventLogService = veeReviewEventLogService;
    }

    @Autowired
    public void setZigbeeEventLogService(ZigbeeEventLogService zigbeeEventLogService) {
        DevEventLogCreationService.zigbeeEventLogService = zigbeeEventLogService;
    }
}

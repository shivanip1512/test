package com.cannontech.web.dev.database.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

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
import com.cannontech.common.events.loggers.EcobeeEventLogService;
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
import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.dev.database.objects.DevEventLog;
import com.google.common.collect.ImmutableMap;

public class DevEventLogCreationService {

    protected static final Logger log = YukonLogManager.getLogger(DevEventLogCreationService.class);

    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private CommandRequestExecutorEventLogService commandRequestExecutorEventLogService;
    @Autowired private CommandScheduleEventLogService commandScheduleEventLogService;
    @Autowired private DatabaseMigrationEventLogService databaseMigrationEventLogService;
    @Autowired private DemandResponseEventLogService demandResponseEventLogService;
    @Autowired private EcobeeEventLogService ecobeeEventLogService;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private InventoryConfigEventLogService inventoryConfigEventLogService;
    @Autowired private MeteringEventLogService meteringEventLogService;
    @Autowired private OutageEventLogService outageEventLogService;
    @Autowired private RfnDeviceEventLogService rfnDeviceEventLogService;
    @Autowired private StarsEventLogService starsEventLogService;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private ValidationEventLogService validationEventLogService;
    @Autowired private VeeReviewEventLogService veeReviewEventLogService;
    @Autowired private ZigbeeEventLogService zigbeeEventLogService;

    private Map<LogType, DevEventLogExecutable> eventLogExecutables;

    @PostConstruct
    public void setup() {
        Map<LogType, DevEventLogExecutable> executables = new HashMap<>();

        executables.put(LogType.ACCOUNT, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());
                String accountNumber = devEventLog.getIndicatorString() + "Account_42";
                String oldAccountNumber = devEventLog.getIndicatorString() + "Old_Account_152";
                String newAccountNumber = devEventLog.getIndicatorString() + "New_Account_124";
                String serialNumber = devEventLog.getIndicatorString() + "Serial_#_82";
                String applianceType = devEventLog.getIndicatorString() + "Toaster Appliance";
                String deviceName = devEventLog.getIndicatorString() + "aDevice";
                String programName =
                    devEventLog.getIndicatorString() + "SudevEventLog.getIndicatorString() + mmerProgram";
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
                accountEventLogService.accountUpdateCreationAttempted(yukonUser, accountNumber,
                    devEventLog.getEventSource());
                accountEventLogService.accountDeletionAttempted(yukonUser, accountNumber, devEventLog.getEventSource());
                accountEventLogService.accountUpdateAttempted(yukonUser, accountNumber, devEventLog.getEventSource());

                // AccountServiceLevel
                accountEventLogService.accountAdded(yukonUser, accountNumber);
                accountEventLogService.accountUpdated(yukonUser, accountNumber);
                accountEventLogService.accountDeleted(yukonUser, accountNumber);
                accountEventLogService.accountNumberChanged(yukonUser, oldAccountNumber, newAccountNumber);

                // *CONTACTINFO*
                accountEventLogService.contactAdded(yukonUser, accountNumber, contactName);
                accountEventLogService.contactUpdated(yukonUser, accountNumber, contactName);
                accountEventLogService.contactRemoved(yukonUser, accountNumber, contactName);
                accountEventLogService.contactNameChanged(yukonUser, accountNumber, oldContactName, newContactName);
                accountEventLogService.customerTypeChanged(yukonUser, accountNumber, oldCustomerType, newcustomerType);
                accountEventLogService.companyNameChanged(yukonUser, accountNumber, oldCompanyName, newCompanyName);

                // Enrollment
                accountEventLogService.enrollmentAttempted(yukonUser, accountNumber, deviceName, programName,
                    loadGroupName, devEventLog.getEventSource());
                accountEventLogService.enrollmentModificationAttempted(yukonUser, accountNumber,
                    devEventLog.getEventSource());
                accountEventLogService.unenrollmentAttempted(yukonUser, accountNumber, deviceName, programName,
                    loadGroupName, devEventLog.getEventSource());

                // EnrollmentServiceLevel
                accountEventLogService.deviceEnrolled(yukonUser, accountNumber, deviceName, programName, loadGroupName);
                accountEventLogService.deviceUnenrolled(yukonUser, accountNumber, deviceName, programName,
                    loadGroupName);

                // OptOuts
                accountEventLogService.optOutLimitReductionAttempted(yukonUser, accountNumber, serialNumber,
                    devEventLog.getEventSource());
                accountEventLogService.optOutLimitIncreaseAttempted(yukonUser, accountNumber, serialNumber,
                    devEventLog.getEventSource());
                accountEventLogService.optOutLimitResetAttempted(yukonUser, accountNumber, serialNumber,
                    devEventLog.getEventSource());
                accountEventLogService.optOutResendAttempted(yukonUser, accountNumber, serialNumber,
                    devEventLog.getEventSource());

                accountEventLogService.optOutAttempted(yukonUser, accountNumber, serialNumber, startDate,
                    devEventLog.getEventSource());
                accountEventLogService.optOutCancelAttempted(yukonUser, accountNumber, serialNumber, optOutStartDate,
                    optOutStopDate, devEventLog.getEventSource());
                accountEventLogService.scheduledOptOutCancelAttempted(yukonUser, accountNumber, serialNumber,
                    devEventLog.getEventSource());
                accountEventLogService.activeOptOutCancelAttempted(yukonUser, accountNumber, serialNumber,
                    devEventLog.getEventSource());

                // OptOutServiceLevel
                accountEventLogService.optOutLimitIncreased(yukonUser, accountNumber, deviceName, optOutsAdded);
                accountEventLogService.optOutLimitReset(yukonUser, accountNumber, deviceName);
                accountEventLogService.optOutResent(yukonUser, accountNumber, deviceName);

                accountEventLogService.deviceOptedOut(yukonUser, accountNumber, deviceName, startDate, stopDate);
                accountEventLogService.optOutCanceled(yukonUser, accountNumber, deviceName);

                // Appliance
                accountEventLogService.applianceAdditionAttempted(yukonUser, accountNumber, applianceType, deviceName,
                    programName, devEventLog.getEventSource());
                accountEventLogService.applianceUpdateAttempted(yukonUser, accountNumber, applianceType, deviceName,
                    programName, devEventLog.getEventSource());
                accountEventLogService.applianceDeletionAttempted(yukonUser, accountNumber, applianceType, deviceName,
                    programName, devEventLog.getEventSource());

                // ApplianceServiceLevel
                accountEventLogService.applianceAdded(yukonUser, accountNumber, applianceType, deviceName, programName);
                accountEventLogService.applianceUpdated(yukonUser, accountNumber, applianceType, deviceName,
                    programName);
                accountEventLogService.applianceDeleted(yukonUser, accountNumber, applianceType, deviceName,
                    programName);

                // WorkOrder
                accountEventLogService.workOrderCreationAttempted(yukonUser, accountNumber, workOrderNumber,
                    devEventLog.getEventSource());
                accountEventLogService.workOrderUpdateAttempted(yukonUser, accountNumber, workOrderNumber,
                    devEventLog.getEventSource());
                accountEventLogService.workOrderDeletionAttempted(yukonUser, accountNumber, workOrderNumber,
                    devEventLog.getEventSource());

                // WorkOrderServiceLevel
                accountEventLogService.workOrderCreated(yukonUser, accountNumber, workOrderNumber);
                accountEventLogService.workOrderUpdated(yukonUser, accountNumber, workOrderNumber);
                accountEventLogService.workOrderDeleted(yukonUser, accountNumber, workOrderNumber);

                // ThermostatSchedule
                accountEventLogService.thermostatScheduleSavingAttempted(yukonUser, accountNumber, serialNumber,
                    scheduleName, devEventLog.getEventSource());
                accountEventLogService.thermostatScheduleSendDefaultAttempted(yukonUser, serialNumber,
                    devEventLog.getEventSource());
                accountEventLogService.thermostatScheduleSendAttempted(yukonUser, serialNumber, scheduleName,
                    devEventLog.getEventSource());
                accountEventLogService.thermostatScheduleDeleteAttempted(yukonUser, accountNumber, scheduleName,
                    devEventLog.getEventSource());
                accountEventLogService.thermostatRunProgramAttempted(yukonUser, serialNumber,
                    devEventLog.getEventSource());
                accountEventLogService.thermostatRunProgramAttempted(yukonUser, accountNumber, serialNumber,
                    devEventLog.getEventSource());
                accountEventLogService.thermostatManualSetAttempted(yukonUser, accountNumber, serialNumber,
                    heatTemperature, coolTemperature, mode, holdTemperature, devEventLog.getEventSource());
                accountEventLogService.thermostatLabelChangeAttempted(yukonUser, serialNumber, oldThermostatLabel,
                    newThermostatLabel, devEventLog.getEventSource());

                // ThermostatScheduleServiceLevel
                accountEventLogService.thermostatScheduleSaved(accountNumber, scheduleName);
                accountEventLogService.thermostatScheduleDeleted(accountNumber, scheduleName);
                accountEventLogService.thermostatManuallySet(yukonUser, serialNumber);
                accountEventLogService.thermostatLabelChanged(yukonUser, accountNumber, serialNumber,
                    oldThermostatLabel, newThermostatLabel);

                accountEventLogService.thermostatScheduleNameChanged(yukonUser, oldScheduleName, newScheduleName);
            }
        });
        executables.put(LogType.COMMAND_REQUEST_EXECUTOR, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                int creId = 10;
                int contextId = 10;
                String currentCommandString =
                    devEventLog.getIndicatorString() + "CurrentCommand";
                String error = devEventLog.getIndicatorString() + "error";
                LiteYukonUser yukonUser =
                    new LiteYukonUser(0, devEventLog.getUsername());

                commandRequestExecutorEventLogService.commandFailedToTransmit(creId,
                    contextId, DeviceRequestType.GROUP_COMMAND,
                    currentCommandString, error, yukonUser);
                commandRequestExecutorEventLogService.foundFailedCre(creId);
            }
        });
        executables.put(LogType.COMMAND_SCHEDULE, new DevEventLogExecutable() {
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
        });
        executables.put(LogType.DATABASE_MIGRATION, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());
                String fileName = "fakeFile.csv";

                databaseMigrationEventLogService.startingExport(yukonUser, fileName);
                databaseMigrationEventLogService.startingImport(yukonUser, fileName);
                databaseMigrationEventLogService.startingValidation(yukonUser, fileName);
            }
        });
        executables.put(LogType.DEMAND_RESPONSE, new DevEventLogExecutable() {
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
                String loadGroupName = devEventLog.getIndicatorString() + "LoadGroupName";

                boolean overrideConstraints = true;
                boolean stopScheduled = true;

                demandResponseEventLogService.threeTierScenarioStarted(yukonUser,
                    scenarioName);
                demandResponseEventLogService.threeTierScenarioStopped(yukonUser,
                    scenarioName);
                // ControlArealogging
                demandResponseEventLogService.controlAreaStarted(controlAreaName, startDate);
                demandResponseEventLogService.controlAreaStopped(controlAreaName, stopDate);
                demandResponseEventLogService.threeTierControlAreaTriggersChanged(yukonUser,
                    controlAreaName, threshold1, offset1, threshold2, offset2);
                demandResponseEventLogService.controlAreaTriggersChanged(controlAreaName,
                    threshold1, offset1, threshold2, offset2);
                demandResponseEventLogService.threeTierControlAreaTimeWindowChanged(
                    yukonUser, controlAreaName, startSeconds, stopSeconds);
                demandResponseEventLogService.controlAreaTimeWindowChanged(controlAreaName,
                    startSeconds, stopSeconds);
                demandResponseEventLogService.threeTierControlAreaStarted(yukonUser,
                    controlAreaName);
                demandResponseEventLogService.threeTierControlAreaStopped(yukonUser,
                    controlAreaName);
                demandResponseEventLogService.threeTierControlAreaEnabled(yukonUser,
                    controlAreaName);
                demandResponseEventLogService.controlAreaEnabled(controlAreaName);
                demandResponseEventLogService.threeTierControlAreaDisabled(yukonUser,
                    controlAreaName);
                demandResponseEventLogService.controlAreaDisabled(controlAreaName);
                demandResponseEventLogService.threeTierControlAreaPeakReset(yukonUser,
                    controlAreaName);
                demandResponseEventLogService.controlAreaPeakReset(controlAreaName);
                // Programlogging
                demandResponseEventLogService.threeTierProgramScheduled(yukonUser,
                    programName, startDate);
                demandResponseEventLogService.programScheduled(programName,
                    overrideConstraints, gearName, startDate, stopScheduled, stopDate);
                demandResponseEventLogService.threeTierProgramStopped(yukonUser, programName,
                    stopDate);
                demandResponseEventLogService.programStopped(programName, stopDate, gearName);
                demandResponseEventLogService.threeTierProgramStopScheduled(yukonUser,
                    programName, stopDate);
                demandResponseEventLogService.programStopScheduled(programName, stopDate,
                    gearName);
                demandResponseEventLogService.threeTierProgramChangeGear(yukonUser,
                    programName);
                demandResponseEventLogService.programChangeGear(programName, gearName);
                demandResponseEventLogService.threeTierProgramEnabled(yukonUser, programName);
                demandResponseEventLogService.programEnabled(programName);
                demandResponseEventLogService.threeTierProgramDisabled(yukonUser, programName);
                demandResponseEventLogService.programDisabled(programName);
                // LoadGrouplogging
                demandResponseEventLogService.threeTierLoadGroupShed(yukonUser,
                    loadGroupName, shedSeconds);
                demandResponseEventLogService.loadGroupShed(loadGroupName, shedSeconds);
                demandResponseEventLogService.threeTierLoadGroupRestore(yukonUser,
                    loadGroupName);
                demandResponseEventLogService.loadGroupRestore(loadGroupName);
                demandResponseEventLogService.threeTierLoadGroupEnabled(yukonUser,
                    loadGroupName);
                demandResponseEventLogService.loadGroupEnabled(loadGroupName);
                demandResponseEventLogService.threeTierLoadGroupDisabled(yukonUser,
                    loadGroupName);
                demandResponseEventLogService.loadGroupDisabled(loadGroupName);
            }
        });
        executables.put(LogType.HARDWARE, new DevEventLogExecutable() {
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

                hardwareEventLogService.hardwareMeterCreationAttempted(yukonUser, meterName,
                    devEventLog.getEventSource());
                hardwareEventLogService.hardwareChangeOutForMeterAttempted(yukonUser, oldMeterName,
                    newMeterName, devEventLog.getEventSource());
                hardwareEventLogService.hardwareChangeOutAttempted(yukonUser, oldSerialNumber,
                    newSerialNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareCreationAttempted(yukonUser, accountNumber,
                    serialNumber, devEventLog.getEventSource());
                hardwareEventLogService.twoWayHardwareCreationAttempted(yukonUser, deviceName,
                    devEventLog.getEventSource());
                hardwareEventLogService.assigningHardwareAttempted(yukonUser, accountNumber,
                    serialNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareAdditionAttempted(yukonUser, accountNumber,
                    serialNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareUpdateAttempted(yukonUser, accountNumber,
                    serialNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareDeletionAttempted(yukonUser, deviceLabel,
                    devEventLog.getEventSource());
                hardwareEventLogService.hardwareRemovalAttempted(yukonUser, accountNumber,
                    serialNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareCreated(yukonUser, deviceLabel);
                hardwareEventLogService.hardwareAdded(yukonUser, deviceLabel, accountNumber);
                hardwareEventLogService.hardwareUpdated(yukonUser, deviceLabel);
                hardwareEventLogService.hardwareRemoved(yukonUser, deviceLabel, accountNumber);
                hardwareEventLogService.hardwareDeleted(yukonUser, deviceLabel);
                hardwareEventLogService.serialNumberChanged(yukonUser, oldSerialNumber,
                    newSerialNumber);
                hardwareEventLogService.hardwareConfigAttempted(yukonUser, serialNumber,
                    accountNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareDisableAttempted(yukonUser, serialNumber,
                    accountNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareEnableAttempted(yukonUser, serialNumber,
                    accountNumber, devEventLog.getEventSource());
                hardwareEventLogService.hardwareConfigUpdated(yukonUser, serialNumber, accountNumber);
                hardwareEventLogService.hardwareDisabled(yukonUser, serialNumber, accountNumber);
                hardwareEventLogService.hardwareEnabled(yukonUser, serialNumber, accountNumber);
            }
        });
        executables.put(LogType.INVENTORY_CONFIG, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());

                String taskName = devEventLog.getIndicatorString() + "TaskName";
                String serialNumber = "1234432";
                int inventoryId = 123321;
                int commandRequestExecutionIdentifier = 123;

                inventoryConfigEventLogService.taskCreated(yukonUser, taskName);
                inventoryConfigEventLogService.taskDeleted(yukonUser, taskName);
                inventoryConfigEventLogService.itemConfigSucceeded(yukonUser, serialNumber,
                    inventoryId, commandRequestExecutionIdentifier);
                inventoryConfigEventLogService.itemConfigFailed(yukonUser, serialNumber,
                    inventoryId, commandRequestExecutionIdentifier);
            }
        });
        executables.put(LogType.METERING, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser user = new LiteYukonUser(0, devEventLog.getUsername());
                String scheduleName = "fakeScheduleName";
                int paoId = 123;

                meteringEventLogService.readNowPushedForReadingsWidget(user, paoId);
                meteringEventLogService.scheduleDeleted(user, scheduleName);
            }
        });
        executables.put(LogType.OUTAGE,  new DevEventLogExecutable() {
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

                outageEventLogService.mspMessageSentToVendor(messageSource, eventType, objectId,
                    deviceType, mspVendor);
                outageEventLogService.outageEventGenerated(eventType, eventDateTime, deviceType,
                    objectId);
                outageEventLogService.statusPointMonitorCreated(statusPointMonitorId,
                    statusPointMonitorName, groupName, attribute, stateGroup, evaluatorStatus,
                    yukonUser);
                outageEventLogService.statusPointMonitorDeleted(statusPointMonitorId,
                    statusPointMonitorName, groupName, attribute, stateGroup, evaluatorStatus,
                    yukonUser);
                outageEventLogService.statusPointMonitorUpdated(statusPointMonitorId,
                    statusPointMonitorName, groupName, attribute, stateGroup, evaluatorStatus,
                    yukonUser);
                outageEventLogService.statusPointMonitorEnableDisable(statusPointMonitorId,
                    evaluatorStatus, yukonUser);
                outageEventLogService.porterResponseMonitorCreated(monitorId, monitorName, attribute,
                    stateGroup, evaluatorStatus, yukonUser);
                outageEventLogService.porterResponseMonitorDeleted(monitorId, monitorName, attribute,
                    stateGroup, evaluatorStatus, yukonUser);
                outageEventLogService.porterResponseMonitorUpdated(monitorId, monitorName, attribute,
                    stateGroup, evaluatorStatus, yukonUser);
                outageEventLogService.porterResponseMonitorEnableDisable(monitorId, evaluatorStatus,
                    yukonUser);
            }
        });
        executables.put(LogType.RFN_DEVICE, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                long paoId = 67;

                String rfnIdentifier = devEventLog.getIndicatorString() + "RfnIdentifier";
                String templateName = devEventLog.getIndicatorString() + "TemplateName";
                String deviceName = devEventLog.getIndicatorString() + "DeviceName";
                String sensorManufacturer =
                    devEventLog.getIndicatorString() + "SensorManufacturer";
                String sensorModel = devEventLog.getIndicatorString() + "SensorModel";
                String sensorSerialNumber = "45666545";

                rfnDeviceEventLogService.createdNewDeviceAutomatically(paoId, rfnIdentifier,
                    templateName, deviceName);
                rfnDeviceEventLogService.receivedDataForUnkownDeviceTemplate(templateName);
                rfnDeviceEventLogService.unableToCreateDeviceFromTemplate(templateName,
                    sensorManufacturer, sensorModel, sensorSerialNumber);
            }
        });
        executables.put(LogType.STARS, new DevEventLogExecutable() {
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
                int ecId = 456;

                starsEventLogService.deleteEnergyCompanyAttempted(user, yukonEnergyCompany,
                    devEventLog.getEventSource());
                starsEventLogService.deleteEnergyCompany(user, yukonEnergyCompany);
                starsEventLogService.energyCompanyDefaultRouteChanged(user, energyCompanyName,
                    oldRouteId, newRouteId);
                starsEventLogService.energyCompanySettingUpdated(user,
                    EnergyCompanySettingType.METER_MCT_BASE_DESIGNATION, ecId, "Fake value");
                starsEventLogService.addWarehouseAttempted(user, warehouseName,
                    devEventLog.getEventSource());
                starsEventLogService.updateWarehouseAttempted(user, warehouseName,
                    devEventLog.getEventSource());
                starsEventLogService.deleteWarehouseAttempted(user, warehouseName,
                    devEventLog.getEventSource());
                starsEventLogService.addWarehouse(warehouseName);
                starsEventLogService.updateWarehouse(warehouseName);
                starsEventLogService.deleteWarehouse(warehouseName);
                starsEventLogService.cancelCurrentOptOutsAttempted(user, devEventLog.getEventSource());
                starsEventLogService.cancelCurrentOptOutsByProgramAttempted(user, programName,
                    devEventLog.getEventSource());
                starsEventLogService.cancelCurrentOptOuts(user);
                starsEventLogService.cancelCurrentOptOutsByProgram(user, programName);
                starsEventLogService.enablingOptOutUsageForTodayAttempted(user,
                    devEventLog.getEventSource());
                starsEventLogService.enablingOptOutUsageForTodayByProgramAttempted(user, programName,
                    devEventLog.getEventSource());
                starsEventLogService.disablingOptOutUsageForTodayAttempted(user,
                    devEventLog.getEventSource());
                starsEventLogService.disablingOptOutUsageForTodayByProgramAttempted(user, programName,
                    devEventLog.getEventSource());
                starsEventLogService.optOutUsageEnabledToday(user, true, true);
                starsEventLogService.optOutUsageEnabledTodayForProgram(user, programName, true, true);
                starsEventLogService.countTowardOptOutLimitTodayAttempted(user,
                    devEventLog.getEventSource());
                starsEventLogService.countTowardOptOutLimitTodayByProgramAttempted(user, programName,
                    devEventLog.getEventSource());
                starsEventLogService.doNotCountTowardOptOutLimitTodayAttempted(user,
                    devEventLog.getEventSource());
                starsEventLogService.doNotCountTowardOptOutLimitTodayByProgramAttempted(user,
                    programName, devEventLog.getEventSource());
                starsEventLogService.countTowardOptOutLimitToday(user, optOutsCount);
                starsEventLogService.countTowardOptOutLimitTodayForProgram(user, programName,
                    optOutsCount);
            }
        });
        executables.put(LogType.SYSTEM, new DevEventLogExecutable() {
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
                systemEventLogService.loginUsernameChangeAttempted(user, newUsername,
                    devEventLog.getEventSource());
                systemEventLogService.loginChangeAttempted(user, username,
                    devEventLog.getEventSource());
                systemEventLogService.usernameChanged(user, oldUsername, newUsername);
                systemEventLogService.loginWeb(user, remoteAddress);
                systemEventLogService.loginClient(user, remoteAddress);
                systemEventLogService.loginOutboundVoice(user, remoteAddress);
                systemEventLogService.rphDeleteDuplicates(rowsDeleted, start, finish);
                systemEventLogService.rphDeleteDanglingEntries(rowsDeleted, start, finish);
                systemEventLogService.systemLogDeleteDanglingEntries(rowsDeleted, start, finish);
                systemEventLogService.globalSettingChanged(user, GlobalSettingType.ACCT_PORT, "abc");
            }
        });
        executables.put(LogType.VALIDATION, new DevEventLogExecutable() {
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

                validationEventLogService.unreasonableValueCausedReRead(paoId, paoName, paoType,
                    pointId, pointType, pointOffset);
                validationEventLogService.validationEngineStartup(lastChangeIdProcessed,
                    tagsCleared);
                validationEventLogService.changedQualityOnPeakedValue(changeId, paoId, paoName,
                    paoType, pointId, pointType, pointOffset);
                validationEventLogService.validationEngineReset(user);
                validationEventLogService.validationEnginePartialReset(validationResetDate, user);
                validationEventLogService.deletedAllTaggedRows(tagSet, user);
                validationEventLogService.acceptedAllTaggedRows(tagSet, user);
            }
        });
        executables.put(LogType.VEE_REVIEW, new DevEventLogExecutable() {
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

                veeReviewEventLogService.deletePointValue(changeId, value, timestamp, paoName,
                    paoType, pointId, pointType, pointOffset, user);
                veeReviewEventLogService.acceptPointValue(changeId, value, timestamp, paoName,
                    paoType, pointId, pointType, pointOffset, user);
                veeReviewEventLogService.updateQuestionableQuality(changeId, value, timestamp,
                    paoName, paoType, pointId, pointType, pointOffset, user);
            }
        });
        executables.put(LogType.ZIGBEE, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());

                String paoName = devEventLog.getIndicatorString() + "PaoName";
                String message = devEventLog.getIndicatorString() + "Message";
                String gatewayName = devEventLog.getIndicatorString() + "GatewayName";

                zigbeeEventLogService.zigbeeDeviceCommission(yukonUser, paoName,
                    devEventLog.getEventSource());
                zigbeeEventLogService.zigbeeDeviceCommissioned(paoName);
                zigbeeEventLogService.zigbeeDeviceDecommission(yukonUser, paoName,
                    devEventLog.getEventSource());
                zigbeeEventLogService.zigbeeDeviceDecommissioned(paoName);
                zigbeeEventLogService.zigbeeDeviceRefresh(yukonUser, paoName,
                    devEventLog.getEventSource());
                zigbeeEventLogService.zigbeeDeviceRefreshed(paoName);
                zigbeeEventLogService.zigbeeSendText(yukonUser, paoName, message,
                    devEventLog.getEventSource());
                zigbeeEventLogService.zigbeeSentText(paoName, message);
                zigbeeEventLogService.zigbeeDeviceAssign(yukonUser, paoName, gatewayName,
                    devEventLog.getEventSource());
                zigbeeEventLogService.zigbeeDeviceAssigned(paoName, gatewayName);
                zigbeeEventLogService.zigbeeDeviceUnassign(yukonUser, paoName, gatewayName,
                    devEventLog.getEventSource());
                zigbeeEventLogService.zigbeeDeviceUnassigned(paoName, gatewayName);
            }
        });
        executables.put(LogType.ECOBEE, new DevEventLogExecutable() {
            @Override
            public void execute(DevEventLog devEventLog) {
                LiteYukonUser yukonUser = new LiteYukonUser(0, devEventLog.getUsername());
                Instant endDate = Instant.now();
                Instant startDate = Instant.now().minus(Duration.standardDays(1));
                String loadGroupIds = devEventLog.getIndicatorString() + "123, 456, 789";

                ecobeeEventLogService.syncIssueFixed(yukonUser,
                    EcobeeDiscrepancyType.EXTRANEOUS_DEVICE.toString(), devEventLog.getEventSource());
                ecobeeEventLogService.allSyncIssuesFixed(yukonUser, devEventLog.getEventSource());
                ecobeeEventLogService.dataDownloaded(yukonUser, startDate, endDate, loadGroupIds,
                    devEventLog.getEventSource());
            }
        });
        eventLogExecutables = ImmutableMap.copyOf(executables);
    }

    public interface DevEventLogExecutable {
        public void execute(DevEventLog devEventLog);
    }

    public static enum LogType implements Displayable {
        ACCOUNT(AccountEventLogService.class, 57),
        COMMAND_REQUEST_EXECUTOR(CommandRequestExecutorEventLogService.class, 2),
        COMMAND_SCHEDULE(CommandScheduleEventLogService.class, 6),
        DATABASE_MIGRATION(DatabaseMigrationEventLogService.class, 3),
        DEMAND_RESPONSE(DemandResponseEventLogService.class, 36),
        ECOBEE(EcobeeEventLogService.class, 3),
        HARDWARE(HardwareEventLogService.class, 22),
        INVENTORY_CONFIG(InventoryConfigEventLogService.class, 4),
        METERING(MeteringEventLogService.class, 2),
        OUTAGE(OutageEventLogService.class, 10),
        RFN_DEVICE(RfnDeviceEventLogService.class, 3),
        STARS(StarsEventLogService.class, 26),
        SYSTEM(SystemEventLogService.class, 12),
        VALIDATION(ValidationEventLogService.class, 7),
        VEE_REVIEW(VeeReviewEventLogService.class, 3),
        ZIGBEE(ZigbeeEventLogService.class, 12),
        ;

        private final Class<?> eventLogServiceClass;
        private final int numberOfMethodsTested;
        
        LogType(Class<?> eventLogServiceClass, int numberOfMethodsTested) {
            this.eventLogServiceClass = eventLogServiceClass;
            this.numberOfMethodsTested = numberOfMethodsTested;
        }

        public int getNumberOfMethodsTested() {
            return numberOfMethodsTested;
        }

        public int getActualNumberOfMethods() {
            return eventLogServiceClass.getMethods().length;
        }

        @Override
        public MessageSourceResolvable getMessage() {
            return YukonMessageSourceResolvable.createDefaultWithoutCode(eventLogServiceClass.getSimpleName());
        }
    }

    private static int complete;
    private static int total;
    private static final ReentrantLock _lock = new ReentrantLock();

    public boolean isRunning() {
        return _lock.isLocked();
    }

    public void execute(DevEventLog devEventLog) {

        if (_lock.tryLock()) {
            try {
                int iterations = devEventLog.getIterations();

                complete = 0;
                total = devEventLog.getTotal();

                Map<LogType, Boolean> eventLogTypes = devEventLog.getEventLogTypes();
                for (LogType logType : eventLogTypes.keySet()) {
                    if (eventLogTypes.get(logType)) {
                        log.info("Inserting EventLog entries for " + logType);
                        DevEventLogExecutable devEventLogExecutable = eventLogExecutables.get(logType);
                        for (int i = 0; i < iterations ; i++) {
                            devEventLogExecutable.execute(devEventLog);
                            complete += logType.getNumberOfMethodsTested();
                        }
                    }
                }
            } finally {
                _lock.unlock();
            }
        }
    }

    public int getPercentComplete() {
        if (total >= 1) {
            return (complete * 100) / total;
        } else {
            return 0;
        }
    }
}

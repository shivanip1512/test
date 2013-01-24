/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11672 */
/* @error ignore-begin */
CREATE TABLE ProgramToSeasonalProgram  (
   AssignedProgramId      NUMBER                 NOT NULL,
   SeasonalProgramId      NUMBER                 NOT NULL,
   CONSTRAINT PK_ProgramToSeasonalProgram PRIMARY KEY (AssignedProgramId)
);

ALTER TABLE ProgramToSeasonalProgram
    ADD CONSTRAINT FK_ProgSeaProg_LMProgWebPub_AP FOREIGN KEY (AssignedProgramId)
        REFERENCES LMProgramWebPublishing (ProgramId)
            ON DELETE CASCADE;

ALTER TABLE ProgramToSeasonalProgram
    ADD CONSTRAINT FK_ProgSeaProg_LMProgWebPub_SP FOREIGN KEY (SeasonalProgramId)
        REFERENCES LMProgramWebPublishing (ProgramId);
/* @error ignore-end */
/* End YUK-11672 */

/* Start YUK-11744 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES (-1126, -2, 'Alternate Program Enrollment', 'false', 'Enables the use of alternate program enrollments.');
/* @error ignore-end */
/* End YUK-11744 */

/* Start YUK-11754 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES (-1125, -2, 'Broadcast Cancel All Opt Out SPID', ' ', 'The SPID to use when sending the broadcast cancel all opt out command.'); 
/* @error ignore-end */
/* End YUK-11754 */

/* Start YUK-11733 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES (-40012, -400, 'Grouped Control History Display', 'true', 'Controls whether to enable grouped control history display.');
/* @error ignore-end */
/* End YUK-11733 */

/* Start YUK-11794 */
ALTER TABLE DeviceGroup 
ADD CreatedDate DATE;
UPDATE DeviceGroup 
SET CreatedDate = '01-JAN-2013';
ALTER TABLE DeviceGroup 
MODIFY CreatedDate DATE NOT NULL;
/* End YUK-11794 */

/* Start YUK-11742 */
UPDATE EventLog
SET String3 = String2,
    String2 = NULL
WHERE String3 IS NULL
  AND EventType IN (
    'hardware.hardwareUpdateAttemptedByOperator',
    'hardware.hardwareUpdateAttemptedThroughAccountImporter',
    'hardware.hardwareCreationAttemptedThroughAccountImporter',
    'hardware.hardwareRemovalAttemptedThroughAccountImporter'
);

UPDATE EventLog
SET String4 = String3,
    String3 = String2,
    String2 = NULL
WHERE EventType = 'stars.account.thermostat.thermostatManualSetAttemptedByApi';

/* - ByApi         */
UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByApi') - 1),
    String2 = 'API'
WHERE String2 = NULL 
  AND EventType IN (
    'stars.optOutAdmin.cancelCurrentOptOutsAttemptedByApi',
    'stars.optOutAdmin.countTowardOptOutLimitTodayAttemptedByApi',
    'stars.optOutAdmin.disablingOptOutUsageForTodayAttemptedByApi',
    'stars.optOutAdmin.doNotCountTowardOptOutLimitTodayAttemptedByApi',
    'stars.optOutAdmin.enablingOptOutUsageForTodayAttemptedByApi'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByApi') - 1),
    String3 = 'API'
WHERE String3 IS NULL 
  AND EventType IN (
    'hardware.hardwareDeletionAttemptedByApi',
    'hardware.hardwareMeterCreationAttemptedByApi',
    'hardware.hardwareRemovalAttemptedByApi',
    'hardware.twoWayHardwareCreationAttemptedByApi',
    'stars.energyCompany.deleteEnergyCompanyAttemptedByApi',
    'stars.energyCompanySettings.energyCompanyDefaultRouteChangedByApi',
    'stars.optOutAdmin.cancelCurrentOptOutsByProgramAttemptedByApi',
    'stars.optOutAdmin.countTowardOptOutLimitTodayByProgramAttemptedByApi',
    'stars.optOutAdmin.disablingOptOutUsageForTodayByProgramAttemptedByApi',
    'stars.optOutAdmin.doNotCountTowardOptOutLimitTodayByProgramAttemptedByApi',
    'stars.optOutAdmin.enablingOptOutUsageForTodayByProgramAttemptedByApi',
    'stars.warehouse.addWarehouseAttemptedByApi',
    'stars.warehouse.deleteWarehouseAttemptedByApi',
    'stars.warehouse.updateWarehouseAttemptedByApi',
    'system.login.loginChangeAttemptedByApi',
    'system.login.loginUsernameChangeAttemptedByApi',
    'stars.account.accountCreationAttemptedByApi',
    'stars.account.accountUpdateAttemptedByApi',
    'stars.account.enrollment.enrollmentModificationAttemptedByApi',
    'zigbee.action.zigbeeDeviceRefreshAttemptedByApi',
    'zigbee.config.zigbeeDeviceCommissionAttemptedByApi',
    'zigbee.config.zigbeeDeviceDecommissionAttemptedByApi',
    'stars.account.accountDeletionAttemptedByApi'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByApi') - 1),
    String4 = 'API'
WHERE String4 IS NULL 
  AND EventType IN (
    'hardware.assigningHardwareAttemptedByApi',
    'hardware.config.hardwareConfigAttemptedByApi',
    'hardware.config.hardwareDisableAttemptedByApi',
    'hardware.config.hardwareEnableAttemptedByApi',
    'hardware.hardwareAdditionAttemptedByApi',
    'hardware.hardwareChangeOutAttemptedByApi',
    'hardware.hardwareChangeOutForMeterAttemptedByApi',
    'hardware.hardwareCreationAttemptedByApi',
    'hardware.hardwareRemovalAttemptedByApi',
    'hardware.hardwareUpdateAttemptedByApi',
    'stars.account.optOut.optOutLimitResetAttemptedByApi',
    'stars.account.optOut.optOutResendAttemptedByApi',
    'stars.account.optOut.activeOptOutCancelAttemptedByApi',
    'stars.account.optOut.optOutAttemptedByApi',
    'stars.account.optOut.optOutCancelAttemptedByApi',
    'stars.account.optOut.optOutLimitReductionAttemptedByApi',
    'stars.account.optOut.scheduledOptOutCancelAttemptedByApi',
    'zigbee.action.zigbeeSendTextAttemptedByApi',
    'zigbee.config.zigbeeDeviceAssignAttemptedByApi',
    'stars.account.workOrder.workOrderCreationAttemptedByApi',
    'zigbee.config.zigbeeDeviceUnassignAttemptedByApi',
    'stars.account.workOrder.workOrderDeletionAttemptedByApi'
);

UPDATE EventLog
SET
    EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByApi') - 1),
    String5 = 'API'
WHERE EventType = 'stars.account.thermostat.thermostatScheduleSavingAttemptedByApi';

UPDATE EventLog
SET
  EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByApi') - 1),
  String6 = 'API'
WHERE String6 IS NULL 
AND EventType IN (
    'stars.account.appliance.applianceAdditionAttemptedByApi',
    'stars.account.enrollment.enrollmentAttemptedByApi',
    'stars.account.enrollment.unenrollmentAttemptedByApi',
    'stars.account.thermostat.thermostatManualSetAttemptedByApi',
    'stars.account.appliance.applianceDeletionAttemptedByApi'
);

/* - ThroughApi */
UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ThroughApi') - 1),
    String2 = 'API'
WHERE String2 IS NULL 
  AND EventType IN (
    'stars.optOutAdmin.cancelCurrentOptOutsAttemptedThroughApi',
    'stars.optOutAdmin.countTowardOptOutLimitTodayAttemptedThroughApi',
    'stars.optOutAdmin.disablingOptOutUsageForTodayAttemptedThroughApi',
    'stars.optOutAdmin.doNotCountTowardOptOutLimitTodayAttemptedThroughApi',
    'stars.optOutAdmin.enablingOptOutUsageForTodayAttemptedThroughApi',
    'system.login.loginPasswordChangeAttemptedThroughApi'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ThroughApi') - 1),
    String3 = 'API'
WHERE String3 IS NULL 
  AND EventType IN (
    'hardware.hardwareDeletionAttemptedThroughApi',
    'hardware.hardwareMeterCreationAttemptedThroughApi',
    'hardware.hardwareRemovalAttemptedThroughApi',
    'hardware.twoWayHardwareCreationAttemptedThroughApi',
    'stars.energyCompany.deleteEnergyCompanyAttemptedThroughApi',
    'stars.energyCompanySettings.energyCompanyDefaultRouteChangedThroughApi',
    'stars.optOutAdmin.cancelCurrentOptOutsByProgramAttemptedThroughApi',
    'stars.optOutAdmin.countTowardOptOutLimitTodayByProgramAttemptedThroughApi',
    'stars.optOutAdmin.disablingOptOutUsageForTodayByProgramAttemptedThroughApi',
    'stars.optOutAdmin.doNotCountTowardOptOutLimitTodayByProgramAttemptedThroughApi',
    'stars.optOutAdmin.enablingOptOutUsageForTodayByProgramAttemptedThroughApi',
    'stars.warehouse.addWarehouseAttemptedThroughApi',
    'stars.warehouse.deleteWarehouseAttemptedThroughApi',
    'stars.warehouse.updateWarehouseAttemptedThroughApi',
    'system.login.loginUsernameChangeAttemptedThroughApi',
    'stars.account.accountCreationAttemptedThroughApi',
    'stars.account.accountUpdateAttemptedThroughApi',
    'stars.account.enrollment.enrollmentModificationAttemptedThroughApi',
    'zigbee.action.zigbeeDeviceRefreshAttemptedThroughApi',
    'zigbee.config.zigbeeDeviceCommissionAttemptedThroughApi',
    'zigbee.config.zigbeeDeviceDecommissionAttemptedThroughApi',
    'stars.account.accountDeletionAttemptedThroughApi'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ThroughApi') - 1),
    String4 = 'API'
WHERE String4 IS NULL 
  AND EventType IN (
    'hardware.assigningHardwareAttemptedThroughApi',
    'hardware.config.hardwareConfigAttemptedThroughApi',
    'hardware.config.hardwareDisableAttemptedThroughApi',
    'hardware.config.hardwareEnableAttemptedThroughApi',
    'hardware.hardwareAdditionAttemptedThroughApi',
    'hardware.hardwareChangeOutAttemptedThroughApi',
    'hardware.hardwareChangeOutForMeterAttemptedThroughApi',
    'hardware.hardwareCreationAttemptedThroughApi',
    'hardware.hardwareRemovalAttemptedThroughApi',
    'hardware.hardwareUpdateAttemptedThroughApi',
    'stars.account.optOut.optOutLimitResetAttemptedThroughApi',
    'stars.account.optOut.optOutResendAttemptedThroughApi',
    'stars.account.optOut.activeOptOutCancelAttemptedThroughApi',
    'stars.account.optOut.optOutAttemptedThroughApi',
    'stars.account.optOut.optOutCancelAttemptedThroughApi',
    'stars.account.optOut.optOutLimitReductionAttemptedThroughApi',
    'stars.account.optOut.scheduledOptOutCancelAttemptedThroughApi',
    'zigbee.action.zigbeeSendTextAttemptedThroughApi',
    'zigbee.config.zigbeeDeviceAssignAttemptedThroughApi',
    'stars.account.workOrder.workOrderCreationAttemptedThroughApi',
    'zigbee.config.zigbeeDeviceUnassignAttemptedThroughApi',
    'stars.account.workOrder.workOrderDeletionAttemptedThroughApi'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ThroughApi') - 1),
    String5 = 'API'
WHERE EventType = 'stars.account.thermostat.thermostatScheduleSavingAttemptedThroughApi';

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ThroughApi') - 1),
    String6 = 'API'
WHERE String6 IS NULL 
  AND EventType IN (
    'stars.account.appliance.applianceAdditionAttemptedThroughApi',
    'stars.account.enrollment.enrollmentAttemptedThroughApi',
    'stars.account.thermostat.thermostatManualSetAttemptedThroughApi',
    'stars.account.enrollment.unenrollmentAttemptedThroughApi',
    'stars.account.appliance.applianceDeletionAttemptedThroughApi'
);

/* - ByConsumer */
UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByConsumer') - 1),
    String2 = 'CONSUMER'
WHERE String2 IS NULL 
  AND EventType IN (
    'stars.optOutAdmin.cancelCurrentOptOutsAttemptedByConsumer',
    'stars.optOutAdmin.countTowardOptOutLimitTodayAttemptedByConsumer',
    'stars.optOutAdmin.disablingOptOutUsageForTodayAttemptedByConsumer',
    'stars.optOutAdmin.doNotCountTowardOptOutLimitTodayAttemptedByConsumer',
    'stars.optOutAdmin.enablingOptOutUsageForTodayAttemptedByConsumer',
    'system.login.loginPasswordChangeAttemptedByConsumer'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByConsumer') - 1),
    String3 = 'CONSUMER'
WHERE String3 IS NULL 
  AND EventType IN (
    'hardware.hardwareDeletionAttemptedByConsumer',
    'hardware.hardwareMeterCreationAttemptedByConsumer',
    'hardware.hardwareRemovalAttemptedByConsumer',
    'hardware.twoWayHardwareCreationAttemptedByConsumer',
    'stars.energyCompany.deleteEnergyCompanyAttemptedByConsumer',
    'stars.energyCompanySettings.energyCompanyDefaultRouteChangedByConsumer',
    'stars.optOutAdmin.cancelCurrentOptOutsByProgramAttemptedByConsumer',
    'stars.optOutAdmin.countTowardOptOutLimitTodayByProgramAttemptedByConsumer',
    'stars.optOutAdmin.disablingOptOutUsageForTodayByProgramAttemptedByConsumer',
    'stars.optOutAdmin.doNotCountTowardOptOutLimitTodayByProgramAttemptedByConsumer',
    'stars.optOutAdmin.enablingOptOutUsageForTodayByProgramAttemptedByConsumer',
    'stars.warehouse.addWarehouseAttemptedByConsumer',
    'stars.warehouse.deleteWarehouseAttemptedByConsumer',
    'stars.warehouse.updateWarehouseAttemptedByConsumer',
    'system.login.loginChangeAttemptedByConsumer',
    'system.login.loginUsernameChangeAttemptedByConsumer',
    'stars.account.accountCreationAttemptedByConsumer',
    'stars.account.accountUpdateAttemptedByConsumer',
    'stars.account.enrollment.enrollmentModificationAttemptedByConsumer',
    'zigbee.action.zigbeeDeviceRefreshAttemptedByConsumer',
    'zigbee.config.zigbeeDeviceCommissionAttemptedByConsumer',
    'zigbee.config.zigbeeDeviceDecommissionAttemptedByConsumer',
    'stars.account.accountDeletionAttemptedByConsumer'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByConsumer') - 1),
    String4 = 'CONSUMER'
WHERE String4 IS NULL 
  AND EventType IN (
    'hardware.assigningHardwareAttemptedByConsumer',
    'hardware.config.hardwareConfigAttemptedByConsumer',
    'hardware.config.hardwareDisableAttemptedByConsumer',
    'hardware.config.hardwareEnableAttemptedByConsumer',
    'hardware.hardwareAdditionAttemptedByConsumer',
    'hardware.hardwareChangeOutAttemptedByConsumer',
    'hardware.hardwareChangeOutForMeterAttemptedByConsumer',
    'hardware.hardwareCreationAttemptedByConsumer',
    'hardware.hardwareRemovalAttemptedByConsumer',
    'hardware.hardwareUpdateAttemptedByConsumer',
    'stars.account.optOut.optOutLimitResetAttemptedByConsumer',
    'stars.account.optOut.optOutResendAttemptedByConsumer',
    'stars.account.optOut.activeOptOutCancelAttemptedByConsumer',
    'stars.account.optOut.optOutAttemptedByConsumer',
    'stars.account.optOut.optOutCancelAttemptedByConsumer',
    'stars.account.optOut.optOutLimitReductionAttemptedByConsumer',
    'stars.account.optOut.scheduledOptOutCancelAttemptedByConsumer',
    'zigbee.action.zigbeeSendTextAttemptedByConsumer',
    'zigbee.config.zigbeeDeviceAssignAttemptedByConsumer',
    'stars.account.workOrder.workOrderCreationAttemptedByConsumer',
    'zigbee.config.zigbeeDeviceUnassignAttemptedByConsumer',
    'stars.account.workOrder.workOrderDeletionAttemptedByConsumer'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByConsumer') - 1),
    String5 = 'API'
WHERE EventType = 'stars.account.thermostat.thermostatScheduleSavingAttemptedByConsumer';

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByConsumer') - 1),
    String6 = 'API'
WHERE String6 IS NULL 
  AND EventType IN (
    'stars.account.appliance.applianceAdditionAttemptedByConsumer',
    'stars.account.enrollment.enrollmentAttemptedByConsumer',
    'stars.account.thermostat.thermostatManualSetAttemptedByConsumer',
    'stars.account.enrollment.unenrollmentAttemptedByConsumer',
    'stars.account.appliance.applianceDeletionAttemptedByConsumer'
);

/* - ByOperator */
UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByOperator') - 1),
    String2 = 'OPERATOR'
WHERE String2 IS NULL 
  AND EventType IN (
    'stars.optOutAdmin.cancelCurrentOptOutsAttemptedByOperator',
    'stars.optOutAdmin.countTowardOptOutLimitTodayAttemptedByOperator',
    'stars.optOutAdmin.disablingOptOutUsageForTodayAttemptedByOperator',
    'stars.optOutAdmin.doNotCountTowardOptOutLimitTodayAttemptedByOperator',
    'stars.optOutAdmin.enablingOptOutUsageForTodayAttemptedByOperator',
    'system.login.loginPasswordChangeAttemptedByOperator'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByOperator') - 1),
    String3 = 'OPERATOR'
WHERE String3 IS NULL 
  AND EventType IN (
    'hardware.commands.config.zigbeeDeviceCommissionByOperator',
    'hardware.commands.config.zigbeeDeviceDecommissionByOperator',
    'hardware.commands.status.zigbeeDeviceRefreshByOperator',
    'hardware.hardwareDeletionAttemptedByOperator',
    'hardware.hardwareMeterCreationAttemptedByOperator',
    'hardware.hardwareRemovalAttemptedByOperator',
    'hardware.twoWayHardwareCreationAttemptedByOperator',
    'stars.energyCompany.deleteEnergyCompanyAttemptedByOperator',
    'stars.energyCompanySettings.energyCompanyDefaultRouteChangedByOperator',
    'stars.optOutAdmin.cancelCurrentOptOutsByProgramAttemptedByOperator',
    'stars.optOutAdmin.countTowardOptOutLimitTodayByProgramAttemptedByOperator',
    'stars.optOutAdmin.disablingOptOutUsageForTodayByProgramAttemptedByOperator',
    'stars.optOutAdmin.doNotCountTowardOptOutLimitTodayByProgramAttemptedByOperator',
    'stars.optOutAdmin.enablingOptOutUsageForTodayByProgramAttemptedByOperator',
    'stars.warehouse.addWarehouseAttemptedByOperator',
    'stars.warehouse.deleteWarehouseAttemptedByOperator',
    'stars.warehouse.updateWarehouseAttemptedByOperator',
    'system.login.loginChangeAttemptedByOperator',
    'system.login.loginUsernameChangeAttemptedByOperator',
    'stars.account.accountCreationAttemptedByOperator',
    'stars.account.accountUpdateAttemptedByOperator',
    'stars.account.enrollment.enrollmentModificationAttemptedByOperator',
    'zigbee.action.zigbeeDeviceRefreshAttemptedByOperator',
    'zigbee.config.zigbeeDeviceCommissionAttemptedByOperator',
    'zigbee.config.zigbeeDeviceDecommissionAttemptedByOperator',
    'stars.account.accountDeletionAttemptedByOperator'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByOperator') - 1),
    String4 = 'OPERATOR'
WHERE String4 IS NULL 
  AND EventType IN (
    'hardware.assigningHardwareAttemptedByOperator',
    'hardware.commands.actions.zigbeeSendTextByOperator',
    'hardware.config.hardwareConfigAttemptedByOperator',
    'hardware.config.hardwareDisableAttemptedByOperator',
    'hardware.config.hardwareEnableAttemptedByOperator',
    'hardware.config.zigbeeDeviceAssignByOperator',
    'hardware.config.zigbeeDeviceUnassignByOperator',
    'hardware.hardwareAdditionAttemptedByOperator',
    'hardware.hardwareChangeOutAttemptedByOperator',
    'hardware.hardwareChangeOutForMeterAttemptedByOperator',
    'hardware.hardwareCreationAttemptedByOperator',
    'hardware.hardwareRemovalAttemptedByOperator',
    'hardware.hardwareUpdateAttemptedByOperator',
    'stars.account.optOut.optOutLimitResetAttemptedByOperator',
    'stars.account.optOut.optOutResendAttemptedByOperator',
    'stars.account.optOut.activeOptOutCancelAttemptedByOperator',
    'stars.account.optOut.optOutAttemptedByOperator',
    'stars.account.optOut.optOutCancelAttemptedByOperator',
    'stars.account.optOut.optOutLimitReductionAttemptedByOperator',
    'stars.account.optOut.scheduledOptOutCancelAttemptedByOperator',
    'zigbee.action.zigbeeSendTextAttemptedByOperator',
    'zigbee.config.zigbeeDeviceAssignAttemptedByOperator',
    'stars.account.workOrder.workOrderCreationAttemptedByOperator',
    'zigbee.config.zigbeeDeviceUnassignAttemptedByOperator',
    'stars.account.workOrder.workOrderDeletionAttemptedByOperator'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByOperator') - 1),
    String5 = 'API'
WHERE EventType = 'stars.account.thermostat.thermostatScheduleSavingAttemptedByOperator';

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ByOperator') - 1),
    String6 = 'API'
WHERE String6 IS NULL 
  AND EventType IN (
    'stars.account.appliance.applianceAdditionAttemptedByOperator',
    'stars.account.enrollment.enrollmentAttemptedByOperator',
    'stars.account.thermostat.thermostatManualSetAttemptedByOperator',
    'stars.account.enrollment.unenrollmentAttemptedByOperator',
    'stars.account.appliance.applianceDeletionAttemptedByOperator'
);

/* - ThroughAccountImporter */
UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ThroughAccountImporter') - 1),
    String2 = 'ACCOUNT_IMPORTER'
WHERE String2 IS NULL 
  AND EventType IN (
    'stars.optOutAdmin.cancelCurrentOptOutsAttemptedThroughAccountImporter',
    'stars.optOutAdmin.countTowardOptOutLimitTodayAttemptedThroughAccountImporter',
    'stars.optOutAdmin.disablingOptOutUsageForTodayAttemptedThroughAccountImporter',
    'stars.optOutAdmin.doNotCountTowardOptOutLimitTodayAttemptedThroughAccountImporter',
    'stars.optOutAdmin.enablingOptOutUsageForTodayAttemptedThroughAccountImporter',
    'system.login.loginPasswordChangeAttemptedThroughAccountImporter'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ThroughAccountImporter') - 1),
    String3 = 'ACCOUNT_IMPORTER'
WHERE String3 IS NULL 
  AND EventType IN (
    'hardware.hardwareDeletionAttemptedThroughAccountImporter',
    'hardware.hardwareMeterCreationAttemptedThroughAccountImporter',
    'hardware.hardwareRemovalAttemptedThroughAccountImporter',
    'hardware.twoWayHardwareCreationAttemptedThroughAccountImporter',
    'stars.energyCompany.deleteEnergyCompanyAttemptedThroughAccountImporter',
    'stars.energyCompanySettings.energyCompanyDefaultRouteChangedThroughAccountImporter',
    'stars.optOutAdmin.cancelCurrentOptOutsByProgramAttemptedThroughAccountImporter',
    'stars.optOutAdmin.countTowardOptOutLimitTodayByProgramAttemptedThroughAccountImporter',
    'stars.optOutAdmin.disablingOptOutUsageForTodayByProgramAttemptedThroughAccountImporter',
    'stars.optOutAdmin.doNotCountTowardOptOutLimitTodayByProgramAttemptedThroughAccountImporter',
    'stars.optOutAdmin.enablingOptOutUsageForTodayByProgramAttemptedThroughAccountImporter',
    'stars.warehouse.addWarehouseAttemptedThroughAccountImporter',
    'stars.warehouse.deleteWarehouseAttemptedThroughAccountImporter',
    'stars.warehouse.updateWarehouseAttemptedThroughAccountImporter',
    'system.login.loginChangeAttemptedThroughAccountImporter',
    'system.login.loginUsernameChangeAttemptedThroughAccountImporter',
    'stars.account.accountCreationAttemptedThroughAccountImporter',
    'stars.account.accountUpdateAttemptedThroughAccountImporter',
    'stars.account.enrollment.enrollmentModificationAttemptedThroughAccountImporter',
    'zigbee.action.zigbeeDeviceRefreshAttemptedThroughAccountImporter',
    'zigbee.config.zigbeeDeviceCommissionAttemptedThroughAccountImporter',
    'zigbee.config.zigbeeDeviceDecommissionAttemptedThroughAccountImporter',
    'stars.account.accountDeletionAttemptedThroughAccountImporter'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ThroughAccountImporter') - 1),
    String4 = 'ACCOUNT_IMPORTER'
WHERE String4 IS NULL 
  AND EventType IN (
    'hardware.assigningHardwareAttemptedThroughAccountImporter',
    'hardware.config.hardwareConfigAttemptedThroughAccountImporter',
    'hardware.config.hardwareDisableAttemptedThroughAccountImporter',
    'hardware.config.hardwareEnableAttemptedThroughAccountImporter',
    'hardware.hardwareAdditionAttemptedThroughAccountImporter',
    'hardware.hardwareChangeOutAttemptedThroughAccountImporter',
    'hardware.hardwareChangeOutForMeterAttemptedThroughAccountImporter',
    'hardware.hardwareCreationAttemptedThroughAccountImporter',
    'hardware.hardwareRemovalAttemptedThroughAccountImporter',
    'hardware.hardwareUpdateAttemptedThroughAccountImporter',
    'stars.account.optOut.optOutLimitResetAttemptedThroughAccountImporter',
    'stars.account.optOut.optOutResendAttemptedThroughAccountImporter',
    'stars.account.optOut.activeOptOutCancelAttemptedThroughAccountImporter',
    'stars.account.optOut.optOutAttemptedThroughAccountImporter',
    'stars.account.optOut.optOutCancelAttemptedThroughAccountImporter',
    'stars.account.optOut.optOutLimitReductionAttemptedThroughAccountImporter',
    'stars.account.optOut.scheduledOptOutCancelAttemptedThroughAccountImporter',
    'zigbee.action.zigbeeSendTextAttemptedThroughAccountImporter',
    'zigbee.config.zigbeeDeviceAssignAttemptedThroughAccountImporter',
    'stars.account.workOrder.workOrderCreationAttemptedThroughAccountImporter',
    'zigbee.config.zigbeeDeviceUnassignAttemptedThroughAccountImporter',
    'stars.account.workOrder.workOrderDeletionAttemptedThroughAccountImporter'
);

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ThroughAccountImporter') - 1),
    String5 = 'API'
WHERE EventType = 'stars.account.thermostat.thermostatScheduleSavingAttemptedThroughAccountImporter';

UPDATE EventLog
SET EventType = SUBSTR(EventType, 1, INSTR(EventType, 'ThroughAccountImporter') - 1),
    String6 = 'API'
WHERE String6 IS NULL 
  AND EventType IN (
    'stars.account.appliance.applianceAdditionAttemptedThroughAccountImporter',
    'stars.account.enrollment.enrollmentAttemptedThroughAccountImporter',
    'stars.account.thermostat.thermostatManualSetAttemptedThroughAccountImporter',
    'stars.account.enrollment.unenrollmentAttemptedThroughAccountImporter',
    'stars.account.appliance.applianceDeletionAttemptedThroughAccountImporter'
);
/* End YUK-11742 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('5.6', '21-JAN-2013', 'Latest Update', 1, GETDATE());
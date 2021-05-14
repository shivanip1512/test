/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-22834 */
/* @error warn-once */
/* @start-block */
DECLARE
    v_count NUMBER := 0;
    v_newLine VARCHAR2(2);
    v_errorText VARCHAR2(512);
BEGIN
    SELECT COUNT(*) INTO v_count FROM (SELECT ZoneName, COUNT(*) AS temp_count FROM Zone GROUP BY ZoneName HAVING COUNT(*) > 1);
    IF v_count > 0 THEN
        v_newLine := CHR(13) || CHR(10);
        v_errorText := 'IVVC Zone Names are now required to be unique.' || v_newLine
            || 'Setup has detected that IVVC Zones with duplicate names are present in the system.' || v_newLine
            || 'In order to proceed with the update this must be manually resolved.' || v_newLine
            || 'More information can be found in YUK-22834.' || v_newLine
            || 'To locate Zones that have duplicated names you can use the query below:' || v_newLine
            || 'SELECT ZoneName, COUNT(*) AS NumberOfOccurences FROM Zone GROUP BY ZoneName HAVING COUNT(*) > 1';
        RAISE_APPLICATION_ERROR(-20001, v_errorText);
    END IF;
END;
/
/* @end-block */
ALTER TABLE Zone ADD CONSTRAINT Ak_ZoneName UNIQUE (ZoneName);

INSERT INTO DBUpdates VALUES ('YUK-22834', '9.0.0', SYSDATE);
/* @end YUK-22834 */

/* @start YUK-22800 */

/* assign to change */
UPDATE EventLog 
SET EventType = 'device.configuration.changeConfigInitiated' 
WHERE EventType = 'device.configuration.assignConfigInitiated';
UPDATE EventLog 
SET EventType = 'device.configuration.changeConfigCompleted' 
WHERE EventType = 'device.configuration.assignConfigCompleted';
UPDATE EventLog 
SET EventType = 'device.configuration.changeConfigOfDeviceCompleted' 
WHERE EventType = 'device.configuration.assignConfigToDeviceCompleted';

/* unassign to remove */
UPDATE EventLog 
SET EventType = 'device.configuration.removeConfigInitiated' 
WHERE EventType = 'device.configuration.unassignConfigInitiated';
UPDATE EventLog 
SET EventType = 'device.configuration.removeConfigCompleted' 
WHERE EventType = 'device.configuration.unassignConfigCompleted';
UPDATE EventLog 
SET EventType = 'device.configuration.removeConfigToDeviceCompleted' 
WHERE EventType = 'device.configuration.unassignConfigToDeviceCompleted';

/* send to upload */
UPDATE EventLog 
SET EventType = 'device.configuration.uploadConfigInitiated' 
WHERE EventType = 'device.configuration.sendConfigInitiated';
UPDATE EventLog 
SET EventType = 'device.configuration.uploadConfigCompleted' 
WHERE EventType = 'device.configuration.sendConfigCompleted';
UPDATE EventLog 
SET EventType = 'device.configuration.uploadConfigCancelled' 
WHERE EventType = 'device.configuration.sendConfigCancelled';
UPDATE EventLog 
SET EventType = 'device.configuration.uploadConfigToDeviceInitiated' 
WHERE EventType = 'device.configuration.sendConfigToDeviceInitiated';
UPDATE EventLog 
SET EventType = 'device.configuration.uploadConfigToDeviceCompleted' 
WHERE EventType = 'device.configuration.sendConfigToDeviceCompleted';

/* read to validate */
UPDATE EventLog 
SET EventType = 'device.configuration.validateConfigInitiated' 
WHERE EventType = 'device.configuration.readConfigInitiated';
UPDATE EventLog 
SET EventType = 'device.configuration.validateConfigCompleted' 
WHERE EventType = 'device.configuration.readConfigCompleted';
UPDATE EventLog 
SET EventType = 'device.configuration.validateConfigCancelled' 
WHERE EventType = 'device.configuration.readConfigCancelled';
UPDATE EventLog 
SET EventType = 'device.configuration.validateConfigOnDeviceInitiated' 
WHERE EventType = 'device.configuration.readConfigFromDeviceInitiated';
UPDATE EventLog 
SET EventType = 'device.configuration.validateConfigOnDeviceCompleted' 
WHERE EventType = 'device.configuration.readConfigFromDeviceCompleted';

INSERT INTO DBUpdates VALUES ('YUK-22800', '9.0.0', SYSDATE);
/* @end YUK-22800 */

/* @start YUK-22983 */
/* @error ignore-begin */
ALTER TABLE MeterProgramStatus DROP CONSTRAINT FK_MeterProgramStatus_Device;
ALTER TABLE MeterProgramStatus DROP CONSTRAINT FK_MeterProgramStatus_DeviceMG;
/* @error ignore-end */
ALTER TABLE MeterProgramStatus
    ADD CONSTRAINT FK_MeterProgramStatus_DeviceMG FOREIGN KEY (DeviceId)
    REFERENCES DEVICEMETERGROUP (DEVICEID)
    ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-22983', '9.0.0', SYSDATE);
/* @end YUK-22983 */

/* @start YUK-23093 */
create table DeviceGuid  (
   DeviceId             NUMERIC                         not null,
   Guid                 CHAR(36)                        not null,
   constraint PK_DeviceGuid primary key (DeviceId)
);

alter table DeviceGuid
   add constraint AK_DeviceGuid_Guid unique (Guid);

alter table DeviceGuid
   add constraint FK_DeviceGuid_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
      on delete cascade;

INSERT INTO DBUpdates VALUES ('YUK-23093', '9.0.0', SYSDATE);
/* @end YUK-23093 */

/* @start YUK-23092 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-6200C', 1342);
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-6600C', 1343);
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-DisconnectC', 1344);

INSERT INTO DBUpdates VALUES ('YUK-23092', '9.0.0', SYSDATE);
/* @end YUK-23092 */

/* @start YUK-23295 */
UPDATE YukonRoleProperty
SET DefaultValue = 'OWNER'
WHERE RolePropertyId = -20220;

UPDATE YukonGroupRole 
SET Value = 'OWNER' 
WHERE RolePropertyID = -20220 
AND Value = 'true';

UPDATE YukonGroupRole 
SET Value = 'NO_ACCESS' 
WHERE RolePropertyID = -20220 
AND Value = 'false';

INSERT INTO DBUpdates VALUES ('YUK-23295', '9.0.0', SYSDATE);
/* @end YUK-23295 */

/* @start YUK-23290 */
create table LMGroupEatonCloud  (
   YukonGroupId         NUMBER                          not null,
   RelayUsage           VARCHAR2(15)                    not null,
   constraint PK_LMGROUPEATONCLOUD primary key (YukonGroupId)
);

alter table LMGroupEatonCloud
   add constraint FK_LMGroupEatonCloud_LMGroup foreign key (YukonGroupId)
      references LMGroup (DeviceID)
      on delete cascade;

INSERT INTO DBUpdates VALUES ('YUK-23290', '9.0.0', SYSDATE);
/* @end YUK-23290 */

/* @start YUK-23313 */
UPDATE YukonRoleProperty
SET DefaultValue = 'NO_ACCESS'
WHERE RolePropertyId = -20220;

UPDATE YukonGroupRole 
SET Value = 'OWNER' 
WHERE RolePropertyID = -20220 
AND Value = ' ';

INSERT INTO DBUpdates VALUES ('YUK-23313', '9.0.0', SYSDATE);
/* @end YUK-23313 */

/* @start YUK-23280 */
UPDATE ArchiveValuesExportField SET Pattern = 'DEFAULT' WHERE FieldType = 'ATTRIBUTE_NAME';

INSERT INTO DBUpdates VALUES ('YUK-23280', '9.0.0', SYSDATE);
/* @end YUK-23280 */

/* @start YUK-23348 */
ALTER TABLE LMItronCycleGear
DROP CONSTRAINT FK_LMItronCycleGear_LMPDirGear;

RENAME LMItronCycleGear TO LMConfigurableCycleGear;

ALTER TABLE LMConfigurableCycleGear RENAME CONSTRAINT PK_LMItronCycleGear TO PK_LMConfigurableCycleGear;

ALTER TABLE LMConfigurableCycleGear
   ADD CONSTRAINT FK_LMConfigurableCycleGear_LMPDirGear FOREIGN KEY (GearId)
      REFERENCES LMProgramDirectGear (GearID)
      ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-23348', '9.0.0', SYSDATE);
/* @end YUK-23348 */

/* @start YUK-23523 */
UPDATE Point
SET PointName = 'Peak kVA Lagging', PointOffset = 255
WHERE PointType = 'Analog'
AND PointOffset = 270
AND PaObjectId IN (
    SELECT DISTINCT PaObjectId FROM YukonPaObject
    WHERE Type IN ('RFN520FRX', 'RFN520FRXD', 'RFN530FRX')
);

INSERT INTO DBUpdates VALUES ('YUK-23523', '9.0.0', SYSDATE);
/* @end YUK-23523 */

/* @start YUK-23502 */
ALTER TABLE PortTiming
ADD PostCommWait NUMBER;

UPDATE PortTiming SET PostCommWait = 0;

ALTER TABLE PortTiming
MODIFY PostCommWait NUMBER NOT NULL;

INSERT INTO DBUpdates VALUES ('YUK-23502', '9.0.0', SYSDATE);
/* @end YUK-23502 */

/* @start YUK-23540 */
INSERT INTO StateGroup VALUES(-30, 'LCR Firmware Update Status', 'Status');
INSERT INTO State VALUES(-30, 0, 'Received and Waiting', 7, 6, 0);
INSERT INTO State VALUES(-30, 1, 'Nothing Pending', 9, 6, 0);
INSERT INTO State VALUES(-30, 2, 'Success', 0, 6, 0);
INSERT INTO State VALUES(-30, 3, 'Failed', 1, 6, 0);

INSERT INTO DBUpdates VALUES ('YUK-23540', '9.0.0', SYSDATE);
/* @end YUK-23540 */

/* @start YUK-23532 */
UPDATE Point
SET PointName = 'kVAh Lagging', PointOffset = 245
WHERE PointType = 'Analog'
AND PointOffset = 150
AND PaObjectId IN (
    SELECT DISTINCT PaObjectId FROM YukonPaObject
    WHERE Type IN ('RFN520FRX', 'RFN520FRXD', 'RFN530FRX')
);  

INSERT INTO DBUpdates VALUES ('YUK-23532', '9.0.0', SYSDATE);
/* @end YUK-23532 */

/* @start YUK-22912 */
CREATE INDEX INDX_CREUns_ExecId_DevId ON CommandRequestUnsupported (
CommandRequestExecId ASC,
DeviceId ASC
);

INSERT INTO DBUpdates VALUES ('YUK-22912', '9.0.0', SYSDATE);
/* @end YUK-22912 */

/* @start YUK-23267 */
CREATE INDEX INDX_LMHardwareEvent_InventoryID ON LMHardwareEvent (
InventoryID ASC
);

INSERT INTO DBUpdates VALUES ('YUK-23267', '9.0.0', SYSDATE);
/* @end YUK-23267 */

/* @start YUK-23969 */
INSERT INTO UnitMeasure VALUES (58, 'Therms', 0, 'Therms', '(none)');

INSERT INTO DBUpdates VALUES ('YUK-23969', '9.0.0', SYSDATE);
/* @end YUK-23969 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('9.0', '23-APR-2021', 'Latest Update', 0, SYSDATE);
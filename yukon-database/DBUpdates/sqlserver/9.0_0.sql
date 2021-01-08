/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-22834 */
/* @error warn-once */
/* @start-block */
BEGIN
    DECLARE @DuplicateZones NUMERIC = (SELECT COUNT(*) FROM (SELECT ZoneName, COUNT(*) AS temp_count FROM Zone GROUP BY ZoneName HAVING COUNT(*) > 1) AS temp_table)

IF @DuplicateZones > 0
    BEGIN
        DECLARE @NewLine CHAR(2) = CHAR(13) + CHAR(10);
        DECLARE @ErrorText VARCHAR(1024) = 'IVVC Zone Names are now required to be unique.' + @NewLine
            + 'Setup has detected that IVVC Zones with duplicate names are present in the system.' + @NewLine
            + 'In order to proceed with the update this must be manually resolved.' + @NewLine
            + 'More information can be found in YUK-22834.' + @NewLine
            + 'To locate Zones that have duplicated names you can use the query below:' + @NewLine
            + 'SELECT ZoneName, COUNT(*) AS NumberOfOccurences FROM Zone GROUP BY ZoneName HAVING COUNT(*) > 1';
        RAISERROR(@ErrorText, 16, 1);
    END;
END;
GO
/* @end-block */
ALTER TABLE Zone ADD CONSTRAINT Ak_ZoneName UNIQUE (ZoneName);

INSERT INTO DBUpdates VALUES ('YUK-22834', '9.0.0', GETDATE());
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

INSERT INTO DBUpdates VALUES ('YUK-22800', '9.0.0', GETDATE());
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

INSERT INTO DBUpdates VALUES ('YUK-22983', '9.0.0', GETDATE());
/* @end YUK-22983 */

/* @start YUK-23093 */
create table DeviceGuid (
   DeviceId             numeric              not null,
   Guid                 char(36)             not null,
   constraint PK_DeviceGuid primary key nonclustered (DeviceId)
)
go

alter table DeviceGuid
   add constraint AK_DeviceGuid_Guid unique (Guid)
go

alter table DeviceGuid
   add constraint FK_DeviceGuid_Device foreign key (DeviceId)
      references DEVICE (DEVICEID)
         on delete cascade
go

INSERT INTO DBUpdates VALUES ('YUK-23093', '9.0.0', GETDATE());
/* @end YUK-23093 */

/* @start YUK-23092 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-6200C', 1342);
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-6600C', 1343);
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'LCR-DisconnectC', 1344);

INSERT INTO DBUpdates VALUES ('YUK-23092', '9.0.0', GETDATE());
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

INSERT INTO DBUpdates VALUES ('YUK-23295', '9.0.0', GETDATE());
/* @end YUK-23295 */

/* @start YUK-23290 */
create table LMGroupEatonCloud (
   YukonGroupId         numeric              not null,
   RelayUsage           varchar(15)          not null,
   constraint PK_LMGROUPEATONCLOUD primary key (YukonGroupId)
)
go

alter table LMGroupEatonCloud
   add constraint FK_LMGroupEatonCloud_LMGroup foreign key (YukonGroupId)
      references LMGroup (DeviceID)
         on delete cascade
go

INSERT INTO DBUpdates VALUES ('YUK-23290', '9.0.0', GETDATE());
/* @end YUK-23290 */

/* @start YUK-23313 */
UPDATE YukonRoleProperty
SET DefaultValue = 'NO_ACCESS'
WHERE RolePropertyId = -20220;

UPDATE YukonGroupRole 
SET Value = 'OWNER' 
WHERE RolePropertyID = -20220 
AND Value = ' ';

INSERT INTO DBUpdates VALUES ('YUK-23313', '9.0.0', GETDATE());
/* @end YUK-23313 */

/* @start YUK-23280 */
UPDATE ArchiveValuesExportField SET Pattern = 'DEFAULT' WHERE FieldType = 'ATTRIBUTE_NAME';

INSERT INTO DBUpdates VALUES ('YUK-23280', '9.0.0', GETDATE());
/* @end YUK-23280 */

/* @start YUK-23348 */
ALTER TABLE LMItronCycleGear
DROP CONSTRAINT FK_LMItronCycleGear_LMPDirGear;
GO

SP_RENAME 'LMItronCycleGear','LMConfigurableCycleGear';
GO

SP_RENAME 'PK_LMItronCycleGear','PK_LMConfigurableCycleGear';
GO

ALTER TABLE LMConfigurableCycleGear
   ADD CONSTRAINT FK_LMConfigurableCycleGear_LMPDirGear FOREIGN KEY (GearId)
      REFERENCES LMProgramDirectGear (GearID)
         ON DELETE CASCADE;
GO

INSERT INTO DBUpdates VALUES ('YUK-23348', '9.0.0', GETDATE());
/* @end YUK-23348 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.0', '09-SEP-2020', 'Latest Update', 0, GETDATE()); */
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

INSERT INTO DBUpdates VALUES ('YUK-23295', '9.0.0', SYSDATE);
/* @end YUK-23295 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.0', '09-SEP-2020', 'Latest Update', 0, SYSDATE); */
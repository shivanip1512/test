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

/* @start YUK-23001 */
UPDATE DeviceGroupComposed SET CompositionType = 'INTERSECTION'
    WHERE DeviceGroupId IN
        (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum IN ('SERVICE_ACTIVE_RFW_METERS', 'SERVICE_ACTIVE_RF_ELECTRIC_METERS'));

INSERT INTO DBUpdates VALUES ('YUK-23001', '9.0.0', GETDATE());
/* @end YUK-23001 */

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

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.0', '09-SEP-2020', 'Latest Update', 0, GETDATE()); */
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

/* @start YUK-23001 */
UPDATE DeviceGroupComposed SET CompositionType = 'INTERSECTION'
    WHERE DeviceGroupId IN
        (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum IN ('SERVICE_ACTIVE_RFW_METERS', 'SERVICE_ACTIVE_RF_ELECTRIC_METERS'));

INSERT INTO DBUpdates VALUES ('YUK-23001', '9.0.0', SYSDATE);
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
SET EventType = 'device.configuration.validateConfigFromDeviceInitiated' 
WHERE EventType = 'device.configuration.readConfigFromDeviceInitiated';
UPDATE EventLog 
SET EventType = 'device.configuration.validateConfigFromDeviceCompleted' 
WHERE EventType = 'device.configuration.readConfigFromDeviceCompleted';

INSERT INTO DBUpdates VALUES ('YUK-22800', '9.0.0', SYSDATE);
/* @end YUK-22800 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.0', '09-SEP-2020', 'Latest Update', 0, SYSDATE); */
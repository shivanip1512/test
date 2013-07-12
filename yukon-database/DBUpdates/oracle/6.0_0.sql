/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12201 */
ALTER TABLE DeviceGroup
    DROP CONSTRAINT AK_DEVICEGR_PDG_GN;

ALTER TABLE DeviceGroup
    ADD CONSTRAINT AK_DeviceGroup_ParentDG_GrpNam unique (GroupName, ParentDeviceGroupId);

ALTER TABLE DeviceGroup 
ADD SystemGroupEnum VARCHAR2(255);

UPDATE DeviceGroup SET SystemGroupEnum = 'ROOT' WHERE GroupName = ' ' AND ParentDeviceGroupId IS NULL;
UPDATE DeviceGroup SET SystemGroupEnum = 'SYSTEM' WHERE GroupName = 'System' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'ROOT');
UPDATE DeviceGroup SET SystemGroupEnum = 'METERS' WHERE GroupName = 'Meters' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'ROOT');
UPDATE DeviceGroup SET SystemGroupEnum = 'MONITORS', Permission='NOEDIT_MOD' WHERE GroupName = 'Monitors' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'ROOT');
UPDATE DeviceGroup SET SystemGroupEnum = 'BILLING' WHERE GroupName = 'Billing' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'METERS');
UPDATE DeviceGroup SET SystemGroupEnum = 'COLLECTION' WHERE GroupName = 'Collection' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'METERS');
UPDATE DeviceGroup SET SystemGroupEnum = 'ALTERNATE' WHERE GroupName = 'Alternate' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'METERS');
UPDATE DeviceGroup SET SystemGroupEnum = 'FLAGS' WHERE GroupName = 'Flags' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'METERS');
UPDATE DeviceGroup SET SystemGroupEnum = 'CIS_SUBSTATION' WHERE GroupName = 'CIS Substation' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'METERS');
UPDATE DeviceGroup SET SystemGroupEnum = 'INVENTORY' WHERE GroupName = 'Inventory' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'FLAGS');
UPDATE DeviceGroup SET SystemGroupEnum = 'DISCONNECTED_STATUS' WHERE GroupName = 'DisconnectedStatus' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'FLAGS');
UPDATE DeviceGroup SET SystemGroupEnum = 'USAGE_MONITORING' WHERE GroupName = 'UsageMonitoring' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'FLAGS');
UPDATE DeviceGroup SET SystemGroupEnum = 'ROUTES' WHERE GroupName = 'Routes' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SYSTEM');
UPDATE DeviceGroup SET SystemGroupEnum = 'DEVICE_TYPES' WHERE GroupName = 'Device Types' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SYSTEM');
UPDATE DeviceGroup SET SystemGroupEnum = 'SYSTEM_METERS' WHERE GroupName = 'Meters' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SYSTEM');
UPDATE DeviceGroup SET SystemGroupEnum = 'SUBSTATIONS' WHERE GroupName = 'Substations' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SYSTEM');
UPDATE DeviceGroup SET SystemGroupEnum = 'ATTRIBUTES' WHERE GroupName = 'Attributes' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SYSTEM');
UPDATE DeviceGroup SET SystemGroupEnum = 'DEVICE_CONFIGS' WHERE GroupName = 'Device Configs' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SYSTEM');
UPDATE DeviceGroup SET SystemGroupEnum = 'AUTO' WHERE GroupName = 'Auto' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SYSTEM');
UPDATE DeviceGroup SET SystemGroupEnum = 'TEMPORARY' WHERE GroupName = 'Temporary' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SYSTEM');
UPDATE DeviceGroup SET SystemGroupEnum = 'SCANNING' WHERE GroupName = 'Scanning' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SYSTEM_METERS');
UPDATE DeviceGroup SET SystemGroupEnum = 'DISABLED' WHERE GroupName = 'Disabled' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SYSTEM_METERS');
UPDATE DeviceGroup SET SystemGroupEnum = 'DISCONNECT' WHERE GroupName = 'Disconnect' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SYSTEM_METERS');
UPDATE DeviceGroup SET SystemGroupEnum = 'LOAD_PROFILE' WHERE GroupName = 'Load Profile' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SCANNING');
UPDATE DeviceGroup SET SystemGroupEnum = 'VOLTAGE_PROFILE' WHERE GroupName = 'Voltage Profile' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SCANNING');
UPDATE DeviceGroup SET SystemGroupEnum = 'INTEGRITY' WHERE GroupName = 'Integrity' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SCANNING');
UPDATE DeviceGroup SET SystemGroupEnum = 'ACCUMULATOR' WHERE GroupName = 'Accumulator' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SCANNING');
UPDATE DeviceGroup SET SystemGroupEnum = 'COLLARS' WHERE GroupName = 'Collars' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'DISCONNECT');
UPDATE DeviceGroup SET SystemGroupEnum = 'SUPPORTED' WHERE GroupName = 'Supported' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'ATTRIBUTES');
UPDATE DeviceGroup SET SystemGroupEnum = 'EXISTING' WHERE GroupName = 'Existing' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'ATTRIBUTES');
UPDATE DeviceGroup SET SystemGroupEnum = 'DEVICE_DATA', Permission='NOEDIT_MOD' WHERE GroupName = 'DeviceData' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'MONITORS');
UPDATE DeviceGroup SET SystemGroupEnum = 'TAMPER_FLAG', Permission='NOEDIT_MOD' WHERE GroupName = 'Tamper Flag' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'MONITORS');
UPDATE DeviceGroup SET SystemGroupEnum = 'OUTAGE', Permission='NOEDIT_MOD' WHERE GroupName = 'Outage' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'MONITORS');
UPDATE DeviceGroup SET SystemGroupEnum = 'PHASE_DETECT', Permission='NOEDIT_MOD' WHERE GroupName = 'Phase Detect' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'SYSTEM_METERS');
UPDATE DeviceGroup SET SystemGroupEnum = 'LAST_RESULTS', Permission='NOEDIT_MOD' WHERE GroupName = 'Last Results' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'PHASE_DETECT');
UPDATE DeviceGroup SET SystemGroupEnum = 'A', Permission='NOEDIT_MOD' WHERE GroupName = 'A' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'LAST_RESULTS');
UPDATE DeviceGroup SET SystemGroupEnum = 'B', Permission='NOEDIT_MOD' WHERE GroupName = 'B' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'LAST_RESULTS');
UPDATE DeviceGroup SET SystemGroupEnum = 'C', Permission='NOEDIT_MOD' WHERE GroupName = 'C' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'LAST_RESULTS');
UPDATE DeviceGroup SET SystemGroupEnum = 'AB', Permission='NOEDIT_MOD' WHERE GroupName = 'AB' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'LAST_RESULTS');
UPDATE DeviceGroup SET SystemGroupEnum = 'AC', Permission='NOEDIT_MOD' WHERE GroupName = 'AC' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'LAST_RESULTS');
UPDATE DeviceGroup SET SystemGroupEnum = 'BC', Permission='NOEDIT_MOD' WHERE GroupName = 'BC' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'LAST_RESULTS');
UPDATE DeviceGroup SET SystemGroupEnum = 'ABC', Permission='NOEDIT_MOD' WHERE GroupName = 'ABC' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'LAST_RESULTS');
UPDATE DeviceGroup SET SystemGroupEnum = 'UNKNOWN', Permission='NOEDIT_MOD' WHERE GroupName = 'UNKNOWN' AND ParentDeviceGroupId IN (SELECT DeviceGroupId FROM DeviceGroup WHERE SystemGroupEnum = 'LAST_RESULTS');

/* @error ignore-begin */
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'Monitors', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'MONITORS'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'ROOT') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'DeviceData', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'DEVICE_DATA'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'MONITORS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'Outage', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'OUTAGE'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'MONITORS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'Tamper Flag', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'TAMPER_FLAG'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'MONITORS') DG2;

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'Phase Detect', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'PHASE_DETECT'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'SYSTEM_METERS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'Last Results', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'LAST_RESULTS'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'PHASE_DETECT') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'A', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'A'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'B', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'B'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'C', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'C'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'AB', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'AB'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'AC', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'AC'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'BC', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'BC'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'ABC', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'ABC'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'UNKNOWN', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', SYSDATE, 'UNKNOWN'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
/* @error ignore-end */
/* End YUK-12201 */

/* Start YUK-12218 */
/* @error ignore-begin */
ALTER TABLE ArchiveValuesExportFormat
ADD TimeZoneFormat VARCHAR2(20);

UPDATE ArchiveValuesExportFormat
SET TimeZoneFormat = 'LOCAL'
WHERE TimeZoneFormat IS NULL;

ALTER TABLE ArchiveValuesExportFormat
MODIFY TimeZoneFormat VARCHAR2(20) NOT NULL;

ALTER TABLE ArchiveValuesExportFormat
MODIFY Delimiter VARCHAR2(20) NULL;
/* @error ignore-end */
/* End YUK-12218 */

/* Start YUK-12233 */
DELETE FROM YukonGroupRole    WHERE RolePropertyId = -10811;
DELETE FROM YukonRoleProperty WHERE RolePropertyId = -10811;
/* End YUK-12233 */

/* Start YUK-12160 */
CREATE TABLE UserPreference (
    PreferenceId         NUMBER                NOT NULL,
    UserId               NUMBER                NOT NULL,
    Name                 VARCHAR2(255)         NOT NULL,
    Value                VARCHAR2(255)         NOT NULL,
    CONSTRAINT PK_UserPreference PRIMARY KEY (PreferenceId)
);

CREATE UNIQUE INDEX Indx_UserPref_UserId_Name_UNQ ON UserPreference (
UserId ASC,
Name ASC);

ALTER TABLE UserPreference
    ADD CONSTRAINT FK_UserPreference_YukonUser FOREIGN KEY (UserId)
        REFERENCES YukonUser (UserID)
            ON DELETE CASCADE;
/* End YUK-12160 */
            
/* Start YUK-12291 */
ALTER TABLE FileExportHistory MODIFY ExportPath VARCHAR2(300) NULL;

INSERT INTO JobProperty (JobPropertyId, JobId, Name, Value)
   (SELECT MAX(JP2.JobPropertyId) + ROW_NUMBER() OVER(ORDER BY J.JobId) As JobPropertyId, J.JobId, 'timestampPatternField', '_yyyyMMddHHmmss'
    FROM Job J
    JOIN JobProperty JP ON J.JobId = JP.JobId
    JOIN JobProperty JP2 ON 1 = 1
    WHERE J.Disabled IN ('Y', 'N')
      AND J.BeanName IN ('scheduledArchivedDataFileExportJobDefinition',
                         'scheduledBillingFileExportJobDefinition',
                         'scheduledMeterEventsFileExportJobDefinition',
                         'scheduledWaterLeakFileExportJobDefinition')
      AND NOT EXISTS (
        SELECT JobId FROM JobProperty JP
        WHERE Name = 'timestampPatternField'
          AND J.JobId = JP.JobId)
    GROUP By J.JobId);

INSERT INTO JobProperty (JobPropertyId, JobId, Name, Value)
   (SELECT MAX(JP2.JobPropertyId) + ROW_NUMBER() OVER(ORDER BY J.JobId) As JobPropertyId, J.JobId, 'includeExportCopy', 'true'
    FROM Job J
    JOIN JobProperty JP ON J.JobId = JP.JobId
    JOIN JobProperty JP2 ON 1 = 1
    WHERE J.Disabled IN ('Y', 'N')
      AND J.BeanName IN ('scheduledArchivedDataFileExportJobDefinition',
                         'scheduledBillingFileExportJobDefinition',
                         'scheduledMeterEventsFileExportJobDefinition',
                         'scheduledWaterLeakFileExportJobDefinition')
      AND NOT EXISTS (
        SELECT JobId FROM JobProperty JP
        WHERE Name = 'includeExportCopy'
          AND J.JobId = JP.JobId)
    GROUP By J.JobId);

INSERT INTO JobProperty (JobPropertyId, JobId, Name, Value)
   (SELECT MAX(JP2.JobPropertyId) + ROW_NUMBER() OVER(ORDER BY J.JobId) As JobPropertyId, J.JobId, 'overrideFileExtension', 'false'
    FROM Job J
    JOIN JobProperty JP ON J.JobId = JP.JobId
    JOIN JobProperty JP2 ON 1 = 1
    WHERE J.Disabled IN ('Y', 'N')
      AND J.BeanName IN ('scheduledArchivedDataFileExportJobDefinition',
                         'scheduledBillingFileExportJobDefinition',
                         'scheduledMeterEventsFileExportJobDefinition',
                         'scheduledWaterLeakFileExportJobDefinition')
      AND NOT EXISTS (
        SELECT JobId FROM JobProperty JP
        WHERE Name = 'overrideFileExtension'
          AND J.JobId = JP.JobId)
    GROUP By J.JobId);
/* End YUK-12291 */

/* Start YUK-12240 */
/* @start-block */
DECLARE 
    v_channel   NUMBER;
    v_numItems  NUMBER;
    v_itemId    NUMBER;
    v_newItemId NUMBER;
BEGIN
    v_channel := 1;
    WHILE (v_channel < 5)
    LOOP
        SELECT COUNT(DCI.DeviceConfigurationItemId) INTO v_numItems
        FROM DeviceConfigurationItem DCI 
        JOIN DeviceConfiguration DC ON DCI.DeviceConfigurationId = DC.DeviceConfigurationId
        WHERE LOWER(DCI.FieldName) = 'channel config ' || v_channel
        AND DC.Type IN ('MCT470', 'MCT430');

        IF v_numItems > 0
        THEN
            DECLARE
                v_oldValue NUMBER;
                CURSOR curs_itemIds IS (
                    SELECT DCI.DeviceConfigurationItemId
                    FROM DeviceConfigurationItem DCI 
                    JOIN DeviceConfiguration DC ON DCI.DeviceConfigurationId = DC.DeviceConfigurationId
                    WHERE LOWER(DCI.FieldName) = 'channel config ' || v_channel
                      AND DC.Type IN ('MCT470', 'MCT430'));
            BEGIN
                OPEN curs_itemIds;
                LOOP
                    FETCH curs_itemIds INTO v_itemId;
                    EXIT WHEN curs_itemIds%NOTFOUND;

                    SELECT MAX(DeviceConfigurationItemId) + 1 INTO v_newItemId FROM DeviceConfigurationItem;

                    SELECT CAST(Value AS NUMBER) INTO v_oldValue
                    FROM DeviceConfigurationItem 
                    WHERE DeviceConfigurationItemId = v_itemId;

                    INSERT INTO DeviceConfigurationItem VALUES (
                        v_newItemId,
                       (SELECT DeviceConfigurationId 
                        FROM DeviceConfigurationItem 
                        WHERE DeviceConfigurationItemId = v_itemId),
                        'channel ' || v_channel || ' type',
                        BITAND(v_oldValue, 3));

                    INSERT INTO DeviceConfigurationItem VALUES (
                        v_newItemId + 1,
                       (SELECT DeviceConfigurationId 
                        FROM DeviceConfigurationItem 
                        WHERE DeviceConfigurationItemId = v_itemId),
                        'channel ' || v_channel || ' physical channel',
                        BITAND((v_oldValue/4), 15));

                    DELETE FROM DeviceConfigurationItem WHERE DeviceConfigurationItemid = v_itemId;
                END LOOP;
                CLOSE curs_itemIds;
            END;
        END IF;
        v_channel := v_channel + 1;
    END LOOP;
END;
/
/* @end-block */

/* @start-block */
DECLARE 
    v_numItems  NUMBER;
    v_itemId    NUMBER;
    v_newItemId NUMBER;
BEGIN
    SELECT COUNT(DCI.DeviceConfigurationItemId) INTO v_numItems
    FROM DeviceConfigurationItem DCI 
    JOIN DeviceConfiguration DC ON DC.DeviceConfigurationId = DCI.DeviceConfigurationId
    WHERE LOWER(DCI.FieldName) = 'configuration'
      AND DC.Type = 'MCT470';

    IF v_numItems > 0
    THEN
        DECLARE
            v_oldValue NUMBER;
            CURSOR curs_itemIds IS (
                SELECT DCI.DeviceConfigurationItemId
                FROM DeviceConfigurationItem DCI 
                JOIN DeviceConfiguration DC ON DCI.DeviceConfigurationId = DC.DeviceConfigurationId
                WHERE LOWER(DCI.FieldName) = 'configuration'
                  AND DC.Type = 'MCT470');
        BEGIN
            OPEN curs_itemIds;
            LOOP
                FETCH curs_itemIds INTO v_itemId;
                EXIT WHEN curs_itemIds%NOTFOUND;
                
                SELECT MAX(DeviceConfigurationItemId) + 1 INTO v_newItemId FROM DeviceConfigurationItem;
                
                SELECT CAST(Value AS NUMBER) INTO v_oldValue
                FROM DeviceConfigurationItem 
                WHERE DeviceConfigurationItemId = v_itemId;

                INSERT INTO DeviceConfigurationItem VALUES (
                    v_newItemId,
                   (SELECT DeviceConfigurationId 
                    FROM DeviceConfigurationItem 
                    WHERE DeviceConfigurationItemId = v_itemId),
                    'enable dst',
                    BITAND(v_oldValue, 1));

                INSERT INTO DeviceConfigurationItem VALUES (
                    v_newItemId + 1,
                   (SELECT DeviceConfigurationId 
                    FROM DeviceConfigurationItem 
                    WHERE DeviceConfigurationItemId = v_itemId),
                    'electronic meter',
                    BITAND((v_oldValue/16), 15));

                DELETE FROM DeviceConfigurationItem WHERE DeviceConfigurationItemid = v_itemId;
            END LOOP;
            CLOSE curs_itemIds;
        END;
    END IF;
END;
/
/* @end-block */

/* @start-block */
DECLARE 
    v_numItems  NUMBER;
    v_itemId    NUMBER;
    v_newItemId NUMBER;
BEGIN
    SELECT COUNT(DCI.DeviceConfigurationItemId) INTO v_numItems
    FROM DeviceConfigurationItem DCI 
    JOIN DeviceConfiguration DC ON DC.DeviceConfigurationId = DCI.DeviceConfigurationId
    WHERE LOWER(DCI.FieldName) = 'configuration'
      AND DC.Type = 'MCT430';

    IF v_numItems > 0
    THEN
        DECLARE
            v_oldValue NUMBER;
            CURSOR curs_itemIds IS (
                SELECT DCI.DeviceConfigurationItemId
                FROM DeviceConfigurationItem DCI 
                JOIN DeviceConfiguration DC ON DCI.DeviceConfigurationId = DC.DeviceConfigurationId
                WHERE LOWER(DCI.FieldName) = 'configuration'
                  AND DC.Type = 'MCT430');
        BEGIN
            OPEN curs_itemIds;
            LOOP
                FETCH curs_itemIds INTO v_itemId;
                EXIT WHEN curs_itemIds%NOTFOUND;

                SELECT MAX(DeviceConfigurationItemId) + 1 INTO v_newItemId FROM DeviceConfigurationItem;

                SELECT CAST(Value AS NUMBER) INTO v_oldValue
                FROM DeviceConfigurationItem 
                WHERE DeviceConfigurationItemId = v_itemId;

                INSERT INTO DeviceConfigurationItem VALUES (
                    v_newItemId,
                   (SELECT DeviceConfigurationId 
                    FROM DeviceConfigurationItem 
                    WHERE DeviceConfigurationItemId = v_itemId),
                    'enable dst',
                    BITAND(v_oldValue, 1));

                DELETE FROM DeviceConfigurationItem WHERE DeviceConfigurationItemid = v_itemId;
            END LOOP;
            CLOSE curs_itemIds;
        END;
    END IF;
END;
/
/* @end-block */

CREATE TABLE DeviceConfigConverter_temp (
    CategoryType VARCHAR2(60) NOT NULL,
    NewFieldName VARCHAR2(60) NOT NULL,
    OldFieldName VARCHAR2(60) NOT NULL,
    CONSTRAINT PK_DeviceConfigConverter PRIMARY KEY (CategoryType, NewFieldName)
);

INSERT INTO DeviceConfigConverter_temp VALUES ('timeZone', 'timeZoneOffset', 'time zone offset');

INSERT INTO DeviceConfigConverter_temp VALUES ('meterParameters', 'lcdCycleTime', 'lcd cycle time');
INSERT INTO DeviceConfigConverter_temp VALUES ('meterParameters', 'displayDigits', 'display digits');
INSERT INTO DeviceConfigConverter_temp VALUES ('meterParameters', 'disconnectDisplayDisabled', 'disconnect display disabled');

INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem1', 'Display Item 1');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem2', 'Display Item 2');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem3', 'Display Item 3');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem4', 'Display Item 4');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem5', 'Display Item 5');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem6', 'Display Item 6');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem7', 'Display Item 7');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem8', 'Display Item 8');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem9', 'Display Item 9');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem10', 'Display Item 10');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem11', 'Display Item 11');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem12', 'Display Item 12');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem13', 'Display Item 13');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem14', 'Display Item 14');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem15', 'Display Item 15');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem16', 'Display Item 16');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem17', 'Display Item 17');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem18', 'Display Item 18');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem19', 'Display Item 19');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem20', 'Display Item 20');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem21', 'Display Item 21');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem22', 'Display Item 22');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem23', 'Display Item 23');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem24', 'Display Item 24');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem25', 'Display Item 25');
INSERT INTO DeviceConfigConverter_temp VALUES ('displayItems', 'displayItem26', 'Display Item 26');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Configuration', 'enableDst', 'enable dst');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Configuration', 'timeAdjustTolerance', 'time adjust tolerance');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Addressing', 'bronzeAddress', 'bronze address');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Addressing', 'leadAddress', 'lead address');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Addressing', 'collectionAddress', 'collection address');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Addressing', 'serviceProviderId', 'service provider id');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct440PhaseLoss', 'phaseLossThreshold', 'phase loss threshold');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440PhaseLoss', 'phaseLossDuration', 'phase loss duration');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1rate0', 'schedule1rate0');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1rate1', 'schedule1rate1');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1rate2', 'schedule1rate2');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1rate3', 'schedule1rate3');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1rate4', 'schedule1rate4');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1rate5', 'schedule1rate5');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1rate6', 'schedule1rate6');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1rate7', 'schedule1rate7');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1rate8', 'schedule1rate8');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1rate9', 'schedule1rate9');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1time0', 'schedule1time0');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1time1', 'schedule1time1');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1time2', 'schedule1time2');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1time3', 'schedule1time3');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1time4', 'schedule1time4');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1time5', 'schedule1time5');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1time6', 'schedule1time6');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1time7', 'schedule1time7');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1time8', 'schedule1time8');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule1time9', 'schedule1time9');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2rate0', 'schedule2rate0');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2rate1', 'schedule2rate1');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2rate2', 'schedule2rate2');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2rate3', 'schedule2rate3');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2rate4', 'schedule2rate4');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2rate5', 'schedule2rate5');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2rate6', 'schedule2rate6');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2rate7', 'schedule2rate7');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2rate8', 'schedule2rate8');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2rate9', 'schedule2rate9');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2time0', 'schedule2time0');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2time1', 'schedule2time1');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2time2', 'schedule2time2');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2time3', 'schedule2time3');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2time4', 'schedule2time4');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2time5', 'schedule2time5');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2time6', 'schedule2time6');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2time7', 'schedule2time7');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2time8', 'schedule2time8');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule2time9', 'schedule2time9');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3rate0', 'schedule3rate0');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3rate1', 'schedule3rate1');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3rate2', 'schedule3rate2');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3rate3', 'schedule3rate3');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3rate4', 'schedule3rate4');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3rate5', 'schedule3rate5');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3rate6', 'schedule3rate6');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3rate7', 'schedule3rate7');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3rate8', 'schedule3rate8');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3rate9', 'schedule3rate9');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3time0', 'schedule3time0');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3time1', 'schedule3time1');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3time2', 'schedule3time2');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3time3', 'schedule3time3');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3time4', 'schedule3time4');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3time5', 'schedule3time5');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3time6', 'schedule3time6');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3time7', 'schedule3time7');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3time8', 'schedule3time8');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule3time9', 'schedule3time9');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4rate0', 'schedule4rate0');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4rate1', 'schedule4rate1');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4rate2', 'schedule4rate2');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4rate3', 'schedule4rate3');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4rate4', 'schedule4rate4');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4rate5', 'schedule4rate5');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4rate6', 'schedule4rate6');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4rate7', 'schedule4rate7');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4rate8', 'schedule4rate8');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4rate9', 'schedule4rate9');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4time0', 'schedule4time0');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4time1', 'schedule4time1');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4time2', 'schedule4time2');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4time3', 'schedule4time3');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4time4', 'schedule4time4');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4time5', 'schedule4time5');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4time6', 'schedule4time6');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4time7', 'schedule4time7');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4time8', 'schedule4time8');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'schedule4time9', 'schedule4time9');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'holiday', 'holiday');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'weekday', 'weekdays');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'saturday', 'saturday');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct440Tou', 'sunday', 'sunday');

INSERT INTO DeviceConfigConverter_temp VALUES ('dnp', 'internalRetries', 'Internal Retries');
INSERT INTO DeviceConfigConverter_temp VALUES ('dnp', 'localTime', 'Local Time');
INSERT INTO DeviceConfigConverter_temp VALUES ('dnp', 'enableDnpTimesyncs', 'Enable DNP Timesyncs');
INSERT INTO DeviceConfigConverter_temp VALUES ('dnp', 'omitTimeRequest', 'Omit Time Request');
INSERT INTO DeviceConfigConverter_temp VALUES ('dnp', 'enableUnsolicitedMessages', 'Enable Unsolicited Messages');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct470DemandLoadProfile', 'demandInterval', 'demand interval');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470DemandLoadProfile', 'loadProfileInterval1', 'load profile interval 1');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel1Type', 'channel 1 type');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel1PhysicalChannel', 'channel 1 physical channel');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel1LoadProfileResolution', 'channel 1 load profile resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel1PeakKWResolution', 'channel 1 peak kw resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel1LastIntervalDemandResolution', 'channel 1 last interval demand resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel1Multiplier', 'channel 1 multiplier');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel2Type', 'channel 2 type');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel2PhysicalChannel', 'channel 2 physical channel');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel2LoadProfileResolution', 'channel 2 load profile resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel2PeakKWResolution', 'channel 2 peak kw resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel2LastIntervalDemandResolution', 'channel 2 last interval demand resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel2Multiplier', 'channel 2 multiplier');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel3Type', 'channel 3 type');                        
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel3PhysicalChannel', 'channel 3 physical channel'); 
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel3LoadProfileResolution', 'channel 3 load profile resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel3PeakKWResolution', 'channel 3 peak kw resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel3LastIntervalDemandResolution', 'channel 3 last interval demand resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel3Multiplier', 'channel 3 multiplier');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel4Type', 'channel 4 type');                         
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel4PhysicalChannel', 'channel 4 physical channel');  
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel4LoadProfileResolution', 'channel 4 load profile resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel4PeakKWResolution', 'channel 4 peak kw resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel4LastIntervalDemandResolution', 'channel 4 last interval demand resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470LoadProfileChannels', 'channel4Multiplier', 'channel 4 multiplier');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct470ConfigurationByte', 'enableDst', 'enable dst');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470ConfigurationByte', 'electronicMeter', 'electronic meter');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470ConfigurationByte', 'timeAdjustTolerance', 'time adjust tolerance');

INSERT INTO DeviceConfigConverter_temp VALUES ('addressing', 'serviceProviderId', 'service provider id');

INSERT INTO DeviceConfigConverter_temp VALUES ('relays', 'relayATimer', 'relay a timer');
INSERT INTO DeviceConfigConverter_temp VALUES ('relays', 'relayBTimer', 'relay b timer');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct470PrecannedTable', 'tableReadInterval', 'table read interval');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470PrecannedTable', 'meterNumber', 'meter number');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct470PrecannedTable', 'tableType', 'table type');

INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule1rate0', 'schedule1rate0');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule1rate1', 'schedule1rate1');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule1rate2', 'schedule1rate2');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule1rate3', 'schedule1rate3');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule1rate4', 'schedule1rate4');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule1rate5', 'schedule1rate5');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule1time0', 'schedule1time0');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule1time1', 'schedule1time1');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule1time2', 'schedule1time2');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule1time3', 'schedule1time3');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule1time4', 'schedule1time4');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule1time5', 'schedule1time5');

INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule2rate0', 'schedule2rate0');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule2rate1', 'schedule2rate1');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule2rate2', 'schedule2rate2');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule2rate3', 'schedule2rate3');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule2rate4', 'schedule2rate4');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule2rate5', 'schedule2rate5');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule2time0', 'schedule2time0');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule2time1', 'schedule2time1');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule2time2', 'schedule2time2');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule2time3', 'schedule2time3');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule2time4', 'schedule2time4');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule2time5', 'schedule2time5');

INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule3rate0', 'schedule3rate0');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule3rate1', 'schedule3rate1');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule3rate2', 'schedule3rate2');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule3rate3', 'schedule3rate3');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule3rate4', 'schedule3rate4');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule3rate5', 'schedule3rate5');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule3time0', 'schedule3time0');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule3time1', 'schedule3time1');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule3time2', 'schedule3time2');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule3time3', 'schedule3time3');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule3time4', 'schedule3time4');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule3time5', 'schedule3time5');

INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule4rate0', 'schedule4rate0');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule4rate1', 'schedule4rate1');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule4rate2', 'schedule4rate2');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule4rate3', 'schedule4rate3');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule4rate4', 'schedule4rate4');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule4rate5', 'schedule4rate5');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule4time0', 'schedule4time0');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule4time1', 'schedule4time1');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule4time2', 'schedule4time2');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule4time3', 'schedule4time3');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule4time4', 'schedule4time4');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'schedule4time5', 'schedule4time5');

INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'defaultRate', 'default rate');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'holiday', 'holiday');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'monday', 'monday');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'tuesday', 'tuesday');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'wednesday', 'wednesday');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'thursday', 'thursday');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'friday', 'friday');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'saturday', 'saturday');
INSERT INTO DeviceConfigConverter_temp VALUES ('tou', 'sunday', 'sunday');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct430DemandLoadProfile', 'demandInterval', 'demand interval');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430DemandLoadProfile', 'loadProfileInterval1', 'load profile interval 1');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel1Type', 'channel 1 type');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel1PhysicalChannel', 'channel 1 physical channel');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel1LoadProfileResolution', 'channel 1 load profile resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel1PeakKWResolution', 'channel 1 peak kw resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel1LastIntervalDemandResolution', 'channel 1 last interval demand resolution');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel2Type', 'channel 2 type');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel2PhysicalChannel', 'channel 2 physical channel');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel2LoadProfileResolution', 'channel 2 load profile resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel2PeakKWResolution', 'channel 2 peak kw resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel2LastIntervalDemandResolution', 'channel 2 last interval demand resolution');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel3Type', 'channel 3 type');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel3PhysicalChannel', 'channel 3 physical channel');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel3LoadProfileResolution', 'channel 3 load profile resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel3PeakKWResolution', 'channel 3 peak kw resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel3LastIntervalDemandResolution', 'channel 3 last interval demand resolution');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel4Type', 'channel 4 type');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel4PhysicalChannel', 'channel 4 physical channel');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel4LoadProfileResolution', 'channel 4 load profile resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel4PeakKWResolution', 'channel 4 peak kw resolution');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430LoadProfileChannels', 'channel4LastIntervalDemandResolution', 'channel 4 last interval demand resolution');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct430ConfigurationByte', 'enableDst', 'enable dst');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430ConfigurationByte', 'timeAdjustTolerance', 'time adjust tolerance');

INSERT INTO DeviceConfigConverter_temp VALUES ('mct430PrecannedTable', 'tableReadInterval', 'table read interval');
INSERT INTO DeviceConfigConverter_temp VALUES ('mct430PrecannedTable', 'tableType', 'table type');

CREATE TABLE TypeToCategoryConverter_temp (
    ConfigType VARCHAR2(60),
    CategoryType VARCHAR2(60),
    CONSTRAINT PK_TypeToCategoryConverter PRIMARY KEY (ConfigType, CategoryType)
);

INSERT INTO TypeToCategoryConverter_temp VALUES ('DNP', 'dnp');

INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT420', 'meterParameters');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT420', 'displayItems');

INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT430', 'timeZone');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT430', 'addressing');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT430', 'mct430DemandLoadProfile');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT430', 'mct430LoadProfileChannels');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT430', 'mct430ConfigurationByte');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT430', 'mct430PrecannedTable');

INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT440', 'timeZone');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT440', 'mct440Configuration');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT440', 'mct440PhaseLoss');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT440', 'mct440Addressing');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT440', 'mct440Tou');

INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT470', 'timeZone');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT470', 'addressing');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT470', 'mct470DemandLoadProfile');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT470', 'mct470LoadProfileChannels');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT470', 'mct470ConfigurationByte');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT470', 'mct470PrecannedTable');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT470', 'relays');
INSERT INTO TypeToCategoryConverter_temp VALUES ('MCT470', 'tou');

CREATE TABLE ConfigTypeToDeviceType_temp (
    ConfigType VARCHAR2(60),
    PaoType    VARCHAR2(60),
    CONSTRAINT PK_ConfigTypeToDeviceTypeTemp PRIMARY KEY (ConfigType, PaoType)
);

INSERT INTO ConfigTypeToDeviceType_temp VALUES ('DNP', 'RTU-DNP');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('DNP', 'RTU-DART');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('DNP', 'CBC DNP');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('DNP', 'CBC 6510');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('DNP', 'CBC 7020');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('DNP', 'CBC 7022');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('DNP', 'CBC 7023');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('DNP', 'CBC 7024');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('DNP', 'CBC 8020');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('DNP', 'CBC 8024');

INSERT INTO ConfigTypeToDeviceType_temp VALUES ('MCT420', 'MCT-420cL');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('MCT420', 'MCT-420cD');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('MCT420', 'MCT-420fL');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('MCT420', 'MCT-420fD');

INSERT INTO ConfigTypeToDeviceType_temp VALUES ('MCT430', 'MCT-430A');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('MCT430', 'MCT-430A3');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('MCT430', 'MCT-430S4');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('MCT430', 'MCT-430SL');

INSERT INTO ConfigTypeToDeviceType_temp VALUES ('MCT440', 'MCT-440-2131B');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('MCT440', 'MCT-440-2132B');
INSERT INTO ConfigTypeToDeviceType_temp VALUES ('MCT440', 'MCT-440-2133B');

INSERT INTO ConfigTypeToDeviceType_temp VALUES ('MCT470', 'MCT-470');

CREATE TABLE DeviceConfigDeviceTypes (
   DeviceConfigDeviceTypeId NUMBER              NOT NULL,
   DeviceConfigurationId NUMBER              NULL,
   PaoType              VARCHAR2(30)          NOT NULL,
   CONSTRAINT PK_DeviceConfigDeviceTypes PRIMARY KEY (DeviceConfigDeviceTypeId)
);

ALTER TABLE DeviceConfigDeviceTypes
   ADD CONSTRAINT AK_DevConDevTypes_CatIdDevType UNIQUE (DeviceConfigurationId, PaoType);

ALTER TABLE DeviceConfigDeviceTypes
   ADD CONSTRAINT FK_DevConfDevTypes_DevConfig FOREIGN KEY (DeviceConfigurationId)
      REFERENCES DeviceConfiguration (DeviceConfigurationId)
         ON DELETE CASCADE;

INSERT INTO DeviceConfigDeviceTypes (DeviceConfigDeviceTypeId, DeviceConfigurationId, PaoType)
    SELECT ROW_NUMBER() OVER (ORDER BY DC.DeviceConfigurationId),
        DC.DeviceConfigurationId,
        T.PaoType
    FROM DeviceConfiguration DC 
    JOIN ConfigTypeToDeviceType_temp T ON DC.Type = T.ConfigType;

CREATE TABLE DeviceConfigCategory (
   DeviceConfigCategoryId NUMBER              NOT NULL,
   CategoryType         VARCHAR2(60)          NOT NULL,
   Name                 VARCHAR2(100)          NOT NULL,
   CONSTRAINT PK_DeviceConfigCategory PRIMARY KEY (DeviceConfigCategoryId)
);

CREATE TABLE DeviceConfigCategoryItem (
   DeviceConfigurationItemId NUMBER              NOT NULL,
   DeviceConfigCategoryId NUMBER              NULL,
   ItemName             VARCHAR2(60)          NOT NULL,
   ItemValue            VARCHAR2(60)          NOT NULL,
   CONSTRAINT PK_DeviceConfigCategoryItem PRIMARY KEY (DeviceConfigurationItemId)
);

ALTER TABLE DeviceConfigCategoryItem
   ADD CONSTRAINT AK_DevConCatItem_CatIdItemName UNIQUE (DeviceConfigCategoryId, ItemName);

ALTER TABLE DeviceConfigCategoryItem
   ADD CONSTRAINT FK_DevConfCatItem_DevConfCat FOREIGN KEY (DeviceConfigCategoryId)
      REFERENCES DeviceConfigCategory (DeviceConfigCategoryId)
         ON DELETE CASCADE;

CREATE TABLE DeviceConfigCategoryMap (
   DeviceConfigurationId NUMBER              NOT NULL,
   DeviceConfigCategoryId NUMBER              NOT NULL,
   CONSTRAINT PK_DeviceConfigCategoryMap PRIMARY KEY (DeviceConfigurationId, DeviceConfigCategoryId)
);

ALTER TABLE DeviceConfigCategoryMap
   ADD CONSTRAINT FK_DevConCatMap_DevCon FOREIGN KEY (DeviceConfigurationId)
      REFERENCES DeviceConfiguration (DeviceConfigurationId)
         ON DELETE CASCADE;

ALTER TABLE DeviceConfigCategoryMap
   ADD CONSTRAINT FK_DevConfCatMap_DevConfCat FOREIGN KEY (DeviceConfigCategoryId)
      REFERENCES DeviceConfigCategory (DeviceConfigCategoryId)
         ON DELETE CASCADE;

CREATE TABLE ConfigToCategory_temp (
    DeviceConfigCategoryId NUMBER NOT NULL,
    DeviceConfigurationId NUMBER NOT NULL,
    CategoryType VARCHAR2(60) NOT NULL,
    CategoryName VARCHAR2(100) NOT NULL
);

INSERT INTO ConfigToCategory_temp (DeviceConfigCategoryId, DeviceConfigurationId, CategoryType, CategoryName)
    SELECT ROW_NUMBER() OVER (ORDER BY DC.DeviceConfigurationId),
        DC.DeviceConfigurationId,
        T.CategoryType,
        DC.Name || ' ' || T.CategoryType || ' Category'
    FROM DeviceConfiguration DC 
    JOIN TypeToCategoryConverter_temp T ON DC.Type = T.ConfigType;

INSERT INTO DeviceConfigCategory (DeviceConfigCategoryId, CategoryType, Name)
    SELECT DeviceConfigCategoryId, CategoryType, CategoryName
    FROM ConfigToCategory_temp;

INSERT INTO DeviceConfigCategoryMap (DeviceConfigurationId, DeviceConfigCategoryId)
    SELECT DeviceConfigurationId, DeviceConfigCategoryId
    FROM ConfigToCategory_temp;
    
DELETE FROM DeviceConfigurationItem
WHERE FieldName IN ('Voltage Profile Interval', 'Demand Interval', 'kW/kVar Profile Interval')
  AND DeviceConfigurationId IN (SELECT DeviceConfigurationId 
                                FROM DeviceConfiguration 
                                WHERE Type = 'MCT440');

DELETE FROM DeviceConfigurationItem
WHERE FieldName = 'Meter Number' 
  AND DeviceConfigurationId IN (SELECT DeviceConfigurationId 
                                FROM DeviceConfiguration 
                                WHERE Type = 'MCT430');

UPDATE DeviceConfigurationItem SET Value = 
    CASE
        WHEN FieldName = 'enable dst' AND Value = '1' THEN 'true'
        WHEN FieldName = 'enable dst' AND Value = '0' THEN 'false'
        ELSE Value
    END;

INSERT INTO DeviceConfigCategoryItem (DeviceConfigurationItemId, DeviceConfigCategoryId, ItemName, ItemValue)
    SELECT ROW_NUMBER() OVER (ORDER BY CT.DeviceConfigCategoryId, D.NewFieldName),
        CT.DeviceConfigCategoryId,
        D.NewFieldName,
        DCI.Value
    FROM DeviceConfiguration DC 
        JOIN DeviceConfigurationItem DCI ON DCI.DeviceConfigurationId = DC.DeviceConfigurationId
        JOIN TypeToCategoryConverter_temp T ON DC.Type = T.ConfigType
        JOIN ConfigToCategory_temp CT ON CT.DeviceConfigurationId = DC.DeviceConfigurationId
        JOIN DeviceConfigConverter_temp D ON LOWER(D.OldFieldName) = LOWER(DCI.FieldName)
    WHERE T.CategoryType = D.CategoryType AND CT.CategoryType = T.CategoryType;
        
DROP TABLE DeviceConfigConverter_temp;
DROP TABLE TypeToCategoryConverter_temp;
DROP TABLE ConfigToCategory_temp;
DROP TABLE ConfigTypeToDeviceType_temp;

ALTER TABLE DeviceConfiguration DROP COLUMN Type;

DROP TABLE DeviceConfigurationItem;

ALTER TABLE DEVICECONFIGURATIONDEVICEMAP 
RENAME CONSTRAINT PK_DEVICECONFIGURATIONDEVICEMA 
TO PK_DEVICECONFIGDEVICEMAP;

ALTER TABLE DEVICECONFIGURATIONDEVICEMAP
RENAME CONSTRAINT FK_DEVICECO_REFERENCE_DEVICECO 
TO FK_DEVCONFIGDEVMAP_DEVCONFIG;

ALTER TABLE DEVICECONFIGURATIONDEVICEMAP
RENAME CONSTRAINT FK_DEVICECO_REFERENCE_YUKONPAO 
TO FK_DEVCONFIGDEVMAP_YUKONPAO;
/* End YUK-12240 */

/* Start YUK-12310 */
DELETE FROM DeviceConfigCategory 
WHERE CategoryType = 'mct430DemandLoadProfile';
/* End YUK-12310 */

/* Start YUK-12281 */
CREATE TABLE CommandRequestUnsupported  (
   CommandRequestUnsupportedId NUMBER                          NOT NULL,
   CommandRequestExecId        NUMBER                          NOT NULL,
   DeviceId                    NUMBER                          NOT NULL,
   CONSTRAINT PK_CommandRequestUnsupported PRIMARY KEY (CommandRequestUnsupportedId)
);

ALTER TABLE CommandRequestUnsupported
   ADD CONSTRAINT FK_ComReqUnsupp_ComReqExec FOREIGN KEY (CommandRequestExecId)
      REFERENCES CommandRequestExec (CommandRequestExecId)
         ON DELETE CASCADE;
/* End YUK-12281 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.0', '30-JUN-2013', 'Latest Update', 0, SYSDATE);
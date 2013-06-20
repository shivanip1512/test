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
SET TimeZoneFormat = 'LOCAL';

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
   ADD CONSTRAINT FK_UserPref_YukonUser_UserId FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserID);
/* End YUK-12160 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '30-JUN-2013', 'Latest Update', 0, SYSDATE);*/
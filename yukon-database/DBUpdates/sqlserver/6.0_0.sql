/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12201 */
sp_rename 'DEVICEGROUP', 'DeviceGroup', OBJECT;
GO
sp_rename 'PK_DEVICEGROUP', 'PK_DeviceGroup', OBJECT;
GO
sp_rename 'AK_DEVICEGR_PDG_GN', 'AK_DeviceGroup_ParentDG_GrpNam', OBJECT;
GO
sp_rename 'FK_DEVICEGROUP_DEVICEGROUP', 'FK_DeviceGroup_DeviceGroup', OBJECT;
GO

ALTER TABLE DeviceGroup 
ADD SystemGroupEnum VARCHAR(255);
GO

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
SELECT DG1.DeviceGroupId, 'Monitors', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'MONITORS'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'ROOT') DG2;
      

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'DeviceData', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'DEVICE_DATA'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'MONITORS') DG2;
      

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'Outage', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'OUTAGE'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'MONITORS') DG2;
      

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'Tamper Flag', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'TAMPER_FLAG'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'MONITORS') DG2;

INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'Phase Detect', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'PHASE_DETECT'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'SYSTEM_METERS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'Last Results', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'LAST_RESULTS'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'PHASE_DETECT') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'A', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'A'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'B', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'B'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'C', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'C'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'AB', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'AB'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'AC', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'AC'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'BC', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'BC'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'ABC', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'ABC'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
      
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type, CreatedDate, SystemGroupEnum)
SELECT DG1.DeviceGroupId, 'UNKNOWN', DG2.ParentDeviceGroupId, 'NOEDIT_MOD', 'STATIC', GETDATE(), 'UNKNOWN'
FROM (SELECT MAX(DG.DeviceGroupId) + 1 AS DeviceGroupId
      FROM DeviceGroup DG
      WHERE DG.DeviceGroupId < 100) DG1,
     (SELECT MAX(DG.DeviceGroupId) AS ParentDeviceGroupId
      FROM DeviceGroup DG
      WHERE SystemGroupEnum = 'LAST_RESULTS') DG2;
/* @error ignore-end */
/* End YUK-12201 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '30-JUN-2013', 'Latest Update', 0, GETDATE());*/
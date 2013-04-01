/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12005 */
UPDATE DeviceGroup
SET ParentDeviceGroupId = 0
WHERE GroupName = 'Monitors' AND ParentDeviceGroupId = 1;

UPDATE DeviceDataMonitor
SET GroupName = SUBSTRING(GroupName, 8, LEN(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');
 
UPDATE DeviceGroupComposedGroup
SET GroupName = SUBSTRING(GroupName, 8, LEN(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');
 
UPDATE OutageMonitor
SET GroupName = SUBSTRING(GroupName, 8, LEN(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');
 
UPDATE PorterResponseMonitor
SET GroupName = SUBSTRING(GroupName, 8, LEN(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');
 
UPDATE StatusPointMonitor
SET GroupName = SUBSTRING(GroupName, 8, LEN(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');
 
UPDATE TamperFlagMonitor
SET GroupName = SUBSTRING(GroupName, 8, LEN(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');
 
UPDATE ValidationMonitor
SET GroupName = SUBSTRING(GroupName, 8, LEN(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');

UPDATE JobProperty
SET value = SUBSTRING(value, 8, LEN(value)-7)
WHERE name = 'deviceGroup'
  AND value LIKE ('/Meters/Monitors%');
/* End YUK-12005 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('5.6', '03-MAY-2013', 'Latest Update', 3, GETDATE()); */